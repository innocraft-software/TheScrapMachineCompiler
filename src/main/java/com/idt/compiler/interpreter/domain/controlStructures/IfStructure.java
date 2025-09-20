package com.idt.compiler.interpreter.domain.controlStructures;

import com.idt.compiler.common.globalDomain.Instruction;
import com.idt.compiler.common.globalDomain.variableManagement.VariableScope;

public record IfStructure(Instruction branchInstruction, VariableScope localScope) implements ControlStructure {

    @Override
    public VariableScope getLocalScope() {
        return localScope;
    }

    @Override
    public ControlStructureType getControlStructureType() {
        return ControlStructureType.IF;
    }

}
