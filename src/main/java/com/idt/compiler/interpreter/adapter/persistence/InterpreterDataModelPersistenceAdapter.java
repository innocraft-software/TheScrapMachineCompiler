package com.idt.compiler.interpreter.adapter.persistence;

import com.idt.compiler.common.globalDomain.Instruction;
import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.common.globalDomain.variableManagement.VariableScope;
import com.idt.compiler.expressionEvaluation.application.port.in.ExpressionEvaluationUseCase;
import com.idt.compiler.interpreter.application.port.out.context.ControlStructureEvaluationPort;
import com.idt.compiler.interpreter.application.port.out.context.GPIOPort;
import com.idt.compiler.interpreter.application.port.out.persistence.CreateReaderStatePort;
import com.idt.compiler.interpreter.application.port.out.persistence.LoadReaderStatePort;
import com.idt.compiler.interpreter.domain.InterpreterSession;
import com.idt.compiler.interpreter.domain.controlStructures.ControlStructure;
import com.idt.compiler.interpreter.domain.controlStructures.ControlStructureType;
import com.idt.compiler.interpreter.domain.controlStructures.IfStructure;
import com.idt.compiler.interpreter.domain.controlStructures.WhileStructure;
import com.idt.compiler.processorAbstraction.application.port.in.InstructionGenerationUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.ScopeManagementUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.VariableManagementUseCase;
import com.idt.compiler.processorAbstraction.application.port.out.domainConnectors.GetNextInstructionPort;
import com.idt.compiler.processorAbstraction.application.port.out.persistence.LoadPAModelPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

// I know this is not elegant, but I don't care
// this is now also a context adapter YAY :) This is what happens on insane overtime
@Component
@RequiredArgsConstructor
public class InterpreterDataModelPersistenceAdapter implements CreateReaderStatePort, LoadReaderStatePort, ControlStructureEvaluationPort, GPIOPort {

    private InterpreterSession interpreterSession;
    private final ExpressionEvaluationUseCase expressionEvaluationUseCase;
    private final InstructionGenerationUseCase instructionGenerationUseCase;
    private final ScopeManagementUseCase scopeManagementUseCase;
    // use carefully from here. I know its not supported to be used like that but this project is running out of hand. rather have it finished in some way rather than having to leave it open forever
    private final LoadPAModelPort loadPAModelPort;

    private final GetNextInstructionPort getNextInstructionPort;

    private final VariableManagementUseCase variableManagementUseCase;

    @Override
    public void createReaderState(InterpreterSession interpreterSession) {
        this.interpreterSession = interpreterSession;
    }

    @Override
    public InterpreterSession getReader() {
        return interpreterSession;
    }

    @Override
    public void startIf(String expression) {
        // create a temporary output scope
        // as we evaluate directly to a variable, we do not want to use the active scope as else we have the pre-copy value orphaned
        VariableScope workScope = scopeManagementUseCase.createScope("workScope_" + UUID.randomUUID());

        // evaluate the comparison
        Variable comparison = expressionEvaluationUseCase.evaluateExpression(expression, workScope);

        // start the if in machine code
        Instruction ifStartBranch = instructionGenerationUseCase.ifStart(comparison);

        // delete temp vars
        scopeManagementUseCase.exitScope(workScope);

        // create the scope to be used in the if branch
        VariableScope ifBranchScope = scopeManagementUseCase.createScope("ifStructureLocalScope_" + UUID.randomUUID());

        // push the operation
        this.interpreterSession.getControlStructureManager().newStructure(new IfStructure(ifStartBranch, ifBranchScope));
    }

    @Override
    public void startWhile(String expression) {
        // create a temporary output scope
        // as we evaluate directly to a variable, we do not want to use the active scope as else we have the pre-copy value orphaned
        VariableScope workScope = scopeManagementUseCase.createScope("workScope_" + UUID.randomUUID());

        // ensure no schroedingers register
        loadPAModelPort.getProcessorAbstraction().safeClearAllRegisters();

        // before comparison evaluation branch
        int beforeWhileBranchAddress = getNextInstructionPort.getNextInstruction();

        // evaluate the comparison
        Variable comparison = expressionEvaluationUseCase.evaluateExpression(expression, workScope);

        // start the while in machine code
        Instruction whileStartBranch = instructionGenerationUseCase.whileStart(comparison);

        // delete temp vars
        scopeManagementUseCase.exitScope(workScope);

        // create the scope to be used in the while branch
        VariableScope whileBranchScope = scopeManagementUseCase.createScope("whileStructureLocalScope_" + UUID.randomUUID());

        // push the operation
        this.interpreterSession.getControlStructureManager().newStructure(new WhileStructure(whileStartBranch, whileBranchScope, beforeWhileBranchAddress));
    }

    @Override
    public void endCurrentControlStatement() {
        ControlStructure poppedStructure = this.interpreterSession.getControlStructureManager().pop();
        if (poppedStructure.getControlStructureType().equals(ControlStructureType.IF)) {
            IfStructure ifStructure = (IfStructure) poppedStructure;
            instructionGenerationUseCase.ifEnd(ifStructure.branchInstruction());
        }

        if (poppedStructure.getControlStructureType().equals(ControlStructureType.WHILE)) {
            WhileStructure whileStructure = (WhileStructure) poppedStructure;
            instructionGenerationUseCase.whileEnd(whileStructure.branchInstruction(), whileStructure.whileStartBranchAddress());
        }


        // close the scope of the structure
        scopeManagementUseCase.exitScope(poppedStructure.getLocalScope());
    }

    @Override
    public void setHighGPIO(int gpioId) {
        instructionGenerationUseCase.setHighGPIO(gpioId);
    }

    @Override
    public void setLowGPIO(int gpioId) {
        instructionGenerationUseCase.setLowGPIO(gpioId);
    }

    @Override
    public void readGPIO(int gpioId, String destination) {
        Variable destinationVar = variableManagementUseCase.getVariableByIdentifier(destination);
        instructionGenerationUseCase.readGPIO(gpioId, destinationVar);
    }
}
