package com.idt.compiler.expressionEvaluation.domain.bool.booleanExpression;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.common.globalDomain.variableManagement.VariableScope;
import com.idt.compiler.expressionEvaluation.domain.bool.booleanOperation.BooleanOperation;
import com.idt.compiler.processorAbstraction.application.port.in.ScopeManagementUseCase;

public class ConcreteBooleanExpression implements BooleanExpression {

    private final ScopeManagementUseCase scopeManagementUseCase;

    private final BooleanExpression value1;
    private final BooleanExpression value2;
    private final BooleanOperation operation;

    private ConcreteBooleanExpression(BooleanExpression value1, BooleanExpression value2, BooleanOperation operation, ScopeManagementUseCase scopeManagementUseCase) {
        this.value1 = value1;
        this.value2 = value2;
        this.operation = operation;
        this.scopeManagementUseCase = scopeManagementUseCase;
    }

    public static BooleanExpression createConcreteBooleanExpression(BooleanExpression value1, BooleanExpression value2, BooleanOperation operation, ScopeManagementUseCase scopeManagementUseCase) {

        // else return the expression
        return new ConcreteBooleanExpression(value1, value2, operation, scopeManagementUseCase);
    }

    @Override
    public Variable evaluate(VariableScope outputScope) {

        VariableScope innerScope = scopeManagementUseCase.createScope("CB_InnerScopeOf_" + this.hashCode());

        Variable value1;
        Variable value2;

        // flip the execution order in case the second expression is not a value, this will prevent values of outer expressions to be written early
        if (this.value2.getClass() == ConcreteBooleanExpression.class) {
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
}
