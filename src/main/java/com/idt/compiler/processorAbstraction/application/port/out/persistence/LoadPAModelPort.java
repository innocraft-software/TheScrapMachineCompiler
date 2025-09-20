package com.idt.compiler.processorAbstraction.application.port.out.persistence;

import com.idt.compiler.common.globalDomain.variableManagement.ProcessorAbstraction;
import com.idt.compiler.common.globalDomain.variableManagement.VariableManagementSession;

public interface LoadPAModelPort {
    ProcessorAbstraction getProcessorAbstraction();

    VariableManagementSession getVMS();

}
