package com.idt.compiler.common.globalDomain.variableManagement;

import com.idt.compiler.processorAbstraction.domain.registerManagement.FutureRegister;
import com.idt.compiler.processorAbstraction.domain.registerManagement.Register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class VariablesInAssignment {
    private HashMap<Variable, FutureRegister> variablesInAssignment;
    private List<Variable> toBeRemoved;

    private VariablesInAssignment() {
        reset();
    }

    protected static VariablesInAssignment initialize() {
        return new VariablesInAssignment();
    }

    protected void reset() {
        this.variablesInAssignment = new HashMap<>();
        this.toBeRemoved = new ArrayList<>();
    }

    protected FutureRegister put(Variable variable) {
        // allow duplicates by returning a reference to preexisting key
        if (variablesInAssignment.containsKey(variable)) {
            return variablesInAssignment.get(variable);
        } else {
            FutureRegister assignment = new FutureRegister();
            variablesInAssignment.put(variable, assignment);
            return assignment;
        }
    }

    protected Set<Variable> variables() {
        return this.variablesInAssignment.keySet();
    }

    protected void setRegister(Variable variable, Register register) {
        this.variablesInAssignment.get(variable).setReg(register);
        this.toBeRemoved.add(variable);
    }

    protected void confirmRegister(Variable variable) {
        this.setRegister(variable, variable.getRegister());
    }

    protected int assignmentSize() {
        return this.variablesInAssignment.size();
    }

    protected void update() {
        for (Variable var : this.toBeRemoved) {
            this.variablesInAssignment.remove(var);
        }
        this.toBeRemoved.clear();
    }

}
