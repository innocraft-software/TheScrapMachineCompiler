package com.idt.compiler.assembly.domain;

import com.idt.compiler.common.globalDomain.Instruction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InstructionTest {

    @Test
    void initializationTest() {
        Instruction instruction = Instruction.initialize(1234);
        assertEquals(1234, instruction.getBinary());
    }

    @Test
    void terminateTest() {
        Instruction terminateInstruction = Instruction.terminateInstruction();
        assertEquals(0b1000000000000000, terminateInstruction.getBinary());
    }

    @Test
    void equalsTest() {
        Instruction i1 = Instruction.initialize(15);
        Instruction i2 = Instruction.initialize(15);
        assertEquals(i1, i2);
    }

}