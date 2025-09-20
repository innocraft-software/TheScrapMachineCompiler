package com.idt.compiler.processorAbstraction.application.service;

import com.idt.compiler.common.globalDomain.variableManagement.ProcessorAbstraction;
import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.common.globalDomain.variableManagement.VariableScope;
import com.idt.compiler.processorAbstraction.application.port.in.VariableManagementUseCase;
import com.idt.compiler.processorAbstraction.application.port.out.persistence.LoadPAModelPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VariableModelManagementService implements VariableManagementUseCase {

    private final LoadPAModelPort loadPAModelPort;

    @Override
    public Variable assignIntVar(VariableScope scope, String identifier) {
        return loadPAModelPort.getProcessorAbstraction().allocateNewVariable(scope, identifier);
    }

    @Override
    public void extendScope(Variable variable, VariableScope destination) {
        loadPAModelPort.getProcessorAbstraction().getVariableScopeManager().extendScope(variable, destination);
    }

    @Override
    public void inRegisterDeepCopy(Variable source, Variable destination) {
        loadPAModelPort.getProcessorAbstraction().inRegisterDeepCopy(source, destination);
    }

    @Override
    public Variable assignIntVar(String identifier) {
        ProcessorAbstraction pa = loadPAModelPort.getProcessorAbstraction();
        VariableScope scope = pa.getActiveScope();
        return pa.allocateNewVariable(scope, identifier);
    }

    @Override
    public Variable getVariableByIdentifier(String identifier) {
        ProcessorAbstraction processorAbstraction = loadPAModelPort.getProcessorAbstraction();

        return processorAbstraction.getVariableScopeManager().getVariableByIdentifier(identifier);
    }
}
