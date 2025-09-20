package com.idt.compiler.processorAbstraction.domain;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.processorAbstraction.domain.registerManagement.Register;
import lombok.Getter;

import java.util.ArrayList;

public class RamManager {

    @Getter
    private final static int TOTAL_RAM = 64;
    private final ArrayList<Integer> allocatedCells;
    private final ArrayList<Variable> variables;

    private RamManager() {
        allocatedCells = new ArrayList<>();
        variables = new ArrayList<>();
    }

    public static RamManager initialize() {
        return new RamManager();
    }

    public Variable allocateVariable(String variableName) throws RamOverflowException {
        for (int cell = 0; cell < TOTAL_RAM; cell++) {
            if (!(allocatedCells.contains(cell))) {
                allocatedCells.add(cell);
                Variable newVariable = new Variable(cell, variableName);
                variables.add(newVariable);
                return newVariable;
            }
        }
        throw new RamOverflowException();
    }

    public void deAllocateVariable(Variable variable) {
        System.out.println("RamManager: Dealloc: " + variable.getName());
        // INV: check that the variable is allocated in this RamManager
        if (!variables.contains(variable) && !variable.isCopy()) {
            throw new IllegalCallerException("RamManager: Attempting to de-allocate variable from ram manager which is not currently allocated");
        }

        // ensure the register holding the variable is clear to ensure the variable never gets referenced from automatic code in the future
        Register associatedRegister = variable.getRegister();
        if (associatedRegister != null) {
            // we can safely free the register as the variable is about to get de allocated anyway
            variable.getRegister().free();
            variable.getRegister().clear();
        }

        // skip copies for ram de allocation as we do not want to delete the ram cell of the source variable
        if (variable.isCopy()) {
            return;
        }

        // free the ram cell
        allocatedCells.remove(Integer.valueOf(variable.getRamCell()));

        // delete the variable
        this.variables.remove(variable);

    }


    // TODO: Remove for debugging
    public void printRam() {
        System.out.println("RamManager: AllocatedCells:");
        for (int index : allocatedCells) {
            System.out.println("- " + index);
        }
        System.out.println("RamManager: AllocatedRam:");
        for (Variable var : variables) {
            System.out.println(var.getName() + " at " + var.getRamCell());
        }
    }

}
