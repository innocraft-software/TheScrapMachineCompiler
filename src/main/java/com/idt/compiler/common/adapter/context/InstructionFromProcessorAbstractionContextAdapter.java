package com.idt.compiler.common.adapter.context;

import com.idt.compiler.assembly.application.port.in.AppendInstructionUseCase;
import com.idt.compiler.assembly.application.port.in.GetNextInstructionUseCase;
import com.idt.compiler.common.globalDomain.Instruction;
import com.idt.compiler.processorAbstraction.application.port.out.domainConnectors.AddInstructionPort;
import com.idt.compiler.processorAbstraction.application.port.out.domainConnectors.GetNextInstructionPort;
import com.idt.compiler.processorAbstraction.application.port.out.domainConnectors.ModifyInstructionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InstructionFromProcessorAbstractionContextAdapter implements AddInstructionPort, GetNextInstructionPort, ModifyInstructionPort {
    private final AppendInstructionUseCase appendInstructionUseCase;

    private final GetNextInstructionUseCase getNextInstructionUseCase;


    @Override
    public void commitInstructionRow(Instruction instruction) {
        appendInstructionUseCase.appendInstruction(instruction);
    }

    @Override
    public int getNextInstruction() {
        return getNextInstructionUseCase.getNextInstruction();
    }

    @Override
    public void updateInstruction(Instruction instruction, Instruction newInstruction) {
        appendInstructionUseCase.replaceInstruction(instruction, newInstruction);
    }
}
