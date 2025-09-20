package com.idt.compiler.common.globalDomain.variableManagement;

import com.idt.compiler.processorAbstraction.domain.registerManagement.Register;
import com.idt.compiler.processorAbstraction.domain.registerManagement.RegisterModule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VariableTest {

    private final RegisterModule registerModulePlaceholder = RegisterModule.initialize();

    @Test
    void createTest() {
        Variable variable = new Variable(1, "TEST");
        assertEquals("TEST", variable.getName());
        assertFalse(variable.isInitialized());
        assertFalse(variable.isSafeToMoveToRam());
        assertEquals(1, variable.getRamCell());
        assertNull(variable.getRegister());
        assertEquals(VarStat.unassigned, variable.getStatus());
    }


    @Test
    void assignToRegisterTest() {
        Variable variable = new Variable(1, "TEST");
        Register newRegister = new Register(1, registerModulePlaceholder);
        variable.setMovedToRegister(newRegister);
        assertEquals(VarStat.inRegister, variable.getStatus());
        assertEquals(newRegister, variable.getRegister());
    }

    @Test
    void setMovedToRamTest() {
        Variable variable = new Variable(1, "TEST");
        Register newRegister = new Register(1, registerModulePlaceholder);

        variable.setMovedToRegister(newRegister);

        variable.setMovedToRam();
        assertEquals(VarStat.inRam, variable.getStatus());
        assertNull(variable.getRegister());
        assertEquals(1, variable.getRamCell());
    }

    @Test
    void setMovedToRegisterUninitializedTest() {
        Variable variable = new Variable(1, "TEST");
        Register newRegister = new Register(1, registerModulePlaceholder);
        variable.setMovedToRegister(newRegister);

        assertTrue(variable.isInitialized());
        assertFalse(variable.isSafeToMoveToRam());
        assertEquals(newRegister, variable.getRegister());

    }

    @Test
    void setMovedToRegisterInitializedTest() {
        Variable variable = new Variable(1, "TEST");
        Register newRegister = new Register(1, registerModulePlaceholder);
        variable.setMovedToRegister(newRegister);

        assertTrue(variable.isInitialized());
        assertFalse(variable.isSafeToMoveToRam());
        assertEquals(newRegister, variable.getRegister());
    }

    @Test
    void unlockTest() {
        Variable variable = new Variable(1, "TEST");
        Register newRegister = new Register(1, registerModulePlaceholder);
        variable.setMovedToRegister(newRegister);

        variable.unlock();

        assertTrue(variable.isSafeToMoveToRam());

    }
}