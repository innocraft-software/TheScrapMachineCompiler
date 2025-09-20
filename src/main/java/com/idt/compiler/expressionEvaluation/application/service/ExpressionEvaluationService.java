package com.idt.compiler.expressionEvaluation.application.service;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.common.globalDomain.variableManagement.VariableScope;
import com.idt.compiler.expressionEvaluation.application.port.in.ExpressionEvaluationUseCase;
import com.idt.compiler.expressionEvaluation.domain.ExpressionSubmissionService;
import com.idt.compiler.expressionEvaluation.domain.expressionStrings.ArithmeticExpressionString;
import com.idt.compiler.expressionEvaluation.domain.expressionStrings.BooleanExpressionString;
import com.idt.compiler.processorAbstraction.application.port.in.InstructionGenerationUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.ScopeManagementUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.VariableManagementUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExpressionEvaluationService implements ExpressionEvaluationUseCase {

    private final ExpressionSubmissionService expressionSubmissionService;
    private final VariableManagementUseCase variableManagementUseCase;
    private final ScopeManagementUseCase scopeManagementUseCase;

    // used for localizing variables only
    private final InstructionGenerationUseCase instructionGenerationUseCase;

    @Override
    public Variable evaluateExpression(String expression, VariableScope evaluationOutputScope) {
        try {
            // most un-elegant method you have ever seen hehe :D
            // move to the catch block if string is "true" or "false"
            if (expression.equals("true") || expression.equals("false")) {
                throw new IllegalArgumentException("actually legal but i just want to run the catch block");
            }


            ArithmeticExpressionString arithmeticExpressionString = new ArithmeticExpressionString(expression);
            return expressionSubmissionService.createArithmeticExpression(arithmeticExpressionString).evaluate(evaluationOutputScope);
        } catch (IllegalArgumentException e) {
            BooleanExpressionString booleanExpressionString = new BooleanExpressionString(expression);
            return expressionSubmissionService.evaluateBooleanExpression(booleanExpressionString, evaluationOutputScope);
        }
    }

    @Override
    public void evaluateExpressionTo(String expresseion, Variable destination) {
        // create a new output scope
        // as we evaluate directly to a variable, we do not want to use the active scope as else we have the pre-copy value orphaned
        VariableScope workScope = scopeManagementUseCase.createScope("workScope_" + UUID.randomUUID());

        // perform the operation in the work scope
        Variable result = evaluateExpression(expresseion, workScope);

        // ensure the variables are available in register
        instructionGenerationUseCase.loadVariableToRegister(result);

        // copy the result to the output variable
        variableManagementUseCase.inRegisterDeepCopy(result, destination);

        // delete the work scope
        scopeManagementUseCase.exitScope(workScope);
    }
}
