package com.idt.compiler.expressionEvaluation.domain.bool.comparisonOperation;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.processorAbstraction.application.port.in.InstructionGenerationUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.VariableManagementUseCase;

public class GreaterThanEqualOperation extends ComparisonOperation {

    public GreaterThanEqualOperation(InstructionGenerationUseCase instructionGenerationUseCase, VariableManagementUseCase variableManagementUseCase) {
        super(instructionGenerationUseCase, variableManagementUseCase);
    }

    @Override
    void performOperation(Variable operand1, Variable operand2, Variable output) {
        // Greater than equal computed using as !<

        // inversion of greater than == o1 < o2
        getInstructionGenerator().isGreaterThan(operand2, operand1, output);

        // flip the output as o1 >= o2 == ! o1 < o2
        getInstructionGenerator().not(output, output);
    }
}
