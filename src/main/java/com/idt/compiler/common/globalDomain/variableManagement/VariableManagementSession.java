package com.idt.compiler.common.globalDomain.variableManagement;

import com.idt.compiler.processorAbstraction.domain.registerManagement.FutureRegister;
import com.idt.compiler.processorAbstraction.domain.registerManagement.Register;
import com.idt.compiler.processorAbstraction.domain.registerManagement.RegisterModule;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class VariableManagementSession {

    protected enum VariableManagementSessionState {
        INITIALIZED,
        AWAITING_REUSE_CONFIRMATION,
        AWAITING_FREEING_REQUIRED_SPACE,
        AWAITING_MEMORY_RESERVATION,
        AWAITING_ASSIGNMENT_CONFIRMATION,
        FINISHED

    }

    private VariableManagementSessionState variableManagementSessionState;

    private final VariablesInAssignment variablesInAssignment;

    private HashMap<Variable, Register> finalAssignment;

    private final RegisterModule registerModule;


    private VariableManagementSession(RegisterModule registerModule) {
        this.registerModule = registerModule;
        this.variablesInAssignment = VariablesInAssignment.initialize();
        reset();
    }

    protected void reset() {
        this.finalAssignment = new HashMap<>();
        this.variableManagementSessionState = VariableManagementSessionState.INITIALIZED;
        this.variablesInAssignment.reset();
    }

    public static VariableManagementSession initialize(RegisterModule registerModule) {
        return new VariableManagementSession(registerModule);
    }


    public FutureRegister requestVariable(Variable variable) {
        // INV: check that the module is not already assigning
        if (this.variableManagementSessionState != VariableManagementSessionState.INITIALIZED) {
            throw new IllegalCallerException("Attempting to request a variable from a module that can not currently accept new requests. Complete the assignment or initialize the module first");
        }

        // add the variable to the request and return the future key
        return this.variablesInAssignment.put(variable);
    }

    protected HashMap<Variable, Register> requestReUse() {

        // INV: check that the module is ready for re-use
        if (this.variableManagementSessionState != VariableManagementSessionState.INITIALIZED) {
            throw new IllegalCallerException("Requested re-use from a session that has already performed re-use assignments");
        }

        HashMap<Variable, Register> toBeReUsed = new HashMap<>();

        for (Variable variable : variablesInAssignment.variables()) {
            // attempt reUse
            Register reUseAttempt = registerModule.attemptReUse(variable);

            if (reUseAttempt != null) {
                // if successful, mark the variable to be re-used (this also unstages it)
                toBeReUsed.put(variable, reUseAttempt);

                // set the assigned register
                variablesInAssignment.setRegister(variable, reUseAttempt);


            }
        }

        // commit un-staging
        this.variablesInAssignment.update();

        this.variableManagementSessionState = VariableManagementSessionState.AWAITING_REUSE_CONFIRMATION;
        return toBeReUsed;
    }

    protected void commitReUse() {
        // INV: check that the system is awaiting a re-use
        if (this.variableManagementSessionState != VariableManagementSessionState.AWAITING_REUSE_CONFIRMATION) {
            throw new IllegalCallerException("Attempting to confirm re-use in session that is not currently awaiting this confirmation.");
        }

        this.variableManagementSessionState = VariableManagementSessionState.AWAITING_FREEING_REQUIRED_SPACE;

    }


    protected List<Variable> freeRequiredSpace() {
        // INV: check that the module is ready for memory reservation
        if (this.variableManagementSessionState != VariableManagementSessionState.AWAITING_FREEING_REQUIRED_SPACE) {
            throw new IllegalCallerException("Attempting to reserve memory in session that is not currently awaiting this step.");
        }

        // check how many memory cells need to be cleaned
        int requiredSpace = this.variablesInAssignment.assignmentSize();
        int currentlyFreeSpace = registerModule.findFreeRegisters().size();


        // clean as many slots as needed
        List<Variable> variablesToArchive = new ArrayList<>();

        for (int count = 0; count < requiredSpace - currentlyFreeSpace; count++) {

            // iterate over all registers holding a variable until an unused is found, then continue with next required slot
            Register[] variableRegisters = registerModule.findVariableRegisters().toArray(new Register[RegisterModule.REGISTER_COUNT]);
            for (Register variableRegister : variableRegisters) {
                // as archiving does not happen immediately, ensure the variable was not already added
                // we only archive if the register is different from ram and the variable is safe to be moved
                if (!variableRegister.isContentEquivalentToRam() && variableRegister.getVariable().isSafeToMoveToRam() && !variablesToArchive.contains(variableRegister.getVariable())) {
                    variablesToArchive.add(variableRegister.getVariable());
                    break;
                }
            }
        }

        this.variableManagementSessionState = VariableManagementSessionState.AWAITING_MEMORY_RESERVATION;
        return variablesToArchive;
    }

    protected Map<Variable, Register> reserveMemory() {
        // INV: check that the module is awaiting memoryReservation
        if (this.variableManagementSessionState != VariableManagementSessionState.AWAITING_MEMORY_RESERVATION) {
            throw new IllegalCallerException("Attempting to reserve memory in session that is not currently awaiting this confirmation.");
        }

        // INV: check that enough registers are now free
        if (this.registerModule.findFreeRegisters().size() < this.variablesInAssignment.assignmentSize()) {
            throw new IllegalCallerException("Not enough registers were freed. There must be an issue in the usage of VariableManagementSession");
        }

        List<Register> freeRegisters = this.registerModule.findFreeRegisters();
        int currentIndex = 0;

        for (Variable variable : variablesInAssignment.variables()) {
            finalAssignment.put(variable, freeRegisters.get(currentIndex++));
        }

        this.variableManagementSessionState = VariableManagementSessionState.AWAITING_ASSIGNMENT_CONFIRMATION;
        return new HashMap<>(finalAssignment);
    }

    protected void confirmAssignment() {
        // INV: check that the module is awaiting assignment confirmation
        if (this.variableManagementSessionState != VariableManagementSessionState.AWAITING_ASSIGNMENT_CONFIRMATION) {
            throw new IllegalCallerException("Attempting to confirm a session that is not currently awaiting this confirmation.");
        }

        for (Variable variable : finalAssignment.keySet()) {
            // un stage the variable
            variablesInAssignment.confirmRegister(variable);
        }
        this.variablesInAssignment.update();

        this.variableManagementSessionState = VariableManagementSessionState.FINISHED;
    }

}
