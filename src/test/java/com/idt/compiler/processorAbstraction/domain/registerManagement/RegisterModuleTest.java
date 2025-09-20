package com.idt.compiler.processorAbstraction.domain.registerManagement;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RegisterModuleTest {
    @Test
    void initializationTest() {
        RegisterModule registerModule = RegisterModule.initialize();
        assertEquals(RegisterModule.REGISTER_COUNT, registerModule.getRegisters().size());
    }

    @Test
    void findFreeRegisterTest() throws RegisterOverflowException {
        // setup
        Variable v1 = new Variable(0, "v1");
        Variable v2 = new Variable(1, "v2");
        Variable v3 = new Variable(2, "v3");
        Variable v4 = new Variable(3, "v4");
        RegisterModule registerModule = RegisterModule.initialize();

        // attempts to assign four
        Register v1_Register = registerModule.findFreeRegister();
        v1_Register.setVariable(v1);
        Register v2_Register = registerModule.findFreeRegister();
        v2_Register.setVariable(v2);
        Register v3_Register = registerModule.findFreeRegister();
        v3_Register.setVariable(v3);
        Register v4_Register = registerModule.findFreeRegister();
        v4_Register.setVariable(v4);


        // assert fifth overflows
        assertThrows(RegisterOverflowException.class, registerModule::findFreeRegister);
    }

    @Test
    void findFreeRegistersTest() {
        RegisterModule registerModule = RegisterModule.initialize();

        List<Register> free = registerModule.findFreeRegisters();

        Register v1_Register = free.get(0);
        Variable v1 = new Variable(0, "v1");
        v1_Register.setVariable(v1);

        assertEquals(free.size() - 1, registerModule.findFreeRegisters().size());

    }

    @Test
    void findReUseRegisterTest() {
        RegisterModule registerModule = RegisterModule.initialize();

        List<Register> free = registerModule.findFreeRegisters();

        Register v1_Register = free.get(1);
        Variable v1 = new Variable(0, "v1");
        v1_Register.setVariable(v1);

        v1_Register.free();

        assertEquals(v1_Register, registerModule.attemptReUse(v1));

    }

    @Test
    void findVariableRegisterTest() {
        RegisterModule registerModule = RegisterModule.initialize();

        List<Register> free = registerModule.findFreeRegisters();

        assertEquals(0, registerModule.findVariableRegisters().size());

        Register v1_Register = free.get(1);
        Variable v1 = new Variable(0, "v1");
        v1_Register.setVariable(v1);

        assertEquals(1, registerModule.findVariableRegisters().size());
        assertEquals(v1_Register, registerModule.findVariableRegisters().get(0));

    }

    @Test
    void variableReUseTest() {
        RegisterModule registerModule = RegisterModule.initialize();

        List<Register> free = registerModule.findFreeRegisters();
        Register v1_Register = free.get(1);
        Variable v1 = new Variable(0, "v1");
        v1_Register.setVariable(v1);

        // test if still in use
        assertEquals(v1_Register, registerModule.attemptReUse(v1));

        // test if no longer in use
        v1_Register.free();
        assertEquals(v1_Register, registerModule.attemptReUse(v1));

    }

    @Test
    void immediateReUseTest() {
        RegisterModule registerModule = RegisterModule.initialize();

        List<Register> free = registerModule.findFreeRegisters();
        Register i1_Register = free.get(1);
        int i1 = 5;
        i1_Register.setImmediate(i1);

        // test if still in use
        assertEquals(i1_Register, registerModule.attemptReUse(i1));

        // test if no longer in use
        i1_Register.free();
        assertEquals(i1_Register, registerModule.attemptReUse(i1));

    }

    @Test
    void invalidReUseTest() {
        RegisterModule registerModule = RegisterModule.initialize();

        List<Register> free = registerModule.findFreeRegisters();
        Register v1_Register = free.get(1);
        Variable v1 = new Variable(0, "v1");
        v1_Register.setVariable(v1);

        Variable v2 = new Variable(2, "v2");

        assertNull(registerModule.attemptReUse(v2));
    }

    @Test
    void findVariableRegistersTest() throws RegisterOverflowException {
        Variable v1 = new Variable(0, "v1");
        Variable v2 = new Variable(1, "v2");
        Variable v3 = new Variable(2, "v3");
        RegisterModule registerModule = RegisterModule.initialize();

        // attempts to assign three

        Register v1_Register = registerModule.findFreeRegister();
        v1_Register.setVariable(v1);
        Register v2_Register = registerModule.findFreeRegister();
        v2_Register.setVariable(v2);
        Register v3_Register = registerModule.findFreeRegister();
        v3_Register.setVariable(v3);

        assertEquals(3, registerModule.findVariableRegisters().size());
        assertTrue(registerModule.findVariableRegisters().contains(v1_Register));
        assertTrue(registerModule.findVariableRegisters().contains(v2_Register));
        assertTrue(registerModule.findVariableRegisters().contains(v3_Register));


    }

    @Test
    void assignEmptyImmediateTest() {
        RegisterModule registerModule = RegisterModule.initialize();

        assertEquals(1, registerModule.assignEmptyImmediate(1).getImmediate());

    }

    @Test
    void freeRegistersTest() throws RegisterOverflowException {
        // setup
        Variable v1 = new Variable(0, "v1");
        Variable v2 = new Variable(1, "v2");
        Variable v3 = new Variable(2, "v3");
        Variable v4 = new Variable(3, "v4");
        RegisterModule registerModule = RegisterModule.initialize();

        Register v1_Register = registerModule.findFreeRegister();
        v1_Register.setVariable(v1);
        Register v2_Register = registerModule.findFreeRegister();
        v2_Register.setVariable(v2);
        Register v3_Register = registerModule.findFreeRegister();
        v3_Register.setVariable(v3);
        Register v4_Register = registerModule.findFreeRegister();
        v4_Register.setVariable(v4);

        // test
        registerModule.freeRegisters();
        assertEquals(4, registerModule.findFreeRegisters().size());

    }

    @Test
    void clearRegistersTest() throws RegisterOverflowException {
        // setup
        Variable v1 = new Variable(0, "v1");
        Variable v2 = new Variable(1, "v2");
        Variable v3 = new Variable(2, "v3");
        Variable v4 = new Variable(3, "v4");
        RegisterModule registerModule = RegisterModule.initialize();

        Register v1_Register = registerModule.findFreeRegister();
        v1_Register.setVariable(v1);
        Register v2_Register = registerModule.findFreeRegister();
        v2_Register.setVariable(v2);
        Register v3_Register = registerModule.findFreeRegister();
        v3_Register.setVariable(v3);
        Register v4_Register = registerModule.findFreeRegister();
        v4_Register.setVariable(v4);
        registerModule.freeRegisters();

        // test
        registerModule.clearRegisters();
        assertEquals(RegisterStatus.free, v1_Register.getStatus());
        assertEquals(RegisterStatus.free, v2_Register.getStatus());
        assertEquals(RegisterStatus.free, v3_Register.getStatus());
        assertEquals(RegisterStatus.free, v4_Register.getStatus());

    }

}