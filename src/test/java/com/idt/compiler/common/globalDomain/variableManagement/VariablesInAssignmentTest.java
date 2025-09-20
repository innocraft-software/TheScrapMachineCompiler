package com.idt.compiler.common.globalDomain.variableManagement;

import com.idt.compiler.processorAbstraction.domain.registerManagement.FutureRegister;
import com.idt.compiler.processorAbstraction.domain.registerManagement.Register;
import com.idt.compiler.processorAbstraction.domain.registerManagement.RegisterModule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VariablesInAssignmentTest {

    private final RegisterModule registerModulePlaceholder = RegisterModule.initialize();

    @Test
    void initializeTest() {
        VariablesInAssignment variablesInAssignment = VariablesInAssignment.initialize();
        assertEquals(0, variablesInAssignment.assignmentSize());
    }

    @Test
    void emptyUpdateTest() {
        VariablesInAssignment variablesInAssignment = VariablesInAssignment.initialize();
        assertEquals(0, variablesInAssignment.assignmentSize());

        variablesInAssignment.update();
    }

    @Test
    void resetTest() {
        VariablesInAssignment variablesInAssignment = VariablesInAssignment.initialize();
        assertEquals(0, variablesInAssignment.assignmentSize());

        variablesInAssignment.put(new Variable(0, "TEST"));

        variablesInAssignment.reset();

        assertEquals(0, variablesInAssignment.assignmentSize());
    }

    @Test
    void resetToBeRemovedTest() {
        VariablesInAssignment variablesInAssignment = VariablesInAssignment.initialize();
        assertEquals(0, variablesInAssignment.assignmentSize());

        Variable var = new Variable(0, "TEST");
        variablesInAssignment.put(var);
        variablesInAssignment.setRegister(var, new Register(1, registerModulePlaceholder));

        // reset before update
        variablesInAssignment.reset();

        // ensure the variable stays on next update
        variablesInAssignment.put(var);
        variablesInAssignment.update();
        assertEquals(1, variablesInAssignment.assignmentSize());

    }

    @Test
    void putTest() {
        VariablesInAssignment variablesInAssignment = VariablesInAssignment.initialize();
        assertEquals(0, variablesInAssignment.assignmentSize());

        Variable var = new Variable(0, "TEST");
        variablesInAssignment.put(var);

        assertTrue(variablesInAssignment.variables().contains(var));
    }

    @Test
    void putDuplicateTest() {

        VariablesInAssignment variablesInAssignment = VariablesInAssignment.initialize();
        assertEquals(0, variablesInAssignment.assignmentSize());

        Variable var = new Variable(0, "TEST");
        FutureRegister futureRegister1 = variablesInAssignment.put(var);

        FutureRegister futureRegister2 = variablesInAssignment.put(var);

        // ensure the same key is returned on duplocate put
        assertEquals(futureRegister1, futureRegister2);

    }

    @Test
    void setRegisterTest() {
        VariablesInAssignment variablesInAssignment = VariablesInAssignment.initialize();
        assertEquals(0, variablesInAssignment.assignmentSize());

        Variable var = new Variable(0, "TEST");
        FutureRegister futureRegister = variablesInAssignment.put(var);

        Register register = new Register(1, registerModulePlaceholder);
        variablesInAssignment.setRegister(var, register);

        assertEquals(futureRegister.getReg(), register);
    }

    @Test
    void confirmRegisterTest() {
        VariablesInAssignment variablesInAssignment = VariablesInAssignment.initialize();
        assertEquals(0, variablesInAssignment.assignmentSize());

        Variable var = new Variable(0, "TEST");
        FutureRegister futureRegister = variablesInAssignment.put(var);

        Register register = new Register(1, registerModulePlaceholder);
        var.setMovedToRegister(register);

        variablesInAssignment.confirmRegister(var);
        assertEquals(futureRegister.getReg(), register);

    }

    @Test
    void assignmentSizeTest() {
        VariablesInAssignment variablesInAssignment = VariablesInAssignment.initialize();
        assertEquals(0, variablesInAssignment.assignmentSize());

        Variable var = new Variable(0, "TEST");
        variablesInAssignment.put(var);

        Variable var2 = new Variable(1, "TEST2");
        variablesInAssignment.put(var2);

        assertEquals(2, variablesInAssignment.assignmentSize());

    }

    @Test
    void toBeRemovedTest() {
        VariablesInAssignment variablesInAssignment = VariablesInAssignment.initialize();
        assertEquals(0, variablesInAssignment.assignmentSize());

        Variable var = new Variable(0, "TEST");
        variablesInAssignment.put(var);

        variablesInAssignment.setRegister(var, new Register(0, registerModulePlaceholder));

        variablesInAssignment.update();

        assertEquals(0, variablesInAssignment.assignmentSize());

    }
}