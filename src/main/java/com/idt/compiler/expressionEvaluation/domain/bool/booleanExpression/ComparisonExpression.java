package com.idt.compiler.expressionEvaluation.domain.bool.booleanExpression;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.common.globalDomain.variableManagement.VariableScope;
import com.idt.compiler.expressionEvaluation.domain.arithmetic.arithmeticExpression.ArithmeticExpression;
import com.idt.compiler.expressionEvaluation.domain.bool.comparisonOperation.ComparisonOperation;
import com.idt.compiler.processorAbstraction.application.port.in.ScopeManagementUseCase;

public class ComparisonExpression implements BooleanExpression {

    private final ScopeManagementUseCase scopeManagementUseCase;

    private final ArithmeticExpression value1;
    private final ArithmeticExpression value2;
    private final ComparisonOperation comparisonOperation;

    public ComparisonExpression(ArithmeticExpression value1, ArithmeticExpression value2, ComparisonOperation comparisonOperation, ScopeManagementUseCase scopeManagementUseCase) {
        this.value1 = value1;
        this.value2 = value2;
        this.comparisonOperation = comparisonOperation;
        this.scopeManagementUseCase = scopeManagementUseCase;
    }

    @Override
    public Variable evaluate(VariableScope outputScope) {

        VariableScope innerScope = scopeManagementUseCase.createScope("CMP_InnerScopeOf_" + this.hashCode());

        Variable value1 = this.value1.evaluate(innerScope);
        Variable value2 = this.value2.evaluate(innerScope);

        Variable result = comparisonOperation.evaluate(value1, value2, outputScope);

        // delete all variables in the scope of this operation
        scopeManagementUseCase.exitScope(innerScope);

        return result;
    }

}