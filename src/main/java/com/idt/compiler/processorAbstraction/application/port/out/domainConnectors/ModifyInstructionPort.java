package com.idt.compiler.processorAbstraction.application.port.out.domainConnectors;

import com.idt.compiler.common.globalDomain.Instruction;

public interface ModifyInstructionPort {
    void updateInstruction(Instruction instruction, Instruction newInstruction);
}
