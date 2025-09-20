package com.idt.compiler.expressionEvaluation.domain.expressionParsing.bool;

import lombok.Value;

import java.util.Arrays;

@Value
public class BooleanPostfix {
    String postfixString;

    public BooleanPostfix(String postfixString) {
        this.postfixString = postfixString;
    }

    public String[] getExpressionElements() {

        return Arrays.stream(postfixString.split(" "))
                .filter(s -> !s.isEmpty())
                .toList()
                .toArray(String[]::new);
    }

}
