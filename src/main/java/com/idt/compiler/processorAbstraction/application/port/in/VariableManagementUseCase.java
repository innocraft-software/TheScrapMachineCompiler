package com.idt.compiler.processorAbstraction.application.port.in;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.common.globalDomain.variableManagement.VariableScope;

public interface VariableManagementUseCase {
    Variable assignIntVar(VariableScope scope, String identifier);

    // extends the scope to include the destination scope. The variable is only deleted if both scopes are exited
    void extendScope(Variable variable, VariableScope destination);

    void inRegisterDeepCopy(Variable source, Variable destination);

    Variable assignIntVar(String identifier);

    Variable getVariableByIdentifier(String identifier);
}
