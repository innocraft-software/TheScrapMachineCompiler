package com.idt.compiler.expressionEvaluation.domain.arithmetic.arithmeticExpression;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.common.globalDomain.variableManagement.VariableScope;

public interface ArithmeticExpression {

    Variable evaluate(VariableScope outputScope);

    /**
     * return true of the expression can be pre-evaluated at compile time
     **/
    boolean isValue();
}
