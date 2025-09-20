package com.idt.compiler.expressionEvaluation.domain.expressionStrings;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.expressionEvaluation.domain.expressionParsing.ExpressionBraceValidityUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BooleanExpressionString {
    private final String expressionString;
    private final InlinableExpressionStringBuilder inlinedString;
    private final List<ComparisonExpressionString> containedComparisons;


    // Initialization
    public BooleanExpressionString(String expressionString) {
        this.expressionString = expressionString.replaceAll("\\s", "");
        this.inlinedString = new InlinableExpressionStringBuilder();
        this.containedComparisons = new ArrayList<>();

        // INV check for illegal symbols
        if (this.expressionString.contains("$") || this.expressionString.contains("£")) {
            throw new IllegalArgumentException("A boolean expression string is not allowed to contain $ or £");
        }

        extractComparisons();
    }

    private void extractComparisons() {

        // Pattern to match comparison operator and surrounding expression until next boolean operator
        Pattern pattern = Pattern.compile("[^><=&|!]+([><=!]=|<|>)[^><=&|!]+");


        // temporary memory
        String unprocessedPart = this.expressionString;

        while (true) {
            Matcher matcher = pattern.matcher(unprocessedPart);

            if (!matcher.find()) {
                this.inlinedString.append(unprocessedPart);
                break;
            }

            // find the comparison and process the substring to have the correct number of braces
            String comparison = ExpressionBraceValidityUtility.convertExpressionToValidBraces(matcher.group());
            ComparisonExpressionString extractedComparisonExpression = new ComparisonExpressionString(comparison);

            // fetch the residual strings
            String[] residual = unprocessedPart.split(Pattern.quote(comparison), 2);

            String beforeComparison = residual[0];
            String afterComparison = residual[1];

            // append the expressions
            this.inlinedString.append(beforeComparison);
            this.inlinedString.append(extractedComparisonExpression);

            this.containedComparisons.add(extractedComparisonExpression);

            unprocessedPart = afterComparison;

        }
    }

    /// end initialization


    public BooleanExpressionString embedPreEvaluatedComparisons(Map<ComparisonExpressionString, Variable> preEvaluatedComparisons) {

        for (ComparisonExpressionString comparisonExpression : preEvaluatedComparisons.keySet()) {
            this.inlinedString.mapExpressionString(comparisonExpression, preEvaluatedComparisons.get(comparisonExpression));
        }

        return new BooleanExpressionString(this.inlinedString.build());
    }


    public BooleanExpressionString getPrepareForBooleanEvaluation() {

        String result = this.expressionString;

        // translate to single symbol operators
        result = result.replace("||", "|");
        result = result.replace("&&", "&");
        result = result.replace(">=", "$");
        result = result.replace("<=", "£");
        result = result.replace("==", "=");
        return new BooleanExpressionString(result);
    }

    public List<ComparisonExpressionString> getContainedComparisons() {
        return containedComparisons;
    }

    public String getExpressionString() {
        return expressionString;
    }


}
