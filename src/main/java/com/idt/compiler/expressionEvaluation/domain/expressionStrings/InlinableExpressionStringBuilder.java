package com.idt.compiler.expressionEvaluation.domain.expressionStrings;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;

import java.util.ArrayList;
import java.util.List;

public class InlinableExpressionStringBuilder {

    private final List<BuildContainer> containerList;

    public InlinableExpressionStringBuilder() {
        this.containerList = new ArrayList<>();
    }


    public void append(String string) {
        this.containerList.add(new StringBuildContainer(string));
    }

    public void append(ComparisonExpressionString expression) {
        this.containerList.add(new ExpressionStringContainer(expression));
    }

    public void mapExpressionString(ComparisonExpressionString comparisonExpressionString, Variable evaluatedIn) {
        for (BuildContainer container : this.containerList) {
            if (container.holdsExactExpressionString(comparisonExpressionString)) {
                // cast can not fail as we query exact container
                ExpressionStringContainer castedContainer = (ExpressionStringContainer) container;
                castedContainer.mapVariable(evaluatedIn);
            }
        }
    }

    public String build() {
        StringBuilder outputBuilder = new StringBuilder();
        for (BuildContainer container : this.containerList) {
            String blockContent = container.generateStringComponent();

            // INV: build before completed Initialization
            if (blockContent == null) {
                throw new IllegalCallerException("Attempt to build a Inlinable String builder before all variables were initialized ");
            }

            outputBuilder.append(blockContent);
        }

        return outputBuilder.toString();
    }


    // Containers
    private interface BuildContainer {
        String generateStringComponent();

        boolean holdsExactExpressionString(ComparisonExpressionString comparisonExpressionString);

    }

    private static class StringBuildContainer implements BuildContainer {

        private final String containedString;

        private StringBuildContainer(String string) {
            containedString = string;
        }

        @Override
        public String generateStringComponent() {
            return containedString;
        }

        @Override
        public boolean holdsExactExpressionString(ComparisonExpressionString comparisonExpressionString) {
            return false;
        }
    }

    private static class ExpressionStringContainer implements BuildContainer {

        private final ComparisonExpressionString containedExpressionString;

        private Variable associatedVariable;

        private ExpressionStringContainer(ComparisonExpressionString expressionString) {
            this.containedExpressionString = expressionString;
        }

        public void mapVariable(Variable variable) {
            this.associatedVariable = variable;
        }

        @Override
        public String generateStringComponent() {
            if (this.associatedVariable == null) {
                return null;
            }
            return associatedVariable.getName();
        }

        @Override
        public boolean holdsExactExpressionString(ComparisonExpressionString comparisonExpressionString) {
            return this.containedExpressionString.equals(comparisonExpressionString);
        }
    }
}
