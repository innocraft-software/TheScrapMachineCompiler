package com.idt.compiler.expressionEvaluation.domain.bool.booleanExpression;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.common.globalDomain.variableManagement.VariableScope;
import com.idt.compiler.processorAbstraction.application.port.in.InstructionGenerationUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.ScopeManagementUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.VariableManagementUseCase;

public class NotExpression implements BooleanExpression {

    private final ScopeManagementUseCase scopeManagementUseCase;

    private final InstructionGenerationUseCase instructionGenerationUseCase;
    private final VariableManagementUseCase variableManagementUseCase;

    private final BooleanExpression value;

    public NotExpression(BooleanExpression value, ScopeManagementUseCase scopeManagementUseCase, InstructionGenerationUseCase instructionGenerationUseCase, VariableManagementUseCase variableManagementUseCase) {
        this.value = value;
        this.scopeManagementUseCase = scopeManagementUseCase;
        this.instructionGenerationUseCase = instructionGenerationUseCase;
        this.variableManagementUseCase = variableManagementUseCase;
    }

    @Override
    public Variable evaluate(VariableScope outputScope) {

        VariableScope innerScope = scopeManagementUseCase.createScope("InnerScopeOf_" + this.hashCode());

        Variable tempOutput = variableManagementUseCase.assignIntVar(outputScope, this.toString());
        instructionGenerationUseCase.not(value.evaluate(innerScope), tempOutput);

        // delete all variables in the scope of this operation
        scopeManagementUseCase.exitScope(innerScope);

        return tempOutput;
    }
}
