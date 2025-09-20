package com.idt.compiler.common.adapter.context;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.expressionEvaluation.application.port.in.ExpressionEvaluationUseCase;
import com.idt.compiler.interpreter.application.port.out.context.DeclareVariablePort;
import com.idt.compiler.interpreter.application.port.out.context.EvaluateToIntVariablePort;
import com.idt.compiler.processorAbstraction.application.port.in.VariableManagementUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InstructionFromInterpreterContextAdapter implements DeclareVariablePort, EvaluateToIntVariablePort {

    private final VariableManagementUseCase variableManagementUseCase;
    private final ExpressionEvaluationUseCase expressionEvaluationUseCase;

    @Override
    public void declareIntVariable(String identifier) {
        variableManagementUseCase.assignIntVar(identifier);
    }

    @Override
    public void evaluateExpressionToIntVariable(String destination, String expression) {
        Variable destinationVar = variableManagementUseCase.getVariableByIdentifier(destination);
        expressionEvaluationUseCase.evaluateExpressionTo(expression, destinationVar);
    }
}
