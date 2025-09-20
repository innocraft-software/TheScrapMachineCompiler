package com.idt.compiler.common.globalDomain.variableManagement;

import com.idt.compiler.processorAbstraction.application.port.out.persistence.LoadPAModelPort;
import com.idt.compiler.processorAbstraction.domain.registerManagement.FutureRegister;
import com.idt.compiler.processorAbstraction.domain.registerManagement.Register;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class VariableManagementService {
    private final LoadPAModelPort loadPAModelPort;


    private void moveToRam(Variable variable) {
        loadPAModelPort.getProcessorAbstraction().variableToRam(variable);
    }

    private void moveToRegister(Variable variable, Register freeRegister) {
        loadPAModelPort.getProcessorAbstraction().variableToRegister(variable, freeRegister);
    }


    public FutureRegister requestVariable(Variable variable) {
        VariableManagementSession variableManagementSession = loadPAModelPort.getVMS();
        return variableManagementSession.requestVariable(variable);
    }


    public void assignFutureRegisters() {
        VariableManagementSession variableManagementSession = loadPAModelPort.getVMS();

        System.out.println("VariableManagement: Assigning:");
        for (Variable var : variableManagementSession.getVariablesInAssignment().variables()) {
            System.out.println("|----" + var.getName());
        }


        // request re-use
        HashMap<Variable, Register> toBeReUsed = variableManagementSession.requestReUse();

        // perform re-use
        for (Variable variable : toBeReUsed.keySet()) {
            moveToRegister(variable, toBeReUsed.get(variable));
            System.out.println("VariableManagement: re-assigned: " + variable.getName() + " at " + variable.getRegister().getIndex());
        }

        // commit the re-use
        variableManagementSession.commitReUse();


        // request variables to be freed
        List<Variable> toBeMovedToRam = variableManagementSession.freeRequiredSpace();


        // move the variables to ram
        for (Variable variable : toBeMovedToRam) {
            System.out.println(variable.getStatus());
            moveToRam(variable);
        }

        // reserve the memory
        Map<Variable, Register> assignment = variableManagementSession.reserveMemory();

        // perform the assignment
        for (Variable variable : assignment.keySet()) {
            moveToRegister(variable, assignment.get(variable));
        }

        // confirm the assignment
        variableManagementSession.confirmAssignment();


        variableManagementSession.reset();
    }


}
