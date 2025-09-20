package com.idt.compiler.interpreter.domain.controlStructures;

import com.idt.compiler.common.globalDomain.variableManagement.VariableScope;

public interface ControlStructure {
    VariableScope getLocalScope();

    ControlStructureType getControlStructureType();
}
