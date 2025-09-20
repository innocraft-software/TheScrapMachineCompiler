package com.idt.compiler.processorAbstraction.domain.registerManagement;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterTest {

    private final RegisterModule registerModulePlaceholder = RegisterModule.initialize();

    @Test
    void setUpTest() {
        Register register = new Register(1, registerModulePlaceholder);

        assertEquals(RegisterStatus.free, register.getStatus());
        assertTrue(register.isContentEquivalentToRam());
        assertEquals(1, register.getIndex());
        assertNull(register.getVariable());
        assertEquals(0, register.getImmediate());
    }

    @Test
    void setVariableTest() {
        Register register = new Register(1, registerModulePlaceholder);

        Variable variable = new Variable(1, "TEST");

        register.setVariable(variable);

        assertEquals(variable, register.getVariable());
        assertEquals(1, register.getIndex());
        assertEquals(0, register.getImmediate());
        assertEquals(RegisterStatus.variable, register.getStatus());
        assertFalse(register.isContentEquivalentToRam());
    }

    @Test
    void setImmediateTest() {
        Register register = new Register(1, registerModulePlaceholder);

        int immediate = 5;

        register.setImmediate(immediate);

        assertNull(register.getVariable());
        assertEquals(1, register.getIndex());
        assertEquals(immediate, register.getImmediate());
        assertEquals(RegisterStatus.immediate, register.getStatus());
        assertFalse(register.isContentEquivalentToRam());
    }

    @Test
    void variableAfterImmediateTest() {
        Register register = new Register(1, registerModulePlaceholder);

        register.setImmediate(10);

        Variable variable = new Variable(1, "TEST");

        register.free();

        register.setVariable(variable);

        assertEquals(variable, register.getVariable());
        assertEquals(1, register.getIndex());
        assertEquals(0, register.getImmediate());
        assertEquals(RegisterStatus.variable, register.getStatus());
        assertFalse(register.isContentEquivalentToRam());
    }

    @Test
    void immediateAfterVariableTest() {
        Register register = new Register(1, registerModulePlaceholder);

        Variable variable = new Variable(2, "TEST");

        register.setVariable(variable);

        int immediate = 5;

        register.free();

        register.setImmediate(immediate);

        assertNull(register.getVariable());
        assertEquals(1, register.getIndex());
        assertEquals(immediate, register.getImmediate());
        assertEquals(RegisterStatus.immediate, register.getStatus());
        assertFalse(register.isContentEquivalentToRam());
    }

    @Test
    void variableAfterImmediateAlreadyUsedTest() {
        Register register = new Register(1, registerModulePlaceholder);

        register.setImmediate(10);

        Variable variable = new Variable(1, "TEST");

        assertThrows(IllegalCallerException.class, () -> register.setVariable(variable));
    }

    @Test
    void immediateAfterVariableAlreadyUsedTest() {
        Register register = new Register(1, registerModulePlaceholder);

        Variable variable = new Variable(2, "TEST");

        register.setVariable(variable);

        int immediate = 5;

        assertThrows(IllegalCallerException.class, () -> register.setImmediate(immediate));
    }

    @Test
    void freeTest() {
        Register register = new Register(1, registerModulePlaceholder);

        Variable variable = new Variable(2, "TEST");

        register.setVariable(variable);

        register.free();

        assertTrue(register.isContentEquivalentToRam());
    }

    @Test
    void reUseVariableTest() {
        Register register = new Register(1, registerModulePlaceholder);

        Variable variable = new Variable(2, "TEST");

        register.setVariable(variable);

        register.free();

        register.reUse(variable);

        assertEquals(variable, register.getVariable());
        assertEquals(1, register.getIndex());
        assertEquals(0, register.getImmediate());
        assertEquals(RegisterStatus.variable, register.getStatus());
        assertFalse(register.isContentEquivalentToRam());
    }

    @Test
    void reUseImmediateTest() {
        Register register = new Register(1, registerModulePlaceholder);

        int immediate = 5;

        register.setImmediate(immediate);

        register.free();

        register.reUse(immediate);

        assertNull(register.getVariable());
        assertEquals(1, register.getIndex());
        assertEquals(immediate, register.getImmediate());
        assertEquals(RegisterStatus.immediate, register.getStatus());
        assertFalse(register.isContentEquivalentToRam());

    }

    @Test
    void reUseIllegalVariableTest() {
        Register register = new Register(1, registerModulePlaceholder);

        Variable variable = new Variable(2, "TEST");

        register.setVariable(variable);

        register.free();

        Variable incorrectVariable = new Variable(3, "INVALID");

        assertThrows(IllegalCallerException.class, () -> register.reUse(incorrectVariable));
    }

    @Test
    void reUseIllegalImmediateTest() {
        Register register = new Register(1, registerModulePlaceholder);

        int immediate = 5;

        register.setImmediate(immediate);

        register.free();

        int incorrectImmediate = 4;

        assertThrows(IllegalCallerException.class, () -> register.reUse(incorrectImmediate));

    }

    @Test
    void clearTest() {
        Register register = new Register(1, registerModulePlaceholder);

        Variable variable = new Variable(2, "TEST");

        register.setVariable(variable);

        register.free();

        register.setImmediate(5);

        register.free();

        register.clear();

        assertNull(register.getVariable());
        assertEquals(1, register.getIndex());
        assertEquals(0, register.getImmediate());
        assertEquals(RegisterStatus.free, register.getStatus());
        assertTrue(register.isContentEquivalentToRam());
    }

    @Test
    void illegalClearTest() {
        Register register = new Register(1, registerModulePlaceholder);

        Variable variable = new Variable(2, "TEST");

        register.setVariable(variable);

        assertThrows(IllegalCallerException.class, register::clear);
    }

}