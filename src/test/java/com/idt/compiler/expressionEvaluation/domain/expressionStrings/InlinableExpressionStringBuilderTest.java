package com.idt.compiler.expressionEvaluation.domain.expressionStrings;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InlinableExpressionStringBuilderTest {

    @Test
    void test_givenString_whenUsingBuilder_thenStringCanBeBuilt() {
        // given
        String test = "test";

        // when
        InlinableExpressionStringBuilder builder = new InlinableExpressionStringBuilder();
        builder.append(test);
        String output = builder.build();

        // then
        assertEquals("test", output);

    }

    @Test
    void test_givenExpression_whenUsingBuilderWithoutMappingExpression_thenError() {
        // given
        ComparisonExpressionString comparison = new ComparisonExpressionString("A>B");

        // when
        InlinableExpressionStringBuilder builder = new InlinableExpressionStringBuilder();
        builder.append(comparison);

        // then
        assertThrows(IllegalCallerException.class, builder::build);
    }

    @Test
    void test_givenExpressionAndAssociatedVariable_whenBuildingAfterMapping_thenCorrectVariableNameReturned() {
        // given
        ComparisonExpressionString comparison = new ComparisonExpressionString("A>B");
        Variable associatedVariable = new Variable(0, "associatedWithTestVariable");

        // when
        InlinableExpressionStringBuilder builder = new InlinableExpressionStringBuilder();
        builder.append(comparison);
        builder.mapExpressionString(comparison, associatedVariable);

        String output = builder.build();

        // then
        assertEquals("associatedWithTestVariable", output);
    }

    @Test
    void test_givenExpressionAndAssociatedVariableAndString_whenBuildingBuilderAfterMapping_thenCorrectStringOutputReturned() {
        // given
        ComparisonExpressionString comparison = new ComparisonExpressionString("A>B");
        Variable associatedVariable = new Variable(0, "associatedWithTestVariable");
        String postExpressionString = "||C";

        // when
        InlinableExpressionStringBuilder builder = new InlinableExpressionStringBuilder();
        builder.append(comparison);
        builder.append(postExpressionString);

        builder.mapExpressionString(comparison, associatedVariable);

        String output = builder.build();

        // then
        assertEquals("associatedWithTestVariable||C", output);
    }

    @Test
    void test_givenExpressionAndAssociatedVariableAndStringWithPrependedEmptyString_whenBuildingBuilderAfterMapping_thenCorrectStringOutputReturned() {
        // given
        ComparisonExpressionString comparison = new ComparisonExpressionString("A>B");
        Variable associatedVariable = new Variable(0, "associatedWithTestVariable");
        String postExpressionString = "||C";

        // when
        InlinableExpressionStringBuilder builder = new InlinableExpressionStringBuilder();
        builder.append("");
        builder.append(comparison);
        builder.append(postExpressionString);

        builder.mapExpressionString(comparison, associatedVariable);

        String output = builder.build();

        // then
        assertEquals("associatedWithTestVariable||C", output);
    }

    @Test
    void test_givenExpressionAndTwoAssociatedVariablesAndStrings_whenBuildingBuilderAfterMappingInInverseOrder_thenCorrectStringOutputReturned() {
        // given
        ComparisonExpressionString comparison1 = new ComparisonExpressionString("A>B");
        Variable associatedVariableWithComparison1 = new Variable(0, "associatedWithComparison1");

        String postExpression1String = "||C&&";

        ComparisonExpressionString comparison2 = new ComparisonExpressionString("D<E");
        Variable associatedVariableWithComparison2 = new Variable(0, "associatedWithComparison2");

        String postExpression2String = "||F";


        // when
        InlinableExpressionStringBuilder builder = new InlinableExpressionStringBuilder();
        builder.append(comparison1);
        builder.append(postExpression1String);
        builder.append(comparison2);
        builder.append(postExpression2String);

        builder.mapExpressionString(comparison2, associatedVariableWithComparison2);
        builder.mapExpressionString(comparison1, associatedVariableWithComparison1);

        String output = builder.build();

        // then
        assertEquals("associatedWithComparison1||C&&associatedWithComparison2||F", output);
    }


}