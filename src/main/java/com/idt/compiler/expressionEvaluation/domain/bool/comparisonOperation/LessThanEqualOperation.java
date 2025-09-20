package com.idt.compiler.expressionEvaluation.domain.bool.comparisonOperation;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.processorAbstraction.application.port.in.InstructionGenerationUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.VariableManagementUseCase;

public class LessThanEqualOperation extends ComparisonOperation {

    public LessThanEqualOperation(InstructionGenerationUseCase instructionGenerationUseCase, VariableManagementUseCase variableManagementUseCase) {
        super(instructionGenerationUseCase, variableManagementUseCase);
    }

    @Override
    void performOperation(Variable operand1, Variable operand2, Variable output) {
        // Less than equal computed as !>

        getInstructionGenerator().isGreaterThan(operand1, operand2, output);

        // flip the output as o1 >= o2 == ! o1 < o2
        getInstructionGenerator().not(output, output);
    }
}
