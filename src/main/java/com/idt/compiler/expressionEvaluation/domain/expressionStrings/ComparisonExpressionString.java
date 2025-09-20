package com.idt.compiler.expressionEvaluation.domain.expressionStrings;

import com.idt.compiler.expressionEvaluation.domain.expressionParsing.ExpressionBraceValidityUtility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record ComparisonExpressionString(
        String expressionString
) {

    private static final String COMPARISON_IDENTIFICATION_REGEX = "[><=!]=|<|>|\\$|£|=";


    public ComparisonExpressionString(String expressionString) {
        this.expressionString = expressionString;
        if (!containsComparison()) {
            throw new IllegalCallerException("Attempt to create a comparison expression without comparison");
        }
    }

    private boolean containsComparison() {
        Pattern pattern = Pattern.compile(COMPARISON_IDENTIFICATION_REGEX);
        Matcher matcher = pattern.matcher(this.expressionString);
        return matcher.find();
    }

    public ArithmeticExpressionString getComparisonLHS() {

        String lhs = this.expressionString.split(COMPARISON_IDENTIFICATION_REGEX)[0];
        lhs = ExpressionBraceValidityUtility.convertExpressionToValidBraces(lhs);

        return new ArithmeticExpressionString(lhs);
    }

    public ArithmeticExpressionString getComparisonRHS() {

        String rhs = this.expressionString.split(COMPARISON_IDENTIFICATION_REGEX)[1];
        rhs = ExpressionBraceValidityUtility.convertExpressionToValidBraces(rhs);

        return new ArithmeticExpressionString(rhs);
    }

    public Character getComparisonOperationCharacter() {
        ComparisonExpressionString prepared = this.prepareForEvaluation();

        Pattern pattern = Pattern.compile("[$£=><]");
        Matcher matcher = pattern.matcher(prepared.expressionString());

        if (matcher.find()) {
            return matcher.group().charAt(0);
        } else {
            return null;
        }
    }

    public ComparisonExpressionString prepareForEvaluation() {
        if (this.expressionString.contains("$") || this.expressionString.contains("£")) {
            throw new IllegalArgumentException("A boolean expression string is not allowed to contain $ or £");
        }

        String result = this.expressionString;

        // translate to single symbol operators
        result = result.replace(">=", "$");
        result = result.replace("<=", "£");
        result = result.replace("==", "=");
        return new ComparisonExpressionString(result);
    }


}
