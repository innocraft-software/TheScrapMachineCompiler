package com.idt.compiler.processorAbstraction.domain.registerManagement;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FutureRegisterTest {

    private final RegisterModule registerModulePlaceholer = RegisterModule.initialize();

    @Test
    void unassignedTest() {
        FutureRegister futureRegister = new FutureRegister();
        assertThrows(IllegalCallerException.class, futureRegister::getReg);
    }

    @Test
    void normalUsageTest() {
        // setup
        Register registerToBeAssigned = new Register(1, registerModulePlaceholer);
        FutureRegister futureRegister = new FutureRegister();

        // assign register
        futureRegister.setReg(registerToBeAssigned);

        // test
        assertEquals(registerToBeAssigned, futureRegister.getReg());

    }
}