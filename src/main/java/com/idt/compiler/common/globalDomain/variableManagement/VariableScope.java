package com.idt.compiler.common.globalDomain.variableManagement;


import com.idt.compiler.processorAbstraction.domain.VariableScopeManager;

import java.util.ArrayList;
import java.util.List;

public class VariableScope {
    private final String name;

    private final List<Variable> variables;

    private final int scopeDepth;

    public VariableScope(String name, VariableScope previousScope) {
        this.name = name;
        this.variables = new ArrayList<>();

        if (previousScope == null) {
            this.scopeDepth = 0;
        } else {
            this.scopeDepth = previousScope.getScopeDepth() + 1;
        }
    }

    // note the manager is only needed here to prevent accidental calls to this method
    public void addVariable(Variable variable, VariableScopeManager associatedManager) {
        // INV check that the manager exists
        if (!associatedManager.validate(this)) {
            throw new IllegalCallerException("copies can only be added to a Session from the associated manager");
        }

        // INV identifier must be unique in scope
        for (Variable existingVariable : variables) {
            if (existingVariable.getName().equals(variable.getName())) {
                throw new IllegalCallerException("Attempt to create a variable with duplicate name");
            }
        }

        this.variables.add(variable);
    }

    public Variable getVariableByIdentifier(String identifier) {
        for (Variable variable : variables) {
            if (variable.getName().equals(identifier)) {
                return variable;
            }
        }
        throw new IllegalCallerException("Variable with given identifier does not exist in scope: " + identifier);
    }

    public boolean containsVariableIdentifier(String identifier) {
        for (Variable variable : variables) {
            if (variable.getName().equals(identifier)) {
                return true;
            }
        }
        return false;
    }

    public List<Variable> getVariables() {
        return new ArrayList<>(variables);
    }

    public boolean contains(Variable variable) {
        return this.variables.contains(variable);
    }

    public String getName() {
        return this.name;
    }

    public int getScopeDepth() {
        return this.scopeDepth;
    }
}
