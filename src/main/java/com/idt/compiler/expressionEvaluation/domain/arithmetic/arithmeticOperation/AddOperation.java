package com.idt.compiler.expressionEvaluation.domain.arithmetic.arithmeticOperation;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.processorAbstraction.application.port.in.InstructionGenerationUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.VariableManagementUseCase;

public class AddOperation extends ArithmeticOperation {

    public AddOperation(InstructionGenerationUseCase instructionGenerationUseCase, VariableManagementUseCase variableManagementUseCase) {
        super(instructionGenerationUseCase, variableManagementUseCase);
    }

    @Override
    void performOperation(Variable operand1, Variable operand2, Variable output) {
        getInstructionGenerator().add(operand1, operand2, output);
    }

    @Override
    public int performOperationAtCompileTime(int operand1, int operand2) {
        return operand1 + operand2;
    }

}
