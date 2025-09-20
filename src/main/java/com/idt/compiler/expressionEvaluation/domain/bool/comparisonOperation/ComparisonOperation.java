package com.idt.compiler.expressionEvaluation.domain.bool.comparisonOperation;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.common.globalDomain.variableManagement.VariableScope;
import com.idt.compiler.processorAbstraction.application.port.in.InstructionGenerationUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.VariableManagementUseCase;

public abstract class ComparisonOperation {
    private final VariableManagementUseCase variableManagementUseCase;
    private final InstructionGenerationUseCase instructionGenerationUseCase;

    public ComparisonOperation(InstructionGenerationUseCase instructionGenerationUseCase, VariableManagementUseCase variableManagementUseCase) {
        this.instructionGenerationUseCase = instructionGenerationUseCase;
        this.variableManagementUseCase = variableManagementUseCase;
    }

    public Variable evaluate(Variable operand1, Variable operand2, VariableScope outputScope) {
        Variable tempOutput = variableManagementUseCase.assignIntVar(outputScope, this.toString());
        performOperation(operand1, operand2, tempOutput);
        return tempOutput;
    }

    protected InstructionGenerationUseCase getInstructionGenerator() {
        return this.instructionGenerationUseCase;
    }

    abstract void performOperation(Variable operand1, Variable operand2, Variable output);

}
