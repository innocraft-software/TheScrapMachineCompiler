package com.idt.compiler.expressionEvaluation.domain.arithmetic.arithmeticExpression;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.common.globalDomain.variableManagement.VariableScope;
import com.idt.compiler.expressionEvaluation.domain.arithmetic.arithmeticOperation.ArithmeticOperation;
import com.idt.compiler.processorAbstraction.application.port.in.InstructionGenerationUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.ScopeManagementUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.VariableManagementUseCase;

public class ConcreteArithmeticExpression implements ArithmeticExpression {

    private final ScopeManagementUseCase scopeManagementUseCase;

    private final ArithmeticExpression value1;
    private final ArithmeticExpression value2;
    private final ArithmeticOperation operation;

    private ConcreteArithmeticExpression(ArithmeticExpression value1, ArithmeticExpression value2, ArithmeticOperation operation, ScopeManagementUseCase scopeManagementUseCase) {
        this.value1 = value1;
        this.value2 = value2;
        this.operation = operation;
        this.scopeManagementUseCase = scopeManagementUseCase;
    }

    public static ArithmeticExpression createConcreteArithmeticExpression(ArithmeticExpression value1, ArithmeticExpression value2, ArithmeticOperation operation, ScopeManagementUseCase scopeManagementUseCase, VariableManagementUseCase variableManagementUseCase, InstructionGenerationUseCase instructionGenerationUseCase) {
        // check if the expression can be pre-evaluated at compile time
        if (value1.isValue() && value2.isValue()) {
            PreEvaluatableNumeric preEvaluate1 = (PreEvaluatableNumeric) value1;
            PreEvaluatableNumeric preEvaluate2 = (PreEvaluatableNumeric) value2;

            // pre evaluate
            int preEvaluation = operation.performOperationAtCompileTime(preEvaluate1.getValue(), preEvaluate2.getValue());

            // return the pre-evaluation
            return new ArithmeticValue(String.valueOf(preEvaluation), variableManagementUseCase, instructionGenerationUseCase);

        }

        // else return the expression
        return new ConcreteArithmeticExpression(value1, value2, operation, scopeManagementUseCase);
    }

    @Override
    public Variable evaluate(VariableScope outputScope) {

        VariableScope innerScope = scopeManagementUseCase.createScope("InnerScopeOf_" + this.hashCode());

        Variable value1;
        Variable value2;
        // flip the execution order in case the second expression is not a value, this will prevent values of outer expressions to be written early
        if (this.value2.getClass() == ConcreteArithmeticExpression.class) {
            value2 = this.value2.evaluate(innerScope);
            value1 = this.value1.evaluate(innerScope);
        } else {
            value1 = this.value1.evaluate(innerScope);
            value2 = this.value2.evaluate(innerScope);
        }

        Variable result = this.operation.evaluate(value1, value2, outputScope);

        // delete all variables in the scope of this operation
        scopeManagementUseCase.exitScope(innerScope);

        return result;
    }

    @Override
    public boolean isValue() {
        return false;
    }
}
