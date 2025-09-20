package com.idt.compiler.expressionEvaluation.domain.bool.booleanExpression;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.common.globalDomain.variableManagement.VariableScope;

public interface BooleanExpression {

    Variable evaluate(VariableScope outputScope);
}
