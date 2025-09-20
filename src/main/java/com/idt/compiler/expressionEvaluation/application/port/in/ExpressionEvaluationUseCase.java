package com.idt.compiler.expressionEvaluation.application.port.in;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.common.globalDomain.variableManagement.VariableScope;

public interface ExpressionEvaluationUseCase {
    Variable evaluateExpression(String expression, VariableScope evaluationOutputScope);

    void evaluateExpressionTo(String expresseion, Variable destination);
}
