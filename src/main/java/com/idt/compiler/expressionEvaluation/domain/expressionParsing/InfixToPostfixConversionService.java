package com.idt.compiler.expressionEvaluation.domain.expressionParsing;

import com.idt.compiler.expressionEvaluation.domain.SyntaxErrorException;
import com.idt.compiler.expressionEvaluation.domain.expressionParsing.arithmetic.ArithmeticOperationParserService;
import com.idt.compiler.expressionEvaluation.domain.expressionParsing.arithmetic.ArithmeticPostfix;
import com.idt.compiler.expressionEvaluation.domain.expressionParsing.bool.BooleanOperationParserService;
import com.idt.compiler.expressionEvaluation.domain.expressionParsing.bool.BooleanPostfix;
import com.idt.compiler.expressionEvaluation.domain.expressionStrings.ArithmeticExpressionString;
import com.idt.compiler.expressionEvaluation.domain.expressionStrings.BooleanExpressionString;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Stack;

// Architecture for arithmetic not clean but module is kept this way as working standalone unit
@Component
@RequiredArgsConstructor
public class InfixToPostfixConversionService {

    private final ArithmeticOperationParserService arithmeticOperationParserService;

    private final BooleanOperationParserService booleanOperationParserService;

    public ArithmeticPostfix interpret(ArithmeticExpressionString input) throws SyntaxErrorException {
        return new ArithmeticPostfix(
                appendSpaceBeforeOperators(
                        infixToPostfix(
                                appendSpaceAfterOperators(
                                        input.expressionString()
                                        , arithmeticOperationParserService
                                )
                        ), arithmeticOperationParserService
                )
        );
    }

    public BooleanPostfix interpret(BooleanExpressionString input) throws SyntaxErrorException {
        return new BooleanPostfix(
                appendSpaceBeforeOperators(
                        infixToPostfix(
                                appendSpaceAfterOperators(
                                        input.getExpressionString(), booleanOperationParserService
                                )
                        ), booleanOperationParserService
                )
        );
    }

    private String appendSpaceAfterOperators(String input, OperationDetector operationDetector) {
        StringBuilder result = new StringBuilder();

        for (char c : input.toCharArray()) {
            result.append(c);
            if (operationDetector.isOperation(c)) {
                result.append(' ');
            }
        }

        return result.toString();
    }

    private String appendSpaceBeforeOperators(String input, OperationDetector operationDetector) {
        StringBuilder result = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (operationDetector.isOperation(c)) {
                result.append(' ');
                result.append(c);
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    private String infixToPostfix(String infixExpression) throws SyntaxErrorException {
        StringBuilder postfix = new StringBuilder();
        Stack<Character> stack = new Stack<>();

        for (char ch : infixExpression.toCharArray()) {

            if (String.valueOf(ch).matches("[A-Za-z@_0-9 .]")) {
                postfix.append(ch);
            } else if (ch == '(') {
                stack.push(ch);
            } else if (ch == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    postfix.append(stack.pop());
                }
                stack.pop(); // Pop '('
            } else {
                while (!stack.isEmpty() && precedence(ch) <= precedence(stack.peek())) {
                    postfix.append(stack.pop());
                }
                stack.push(ch);
            }
        }

        while (!stack.isEmpty()) {
            postfix.append(stack.pop());
        }

        return postfix.toString();
    }

    private int precedence(char operator) throws SyntaxErrorException {
        return switch (operator) {
            case '+', '-', '|' -> 1;
            case '*', '/', '&' -> 2;
            case '!' -> 3;
            case '(' -> -1;
            default -> throw new SyntaxErrorException("Illegal Character in Expression: " + operator);
        };
    }
}
