package com.idt.compiler.expressionEvaluation.domain.bool.comparisonOperation;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.processorAbstraction.application.port.in.InstructionGenerationUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.VariableManagementUseCase;

public class GreaterThanOperation extends ComparisonOperation {

    public GreaterThanOperation(InstructionGenerationUseCase instructionGenerationUseCase, VariableManagementUseCase variableManagementUseCase) {
        super(instructionGenerationUseCase, variableManagementUseCase);
    }

    @Override
    void performOperation(Variable operand1, Variable operand2, Variable output) {
        // Greater than exists as is

        getInstructionGenerator().isGreaterThan(operand1, operand2, output);
    }
}
