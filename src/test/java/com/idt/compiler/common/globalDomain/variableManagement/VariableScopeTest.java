package com.idt.compiler.common.globalDomain.variableManagement;

import com.idt.compiler.processorAbstraction.domain.VariableScopeManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VariableScopeTest {

    @Test
    void givenValidManager_whenAddingCopyVariable_thenCopyAddedTest() {
        VariableScope tvs = new VariableScope("TEST", null);
        Variable var = new Variable(0, "test");

        // mock the tvm
        VariableScopeManager variableScopeManager = mock(VariableScopeManager.class);
        when(variableScopeManager.validate(tvs)).thenReturn(true);

        // note that this method should never be used in this way. It should be called by the manager itself
        tvs.addVariable(var, variableScopeManager);
        assertTrue(tvs.getVariables().contains(var));
    }

    @Test
    void givenIncorrectManager_whenAddingCopyVariable_thenRejectedTest() {
        VariableScope tvs = new VariableScope("TEST", null);
        Variable var = new Variable(0, "test");

        // mock the tvm
        VariableScopeManager variableScopeManager = mock(VariableScopeManager.class);
        when(variableScopeManager.validate(tvs)).thenReturn(false);

        // note that this method should never be used in this way. It should be called by the manager itself
        assertThrows(IllegalCallerException.class, () -> tvs.addVariable(var, variableScopeManager));
    }

    @Test
    void getNameTest() {
        VariableScope tvs = new VariableScope("TEST", null);
        assertEquals("TEST", tvs.getName());
    }

    @Test
    void containsTest() {
        VariableScope tvs = new VariableScope("TEST", null);
        Variable var = new Variable(0, "test");

        assertFalse(tvs.contains(var));

        // mock the tvm
        VariableScopeManager variableScopeManager = mock(VariableScopeManager.class);
        when(variableScopeManager.validate(tvs)).thenReturn(true);

        tvs.addVariable(var, variableScopeManager);

        assertTrue(tvs.contains(var));

    }
}