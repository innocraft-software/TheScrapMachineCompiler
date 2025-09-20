package com.idt.compiler.expressionEvaluation.domain.arithmetic.arithmeticExpression;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.common.globalDomain.variableManagement.VariableScope;
import com.idt.compiler.processorAbstraction.application.port.in.InstructionGenerationUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.VariableManagementUseCase;
import lombok.Getter;

@Getter
public class ArithmeticValue implements ArithmeticExpression, PreEvaluatableNumeric {

    private final VariableManagementUseCase variableManagementUseCase;

    private final InstructionGenerationUseCase instructionGenerationUseCase;

    private boolean isVariable;
    private int number;
    private String variable;

    public ArithmeticValue(String value, VariableManagementUseCase variableManagementUseCase, InstructionGenerationUseCase instructionGenerationUseCase) {

        this.variableManagementUseCase = variableManagementUseCase;
        this.instructionGenerationUseCase = instructionGenerationUseCase;


        try {
            // number case
            int content = Integer.parseInt(value);
            this.isVariable = false;
            this.number = content;

        } catch (NumberFormatException e) {
            // variable case
            this.isVariable = true;
            this.variable = value;
        }
    }

    @Override
    public Variable evaluate(VariableScope outputScope) {

        if (isVariable) {
            return variableManagementUseCase.getVariableByIdentifier(this.variable);

        } else {
            Variable temp = variableManagementUseCase.assignIntVar(outputScope, this.toString());
            instructionGenerationUseCase.writeIntVar(this.number, temp);
            return temp;
        }
    }

    @Override
    public boolean isValue() {
        return !isVariable;
    }

    @Override
    public int getValue() {
        // INV: only perform this action if value exists
        if (!this.isValue()) {
            throw new IllegalCallerException("Attempt to get value of non-pre evaluatable");
        }

        return this.number;
    }
}
