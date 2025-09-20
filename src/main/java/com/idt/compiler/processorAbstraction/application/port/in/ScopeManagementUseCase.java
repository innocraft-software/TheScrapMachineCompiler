package com.idt.compiler.processorAbstraction.application.port.in;

import com.idt.compiler.common.globalDomain.variableManagement.VariableScope;

public interface ScopeManagementUseCase {
    VariableScope createScope(String identifier);

    VariableScope getActiveScope();

    void exitScope(VariableScope scope);
}
