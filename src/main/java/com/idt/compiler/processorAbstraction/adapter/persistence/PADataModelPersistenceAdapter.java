package com.idt.compiler.processorAbstraction.adapter.persistence;

import com.idt.compiler.common.globalDomain.variableManagement.ProcessorAbstraction;
import com.idt.compiler.common.globalDomain.variableManagement.VariableManagementSession;
import com.idt.compiler.processorAbstraction.application.port.out.domainConnectors.AddInstructionPort;
import com.idt.compiler.processorAbstraction.application.port.out.persistence.LoadPAModelPort;
import com.idt.compiler.processorAbstraction.application.port.out.persistence.ResetPAModelPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class PADataModelPersistenceAdapter implements LoadPAModelPort, ResetPAModelPort {

    private ProcessorAbstraction processorAbstraction;
    private VariableManagementSession variableManagementSession;

    private final AddInstructionPort addInstructionPort;

    @Autowired
    public PADataModelPersistenceAdapter(AddInstructionPort addInstructionPort) {
        this.addInstructionPort = addInstructionPort;
        this.processorAbstraction = ProcessorAbstraction.initialize(this.addInstructionPort);
        this.variableManagementSession = VariableManagementSession.initialize(this.processorAbstraction.getRegisterModule());
    }

    @Override
    public ProcessorAbstraction getProcessorAbstraction() {
        return this.processorAbstraction;
    }

    @Override
    public VariableManagementSession getVMS() {
        return this.variableManagementSession;
    }

    @Override
    public void reset() {
        this.processorAbstraction = ProcessorAbstraction.initialize(this.addInstructionPort);
        this.variableManagementSession = VariableManagementSession.initialize(this.processorAbstraction.getRegisterModule());
    }
}
