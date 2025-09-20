package com.idt.compiler.expressionEvaluation.domain.bool.comparisonOperation;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.processorAbstraction.application.port.in.InstructionGenerationUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.VariableManagementUseCase;

public class LessThanOperation extends ComparisonOperation {

    public LessThanOperation(InstructionGenerationUseCase instructionGenerationUseCase, VariableManagementUseCase variableManagementUseCase) {
        super(instructionGenerationUseCase, variableManagementUseCase);
    }

    @Override
    void performOperation(Variable operand1, Variable operand2, Variable output) {
        // Less than computed as inverted >

        // inversion of greater than
        getInstructionGenerator().isGreaterThan(operand2, operand1, output);
    }
}
