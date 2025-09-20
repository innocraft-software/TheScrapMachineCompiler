package com.idt.compiler.expressionEvaluation.domain.expressionParsing;

public class ExpressionBraceValidityUtility {
    private static int hasValidBraceCount(String expression) {
        Integer braceCount = 0;
        for (char c : expression.toCharArray()) {
            if (c == '(') braceCount++;
            else if (c == ')') braceCount--;
        }
        return braceCount.compareTo(0);
    }

    public static String convertExpressionToValidBraces(String input) {
        while (hasValidBraceCount(input) != 0) {
            if (hasValidBraceCount(input) > 0) {
                input = input.substring(1);
            } else {
                input = input.substring(0, input.length() - 1);
            }
        }
        return input;
    }
}
