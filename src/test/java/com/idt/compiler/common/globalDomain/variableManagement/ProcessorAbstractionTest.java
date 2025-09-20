package com.idt.compiler.common.globalDomain.variableManagement;

import com.idt.compiler.common.globalDomain.Instruction;
import com.idt.compiler.processorAbstraction.application.port.out.domainConnectors.AddInstructionPort;
import com.idt.compiler.processorAbstraction.domain.registerManagement.Register;
import com.idt.compiler.processorAbstraction.domain.registerManagement.RegisterModule;
import com.idt.globalDependencies.assemblyProcessor.AssemblyGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.then;

@SpringBootTest
class ProcessorAbstractionTest {

    private VariableScope localScope;

    @Mock
    private AddInstructionPort addInstructionPort;

    private ProcessorAbstraction processorAbstraction;

    private final RegisterModule registerModulePlaceholder = RegisterModule.initialize();

    @BeforeEach
    void init() {
        this.processorAbstraction = ProcessorAbstraction.initialize(addInstructionPort);

        if (localScope == null) {
            localScope = processorAbstraction.createScope("TestScope");
        }
    }

    @Test
    void allocateVariableTest() {
        Variable variable = processorAbstraction.allocateNewVariable(localScope, "TEST");
        assertTrue(processorAbstraction.getVariableScopeManager().variableExists(variable));
        assertEquals(0, variable.getRamCell());
    }

    @Test
    void givenVariableInRegister_whenMoveToRegister_thenRequestRejectedTest() {
        Variable variable = processorAbstraction.allocateNewVariable(localScope, "TEST");

        Register register = new Register(0, registerModulePlaceholder);

        // move to a register in order to initialize var
        variable.setMovedToRegister(register);
        register.setVariable(variable);


        assertThrows(IllegalCallerException.class, () -> processorAbstraction.variableToRegister(variable, new Register(1, registerModulePlaceholder)));


        // check that the port was not called
        then(addInstructionPort).shouldHaveNoInteractions();
    }

    @Test
    void givenNonInitializedVariableInRam_whenMoveToRegister_thenPortNotCalledTest() {
        Variable variable = processorAbstraction.allocateNewVariable(localScope, "TEST");

        processorAbstraction.variableToRegister(variable, new Register(1, registerModulePlaceholder));

        // check that the port was not called
        then(addInstructionPort).shouldHaveNoInteractions();
    }

    @Test
    void givenInitializedVariableInRam_whenMoveToRegister_thenInstructionIsSentToPortTest() {
        Variable variable = processorAbstraction.allocateNewVariable(localScope, "TEST");

        // move to a register in order to initialize var
        variable.setMovedToRegister(new Register(0, registerModulePlaceholder));

        // move var back to ram to not trigger an invariant
        variable.setMovedToRam();

        processorAbstraction.variableToRegister(variable, new Register(1, registerModulePlaceholder));

        // check if the port was called with the correct instruction
        then(addInstructionPort).should().commitInstructionRow(Instruction.initialize(AssemblyGenerator.ld(variable.getRamCell(), 1)));
    }

    @Test
    void givenForeignInitializedVariableInRam_whenMoveToRegister_thenRequestRejectedTest() {
        Variable variable = new Variable(3, "Test");

        // move to a register in order to initialize var
        variable.setMovedToRegister(new Register(0, registerModulePlaceholder));

        // move var back to ram to not trigger an invariant
        variable.setMovedToRam();

        assertThrows(IllegalCallerException.class, () -> processorAbstraction.variableToRegister(variable, new Register(1, registerModulePlaceholder)));

        // check if the port was called with the correct instruction
        then(addInstructionPort).shouldHaveNoInteractions();
    }

    @Test
    void givenVariableInReg_whenMoveToRegister_thenVariableIsReUsedTest() {
        Variable variable = processorAbstraction.allocateNewVariable(localScope, "TEST");

        Register register = new Register(1, registerModulePlaceholder);

        // move to a register in order to initialize var
        variable.setMovedToRegister(register);
        register.setVariable(variable);


        processorAbstraction.variableToRegister(variable, register);

        // check if the port was called with the correct instruction
        then(addInstructionPort).shouldHaveNoInteractions();
    }

    @Test
    void givenVariableInRegister_whenMoveToRam_thenCorrectInstructionSentToPortTest() {
        Variable variable = processorAbstraction.allocateNewVariable(localScope, "TEST");
        Register register = new Register(1, registerModulePlaceholder);
        processorAbstraction.variableToRegister(variable, register);

        processorAbstraction.variableToRam(variable);

        then(addInstructionPort).should().commitInstructionRow(Instruction.initialize(AssemblyGenerator.str(variable.getRamCell(), 1)));
    }

    @Test
    void givenVariableInRam_whenMoveToRam_thenMoveRejectedTest() {

        Variable variable = processorAbstraction.allocateNewVariable(localScope, "TEST");
        Register register = new Register(1, registerModulePlaceholder);
        processorAbstraction.variableToRegister(variable, register);

        processorAbstraction.variableToRam(variable);

        assertThrows(IllegalCallerException.class, () -> processorAbstraction.variableToRam(variable));

    }

    @Test
    void givenForeignVariableInRegister_whenMoveToRam_thenRestrictTest() {
        // create alternative abstraction
        ProcessorAbstraction processorAbstraction2 = ProcessorAbstraction.initialize(this.addInstructionPort);
        // create alternative scope for the abstraction
        VariableScope tempScope = processorAbstraction2.createScope("TempScope");
        // create a foreign variable
        Variable variable = processorAbstraction2.allocateNewVariable(tempScope, "TEST");

        Register register = new Register(1, registerModulePlaceholder);
        processorAbstraction2.variableToRegister(variable, register);

        assertThrows(IllegalCallerException.class, () -> processorAbstraction.variableToRam(variable));
    }


}