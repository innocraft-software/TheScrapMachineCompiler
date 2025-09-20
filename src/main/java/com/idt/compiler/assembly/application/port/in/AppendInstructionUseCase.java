package com.idt.compiler.assembly.application.port.in;

import com.idt.compiler.common.globalDomain.Instruction;

public interface AppendInstructionUseCase {
    void appendInstruction(Instruction instruction);

    void replaceInstruction(Instruction instruction, Instruction newInstruction);
}
