package com.idt.compiler.expressionEvaluation.domain.expressionParsing;

import com.idt.compiler.expressionEvaluation.domain.NonOperationException;
import com.idt.compiler.expressionEvaluation.domain.SyntaxErrorException;
import com.idt.compiler.expressionEvaluation.domain.arithmetic.arithmeticExpression.ArithmeticExpression;
import com.idt.compiler.expressionEvaluation.domain.arithmetic.arithmeticExpression.ArithmeticValue;
import com.idt.compiler.expressionEvaluation.domain.arithmetic.arithmeticExpression.ConcreteArithmeticExpression;
import com.idt.compiler.expressionEvaluation.domain.bool.booleanExpression.BooleanExpression;
import com.idt.compiler.expressionEvaluation.domain.bool.booleanExpression.BooleanValue;
import com.idt.compiler.expressionEvaluation.domain.bool.booleanExpression.ConcreteBooleanExpression;
import com.idt.compiler.expressionEvaluation.domain.bool.booleanExpression.NotExpression;
import com.idt.compiler.expressionEvaluation.domain.expressionParsing.arithmetic.ArithmeticOperationParserService;
import com.idt.compiler.expressionEvaluation.domain.expressionParsing.arithmetic.ArithmeticPostfix;
import com.idt.compiler.expressionEvaluation.domain.expressionParsing.bool.BooleanOperationParserService;
import com.idt.compiler.expressionEvaluation.domain.expressionParsing.bool.BooleanPostfix;
import com.idt.compiler.processorAbstraction.application.port.in.InstructionGenerationUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.ScopeManagementUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.VariableManagementUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EmptyStackException;
import java.util.Stack;

@Component
@RequiredArgsConstructor
public class ExpressionBuilderService {

    private final ArithmeticOperationParserService arithmeticOperationParserService;

    private final BooleanOperationParserService booleanOperationParserService;

    private final ScopeManagementUseCase scopeManagementUseCase;

    private final VariableManagementUseCase variableManagementUseCase;

    private final InstructionGenerationUseCase instructionGenerationUseCase;

    public ArithmeticExpression submitArithmeticExpression(ArithmeticPostfix arithmeticPostfix) throws SyntaxErrorException {

        Stack<ArithmeticExpression> processExpression = new Stack<>();

        for (String content : arithmeticPostfix.getExpressionElements()) {
            Character operation = content.charAt(0);
            // check if operation or value
            if (arithmeticOperationParserService.isOperation(operation)) {

                // assemble the operation
                ArithmeticExpression expressionInput1;
                ArithmeticExpression expressionInput2;
                try {
                    expressionInput2 = processExpression.pop();
                    expressionInput1 = processExpression.pop();
                } catch (EmptyStackException e) {
                    throw new SyntaxErrorException();
                }

                // push the operation
                try {
                    processExpression.push(ConcreteArithmeticExpression.createConcreteArithmeticExpression(expressionInput1, expressionInput2, arithmeticOperationParserService.parseOperation(operation), scopeManagementUseCase, variableManagementUseCase, instructionGenerationUseCase));
                } catch (NonOperationException e) {
                    throw new SyntaxErrorException();
                }

            } else {
                // if not operation, push a value
                processExpression.push(new ArithmeticValue(content, variableManagementUseCase, instructionGenerationUseCase));
            }
        }

        if (processExpression.size() != 1) {
            throw new SyntaxErrorException();
        }

        // final expression on outer layer
        return processExpression.pop();

    }


    public BooleanExpression submitBooleanExpression(BooleanPostfix booleanPostfix) throws SyntaxErrorException {

        Stack<BooleanExpression> processExpression = new Stack<>();

        for (String content : booleanPostfix.getExpressionElements()) {
            Character operation = content.charAt(0);
            // check if operation or value
            if (booleanOperationParserService.isOperation(operation)) {

                // EXCEPTION: not operator
                if (operation.equals('!')) {
                    // assemble the operation
                    BooleanExpression expressionInput1;
                    try {
                        expressionInput1 = processExpression.pop();
                    } catch (EmptyStackException e) {
                        throw new SyntaxErrorException();
                    }
                    // push the operation
                    processExpression.push(new NotExpression(expressionInput1, scopeManagementUseCase, instructionGenerationUseCase, variableManagementUseCase));
                } else {

                    // assemble the operation
                    BooleanExpression expressionInput1;
                    BooleanExpression expressionInput2;
                    try {
                        expressionInput2 = processExpression.pop();
                        expressionInput1 = processExpression.pop();
                    } catch (EmptyStackException e) {
                        throw new SyntaxErrorException();
                    }

                    // push the operation
                    try {
                        processExpression.push(ConcreteBooleanExpression.createConcreteBooleanExpression(expressionInput1, expressionInput2, booleanOperationParserService.parseOperation(operation), scopeManagementUseCase));
                    } catch (NonOperationException e) {
                        throw new SyntaxErrorException();
                    }
                }

            } else {
                // if not operation, push a value
                processExpression.push(new BooleanValue(content, variableManagementUseCase, instructionGenerationUseCase));
            }
        }

        if (processExpression.size() != 1) {
            throw new SyntaxErrorException();
        }

        // final expression on outer layer
        return processExpression.pop();

    }
}
