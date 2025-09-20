package com.idt.compiler.common.globalDomain.variableManagement;

import com.idt.compiler.common.globalDomain.Instruction;
import com.idt.compiler.processorAbstraction.application.port.out.domainConnectors.AddInstructionPort;
import com.idt.compiler.processorAbstraction.domain.RamManager;
import com.idt.compiler.processorAbstraction.domain.VariableScopeManager;
import com.idt.compiler.processorAbstraction.domain.registerManagement.Register;
import com.idt.compiler.processorAbstraction.domain.registerManagement.RegisterModule;
import com.idt.globalDependencies.assemblyProcessor.AssemblyGenerator;
import lombok.Getter;

import java.util.List;

/**
 * Aggregate root
 * this object manages a virtualized simulation of ram and registers
 * this object ensures the states are always consistent
 */
@Getter
public class ProcessorAbstraction {

    private final RegisterModule registerModule;
    private final RamManager ramManager;

    private final VariableScopeManager variableScopeManager;

    private final AddInstructionPort addInstructionPort;

    private ProcessorAbstraction(RegisterModule registerModule, RamManager ramManager, AddInstructionPort addInstructionPort) {
        this.registerModule = registerModule;
        this.ramManager = ramManager;
        this.variableScopeManager = new VariableScopeManager();
        this.addInstructionPort = addInstructionPort;
    }

    public static ProcessorAbstraction initialize(AddInstructionPort addInstructionPort) {
        RegisterModule registerModule = RegisterModule.initialize();
        RamManager ramManager = RamManager.initialize();
        return new ProcessorAbstraction(
                registerModule,
                ramManager,
                addInstructionPort
        );
    }

    public VariableScope getActiveScope() {
        return variableScopeManager.getActiveScope();
    }

    public void inRegisterDeepCopy(Variable source, Variable destination) {
        // INV
        if (source.getRegister() == null) {
            throw new RuntimeException("ProcessorAbstraction: inRegisterDeepCopy requires source to be in register");
        }

        // Destination must be in ram for deep copy to work
        if (destination.getRegister() != null) {
            variableToRam(destination);
        }

        // Archive source but keep register. This ensures the source is not affected
        Register sourceLocation = source.getRegister();
        variableToRam(source);

        // ensure the destination no longer occupies a register. This ensures no orphaned registers remain
        if (destination.getRegister() != null) {
            destination.getRegister().free();
        }

        // assign destination to source register. This assigns the unassigned source register with the source value to the destination variable
        sourceLocation.setVariable(destination);
        destination.setMovedToRegister(sourceLocation);

        // unlock destination. This tells the system that there is no ongoing operation
        destination.unlock();

    }

    public Variable allocateNewVariable(VariableScope scope, String variableName) {
        System.out.println("ProcessorAbstraction: Assigning Variable: " + variableName + " In scope: " + scope.getName());
        // create variable in ram
        Variable newVariable = ramManager.allocateVariable(variableName);

        // add the variable to variable list
        this.variableScopeManager.addVariable(scope, newVariable);

        // return the variable
        return newVariable;
    }

    public Variable allocateTemporaryVariable(VariableScope variableScope, String variableName) {
        // create variable in ram
        Variable newVariable = ramManager.allocateVariable(variableName);

        // add the variable to variable list
        this.variableScopeManager.addVariable(variableScope, newVariable);

        // return the variable
        return newVariable;
    }

    public void variableToRegister(Variable variable, Register register) {
        // INV: variable must be initialized in this abstraction or be a copy
        if (!this.variableScopeManager.variableExists(variable)) {
            throw new IllegalCallerException("ProcessorAbstraction: Attempt to use a variable in incorrect processor abstraction.");
        }

        if (register.getVariable() == variable) {
            // re-use if possible
            reUseRegister(variable, register);
        } else {
            moveToRegister(variable, register);
        }


    }

    private void moveToRegister(Variable variable, Register register) {
        // INV: Reject if the most recent version of the variable already is in a register
        if (variable.getStatus().equals(VarStat.inRegister)) {
            throw new IllegalCallerException("ProcessorAbstraction: Attempting to move variable to register that is already in register");
        }

        // load the variable if it is initialized in memory
        if (variable.isInitialized()) {
            addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.ld(variable.getRamCell(), register.getIndex())));
        }

        // update the register status
        register.setVariable(variable);

        // update the variable status
        variable.setMovedToRegister(register);
    }

    private void reUseRegister(Variable variable, Register registerHoldingVariable) {

        // INV: Check that the register actually holds the var
        if (registerHoldingVariable.getVariable() != variable) {
            throw new IllegalCallerException("ProcessorAbstraction: Illegal Variable re-use detected. The re use register variable stat did not match the variable");
        }

        // update the register status
        registerHoldingVariable.reUse(variable);

        // update the variable status
        variable.setMovedToRegister(registerHoldingVariable);
    }

    public void variableToRam(Variable variable) {
        // INV: do not move to ram if variable set as in ram
        if (variable.getStatus().equals(VarStat.inRam)) {
            throw new IllegalCallerException("ProcessorAbstraction: Attempt to move variable to ram which is already set to ram: " + variable.getName());
        }

        // INV: variable must be initialized in this abstraction.
        if (!this.variableScopeManager.variableExists(variable)) {
            throw new IllegalCallerException("ProcessorAbstraction: Attempt to use a variable in incorrect processor abstraction: " + variable.getName());
        }

        // INV reject the operation if the variable is a copy
        if (variable.isCopy()) {
            throw new IllegalCallerException("ProcessorAbstraction: Attempt to store a copy variable");
        }

        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.str(variable.getRamCell(), variable.getRegister().getIndex())));

        // update the register status
        variable.getRegister().free();

        // update the variable status
        variable.setMovedToRam();
    }

    public VariableScope createScope(String identifier) {
        return this.variableScopeManager.createScope(identifier);
    }

    public void exitScope(VariableScope variableScope) {
        List<Variable> toDelete = this.variableScopeManager.exitScope(variableScope);

        for (Variable currentVarInDeletion : toDelete) {
            this.ramManager.deAllocateVariable(currentVarInDeletion);
        }
    }

    public Variable createTempCopy(VariableScope variableScope, Variable copySource) {
        // INV: variable must be initialized in this abstraction
        if (!this.variableScopeManager.variableExists(copySource)) {
            throw new IllegalCallerException("ProcessorAbstraction: Attempt to copy a variable in incorrect processor abstraction.");
        }

        // INV: ensure the source is not a copy itself
        if (copySource.isCopy()) {
            throw new IllegalCallerException("ProcessorAbstraction: Attempt to copy a copy");
        }

        // ensure the ram cell of the origin variable is up-to date
        if (!copySource.getStatus().equals(VarStat.inRam)) {
            variableToRam(copySource);
        }

        // create the temporaryCopy
        Variable copy = new Variable(copySource);

        // store the copy
        this.variableScopeManager.addVariable(variableScope, copy);

        // return the copy
        return copy;
    }

    public void safeClearAllRegisters() {
        System.out.println("RegisterSafeClear!");
        for (Register register : registerModule.getRegisters()) {

            // if there is a variable that is still assigned ensure it is archived
            if (register.getVariable() != null) {
                if (!register.getVariable().getStatus().equals(VarStat.inRam)) {
                    variableToRam(register.getVariable());
                }
            }

            // clear
            register.clear();
        }
    }


    // TODO: Remove debug tool
    public void printRam() {
        System.out.println("ProcessorAbstraction: RAM DUMP");
        this.ramManager.printRam();
    }


}
