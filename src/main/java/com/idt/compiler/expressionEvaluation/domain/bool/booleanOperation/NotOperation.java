package com.idt.compiler.expressionEvaluation.domain.bool.booleanOperation;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.processorAbstraction.application.port.in.InstructionGenerationUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.VariableManagementUseCase;

public class NotOperation extends BooleanOperation {
    public NotOperation(InstructionGenerationUseCase instructionGenerationUseCase, VariableManagementUseCase variableManagementUseCase) {
        super(instructionGenerationUseCase, variableManagementUseCase);
    }

    @Override
    void performOperation(Variable operand1, Variable operand2, Variable output) {
        // Liskov is not happy but this is the simplest solution in this case
        throw new IllegalStateException("Not Operation requires different parsing. This should be handled in appropriate boolean builder.");
    }
}
