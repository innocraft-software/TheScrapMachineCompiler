package com.idt;

import com.idt.compiler.assembly.application.port.in.ManageApplicationUseCase;
import com.idt.compiler.expressionEvaluation.application.port.in.ExpressionEvaluationUseCase;
import com.idt.compiler.interpreter.application.port.in.ControlInterpreterSessionUseCase;
import com.idt.compiler.interpreter.application.port.in.ManageInterpreterSessionUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.InstructionGenerationUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.ScopeManagementUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.VariableManagementUseCase;
import com.idt.compiler.processorAbstraction.application.service.InstructionGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestContext {

    private static InstructionGenerationUseCase instructionGenerationUseCase;

    private static ManageApplicationUseCase createApplicationUseCase;

    private static ScopeManagementUseCase scopeManagementUseCase;

    private static ExpressionEvaluationUseCase expressionEvaluationUseCase;

    private static VariableManagementUseCase variableManagementUseCase;

    private static ManageInterpreterSessionUseCase manageInterpreterSessionUseCase;

    private static ControlInterpreterSessionUseCase controlInterpreterSessionUseCase;

    private static InstructionGenerationService instructionGenerationService;

    @Autowired
    public void setMyBean(InstructionGenerationUseCase myBean, ManageApplicationUseCase createApplicationUseCase, ScopeManagementUseCase scopeManagementUseCase, ExpressionEvaluationUseCase expressionEvaluationUseCase, VariableManagementUseCase variableManagementUseCase, ManageInterpreterSessionUseCase interpreterSessionManagementService, ControlInterpreterSessionUseCase controlInterpreterSessionUseCase, InstructionGenerationService instructionGenerationService) {
        TestContext.instructionGenerationUseCase = myBean;
        TestContext.createApplicationUseCase = createApplicationUseCase;
        TestContext.scopeManagementUseCase = scopeManagementUseCase;
        TestContext.expressionEvaluationUseCase = expressionEvaluationUseCase;
        TestContext.variableManagementUseCase = variableManagementUseCase;
        TestContext.manageInterpreterSessionUseCase = interpreterSessionManagementService;
        TestContext.controlInterpreterSessionUseCase = controlInterpreterSessionUseCase;
        TestContext.instructionGenerationService = instructionGenerationService;
    }

    public static InstructionGenerationUseCase getSMG() {
        return instructionGenerationUseCase;
    }

    public static ManageApplicationUseCase getManageApplicationUseCase() {
        return createApplicationUseCase;
    }

    public static ScopeManagementUseCase getScopeManagementUseCase() {
        return scopeManagementUseCase;
    }

    public static ExpressionEvaluationUseCase getExpressionEvaluationUseCase() {
        return expressionEvaluationUseCase;
    }

    public static VariableManagementUseCase getVariableManagementUseCase() {
        return variableManagementUseCase;
    }

    public static ManageInterpreterSessionUseCase getManageInterpreterSessionUseCase() {
        return manageInterpreterSessionUseCase;
    }

    public static ControlInterpreterSessionUseCase getControlInterpreterSessionUseCase() {
        return controlInterpreterSessionUseCase;
    }

    public static InstructionGenerationService getInstructionGenerationService() {
        return instructionGenerationService;
    }
}
