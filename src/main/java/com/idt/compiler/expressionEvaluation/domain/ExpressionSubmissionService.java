package com.idt.compiler.expressionEvaluation.domain;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.common.globalDomain.variableManagement.VariableScope;
import com.idt.compiler.expressionEvaluation.domain.arithmetic.arithmeticExpression.ArithmeticExpression;
import com.idt.compiler.expressionEvaluation.domain.bool.booleanExpression.BooleanExpression;
import com.idt.compiler.expressionEvaluation.domain.bool.booleanExpression.ComparisonExpression;
import com.idt.compiler.expressionEvaluation.domain.bool.comparisonOperation.ComparisonOperation;
import com.idt.compiler.expressionEvaluation.domain.expressionParsing.ExpressionBuilderService;
import com.idt.compiler.expressionEvaluation.domain.expressionParsing.InfixToPostfixConversionService;
import com.idt.compiler.expressionEvaluation.domain.expressionParsing.arithmetic.ArithmeticPostfix;
import com.idt.compiler.expressionEvaluation.domain.expressionParsing.bool.BooleanPostfix;
import com.idt.compiler.expressionEvaluation.domain.expressionParsing.bool.ComparisonExpressionParserService;
import com.idt.compiler.expressionEvaluation.domain.expressionStrings.ArithmeticExpressionString;
import com.idt.compiler.expressionEvaluation.domain.expressionStrings.BooleanExpressionString;
import com.idt.compiler.expressionEvaluation.domain.expressionStrings.ComparisonExpressionString;
import com.idt.compiler.processorAbstraction.application.port.in.ScopeManagementUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ExpressionSubmissionService {

    private final InfixToPostfixConversionService infixToPostfixConversionService;
    private final ExpressionBuilderService expressionBuilderService;

    private final ScopeManagementUseCase scopeManagementUseCase;

    public final ComparisonExpressionParserService comparisonExpressionParserService;

    public ArithmeticExpression createArithmeticExpression(ArithmeticExpressionString arithmeticExpressionString) {

        ArithmeticPostfix postfix = infixToPostfixConversionService.interpret(arithmeticExpressionString);

        return expressionBuilderService.submitArithmeticExpression(postfix);

    }

    public Variable evaluateBooleanExpression(BooleanExpressionString booleanExpressionString, VariableScope outputScope) {

        VariableScope evaluationScope = scopeManagementUseCase.createScope("InnerScopeOf_" + this.hashCode());

        List<ComparisonExpressionString> containedComparisons = booleanExpressionString.getContainedComparisons();

        Map<ComparisonExpressionString, Variable> comparisonResults = new HashMap<>();

        for (ComparisonExpressionString comparison : containedComparisons) {
            ArithmeticExpressionString lhs = comparison.getComparisonLHS();
            ArithmeticExpressionString rhs = comparison.getComparisonRHS();

            Character comparisonOperator = comparison.getComparisonOperationCharacter();

            ArithmeticExpression lhsExpression = createArithmeticExpression(lhs);
            ArithmeticExpression rhsExpression = createArithmeticExpression(rhs);

            ComparisonOperation operation;
            try {
                if (comparisonOperator == null) {
                    throw new SyntaxErrorException("Invalid comparison");
                }
                operation = comparisonExpressionParserService.parseOperation(comparisonOperator);
            } catch (NonOperationException e) {
                throw new SyntaxErrorException();
            }

            ComparisonExpression comparisonExpression = new ComparisonExpression(lhsExpression, rhsExpression, operation, scopeManagementUseCase);

            Variable comparisonResult = comparisonExpression.evaluate(evaluationScope);

            comparisonResults.put(comparison, comparisonResult);
        }
        // map the variables to the comparisons in string
        BooleanExpressionString comparisonLessExpressionString = booleanExpressionString.embedPreEvaluatedComparisons(comparisonResults);

        BooleanExpressionString preProcessedString = comparisonLessExpressionString.getPrepareForBooleanEvaluation();

        BooleanPostfix postfix = infixToPostfixConversionService.interpret(preProcessedString);

        BooleanExpression fullExpression = expressionBuilderService.submitBooleanExpression(postfix);

        Variable outputVariable = fullExpression.evaluate(outputScope);

        scopeManagementUseCase.exitScope(evaluationScope);

        return outputVariable;

    }
}
