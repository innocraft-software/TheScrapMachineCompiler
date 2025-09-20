package com.idt.compiler.expressionEvaluation.domain.expressionStrings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record ArithmeticExpressionString(
        String expressionString
) {
    public ArithmeticExpressionString(String expressionString) {
        this.expressionString = expressionString.replaceAll("\\s", "");

        // INV: No Comparisons in arithmetic expressions
        if (returnsBoolean()) {
            throw new IllegalArgumentException("Arithmetic expression must not contain comparison or boolean operators");
        }
    }


    public boolean returnsBoolean() {
        String patternString = "[><|&=!]";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(this.expressionString);
        return matcher.find();
    }


}
