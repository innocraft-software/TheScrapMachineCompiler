package com.idt.compiler.processorAbstraction.domain;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RamManagerTest {

    @Test
    void initializeTest() {
        RamManager.initialize();
    }

    @Test
    void overflowTest() {
        // setup
        RamManager ramManager = RamManager.initialize();

        // check how much ram is configured
        int maximumAllowedRam = RamManager.getTOTAL_RAM();

        // fill the existing ram
        for (int index = 0; index < maximumAllowedRam; index++) {
            ramManager.allocateVariable(String.valueOf(index));
        }

        // attempt overflow
        assertThrows(RamOverflowException.class, () -> ramManager.allocateVariable("moreThanAllowed"));
    }

    @Test
    void allocateVariableTest() {
        // setup
        RamManager ramManager = RamManager.initialize();

        Variable variable = ramManager.allocateVariable("TEST");

        assertEquals(0, variable.getRamCell());
    }

    @Test
    void allocateMultipleVariableTest() {
        // setup
        RamManager ramManager = RamManager.initialize();

        Variable variable = ramManager.allocateVariable("TEST");
        Variable variable2 = ramManager.allocateVariable("TEST2");

        assertEquals(0, variable.getRamCell());
        assertNotEquals(variable.getRamCell(), variable2.getRamCell());
    }
}