package com.idt.compiler.processorAbstraction.domain;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.common.globalDomain.variableManagement.VariableScope;

import java.util.ArrayList;
import java.util.List;

public class VariableScopeManager {
    private final List<VariableScope> scopeContainer;

    public VariableScopeManager() {
        this.scopeContainer = new ArrayList<>();
    }

    public VariableScope createScope(String identifier) {
        System.out.println("ScopeManager: Created Scope: " + identifier);
        // INV: Session name must be unique
        for (VariableScope session : this.scopeContainer) {
            if (session.getName().equals(identifier)) {
                throw new IllegalCallerException("ScopeManager: Attempt to create session with non unique identifier");
            }
        }

        VariableScope newSession = new VariableScope(identifier, getDeepestScope(this.scopeContainer));
        scopeContainer.add(newSession);
        return newSession;
    }

    public void extendScope(Variable variable, VariableScope destination) {
        // skip if the variable is already part of the new scope
        if (destination.contains(variable)) {
            return;
        }

        // add the variable to the new scope
        destination.addVariable(variable, this);
    }

    public VariableScope getActiveScope() {
        return getDeepestScope(this.scopeContainer);
    }

    private VariableScope getDeepestScope(List<VariableScope> scopeList) {
        // INV: emptyScope
        if (scopeList.size() < 1) {
            return null;
        }

        VariableScope deepestScope = scopeList.get(0);
        for (VariableScope scope : scopeList) {
            if (scope.getScopeDepth() > deepestScope.getScopeDepth()) {
                deepestScope = scope;
            }
        }
        return deepestScope;
    }

    public void addVariable(VariableScope session, Variable variable) {
        // INV only allow adding to sessions which are available in this container
        if (!this.scopeContainer.contains(session)) {
            throw new IllegalCallerException("ScopeManager: Attempt to insert into Temporary Variable Session from incorrect container");
        }

        session.addVariable(variable, this);
    }

    public List<Variable> exitScope(VariableScope session) {

        System.out.println("ScopeManager: Leaving scope: " + session.getName());

        // INV only allow removal of sessions which are available in this container
        if (!this.scopeContainer.contains(session)) {
            throw new IllegalCallerException("Attempt to delete Temporary Variable Session from incorrect container");
        }

        this.scopeContainer.remove(session);

        List<Variable> scopeContent = session.getVariables();
        List<Variable> output = new ArrayList<>();

        System.out.println("ScopeManager: Deallocating:");
        for (Variable variable : scopeContent) {
            // only add variables that are not protected from another scope
            if (!variableExists(variable)) {
                output.add(variable);
                System.out.println("|----" + variable.getName());
            }
        }

        return output;

    }

    public boolean variableExists(Variable variable) {
        for (VariableScope variableScope : this.scopeContainer) {
            if (variableScope.contains(variable)) {
                return true;
            }
        }

        return false;
    }

    /**
     * returns the variable with the given identifier. If the identifier exists multiple times, the deepest one will be returned
     **/
    public Variable getVariableByIdentifier(String identifier) {

        // find all scopes holding the variable
        List<VariableScope> scopesWithVariable = new ArrayList<>();
        for (VariableScope scope : this.scopeContainer) {
            if (scope.containsVariableIdentifier(identifier)) {
                scopesWithVariable.add(scope);
            }
        }

        VariableScope deepestScope = getDeepestScope(scopesWithVariable);

        // INV: no variable found
        if (deepestScope == null) {
            throw new IllegalCallerException("ScopeManager: Variable with identifier does not exist: " + identifier);
        }

        return deepestScope.getVariableByIdentifier(identifier);
    }

    // this method is here to check if a session is permitted to actually add a variable
    public boolean validate(VariableScope session) {
        return this.scopeContainer.contains(session);
    }
}
