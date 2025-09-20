package com.idt.compiler.processorAbstraction.application.port.out.domainConnectors;

import com.idt.compiler.common.globalDomain.Instruction;

public interface AddInstructionPort {
    void commitInstructionRow(Instruction instruction);
}
