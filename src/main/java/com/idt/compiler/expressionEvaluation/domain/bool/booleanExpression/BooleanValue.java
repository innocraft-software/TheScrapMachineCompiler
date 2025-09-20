package com.idt.compiler.expressionEvaluation.domain.bool.booleanExpression;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.common.globalDomain.variableManagement.VariableScope;
import com.idt.compiler.processorAbstraction.application.port.in.InstructionGenerationUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.VariableManagementUseCase;

import java.util.Objects;

public class BooleanValue implements BooleanExpression {
    private final VariableManagementUseCase variableManagementUseCase;

    private final InstructionGenerationUseCase instructionGenerationUseCase;

    private boolean isVariable;
    private boolean value;
    private String variable;

    public BooleanValue(String value, VariableManagementUseCase variableManagementUseCase, InstructionGenerationUseCase instructionGenerationUseCase) {

        this.variableManagementUseCase = variableManagementUseCase;
        this.instructionGenerationUseCase = instructionGenerationUseCase;


        if (Objects.equals(value, "true")) {
            this.value = true;
        } else if (Objects.equals(value, "false")) {
            this.value = false;
        } else {
            // variable case
            this.isVariable = true;
            this.variable = value;
        }
    }

    @Override
    public Variable evaluate(VariableScope outputScope) {

        if (isVariable) {
            Variable returnVar = variableManagementUseCase.getVariableByIdentifier(this.variable);
            variableManagementUseCase.extendScope(returnVar, outputScope);
            return variableManagementUseCase.getVariableByIdentifier(this.variable);

        } else {
            Variable temp = variableManagementUseCase.assignIntVar(outputScope, this.toString());
            instructionGenerationUseCase.writeIntVar(this.value ? 1 : 0, temp);
            return temp;
        }
    }
}
