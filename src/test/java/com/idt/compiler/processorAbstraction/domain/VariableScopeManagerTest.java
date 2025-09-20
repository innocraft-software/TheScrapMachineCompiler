package com.idt.compiler.processorAbstraction.domain;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.common.globalDomain.variableManagement.VariableScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VariableScopeManagerTest {
    private VariableScopeManager variableScopeManager;

    @BeforeEach
    void setup() {
        this.variableScopeManager = new VariableScopeManager();
    }

    @Test
    void givenNewTVM_whenSessionCreated_thenCreationSuccessfulTest() {
        VariableScope variableScope = variableScopeManager.createScope("Test1");
        assertEquals("Test1", variableScope.getName());
    }

    @Test
    void givenExistingTVM_whenSessionWithDuplicateNameCreated_thenRequestRejectedTest() {
        variableScopeManager.createScope("Test1");
        assertThrows(IllegalCallerException.class, () -> variableScopeManager.createScope("Test1"));
    }

    @Test
    void givenExistingTVM_whenSecondSessionCreated_thenCreationSuccessfulTest() {
        variableScopeManager.createScope("Test1");
        VariableScope variableScope2 = variableScopeManager.createScope("Test2");
        assertEquals("Test2", variableScope2.getName());
    }

    @Test
    void givenSessionFromTVM_whenAddingCopy_thenCopyAdded() {
        VariableScope variableScope = variableScopeManager.createScope("Test1");
        Variable var = new Variable(0, "TestVar");
        variableScopeManager.addVariable(variableScope, var);
        assertTrue(variableScope.getVariables().contains(var));
    }

    @Test
    void givenSessionWithVariable_whenDelete_thenSessionDeletedAndVariablesReturned() {
        VariableScope variableScope = variableScopeManager.createScope("Test1");
        Variable var = new Variable(0, "TestVar");
        variableScopeManager.addVariable(variableScope, var);
        List<Variable> toDelete = variableScopeManager.exitScope(variableScope);
        assertTrue(toDelete.contains(var));
        assertThrows(IllegalCallerException.class, () -> variableScopeManager.exitScope(variableScope));
    }

}