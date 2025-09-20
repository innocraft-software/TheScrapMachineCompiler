package com.idt.compiler.processorAbstraction.application.service;

import com.idt.compiler.common.globalDomain.variableManagement.VariableScope;
import com.idt.compiler.processorAbstraction.application.port.in.ScopeManagementUseCase;
import com.idt.compiler.processorAbstraction.application.port.out.persistence.LoadPAModelPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScopeManagementService implements ScopeManagementUseCase {
    private final LoadPAModelPort loadPAModelPort;

    @Override
    public VariableScope createScope(String identifier) {
        return loadPAModelPort.getProcessorAbstraction().createScope(identifier);
    }

    @Override
    public VariableScope getActiveScope() {
        return loadPAModelPort.getProcessorAbstraction().getActiveScope();
    }

    @Override
    public void exitScope(VariableScope scope) {
        loadPAModelPort.getProcessorAbstraction().exitScope(scope);
    }
}
