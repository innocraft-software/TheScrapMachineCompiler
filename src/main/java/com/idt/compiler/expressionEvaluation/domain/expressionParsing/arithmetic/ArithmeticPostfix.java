package com.idt.compiler.expressionEvaluation.domain.expressionParsing.arithmetic;

import lombok.Value;

@Value
public class ArithmeticPostfix {
    String postfixString;

    public ArithmeticPostfix(String postfixString) {
        this.postfixString = postfixString;
    }

    public String[] getExpressionElements() {
        return postfixString.split(" ");
    }

}
