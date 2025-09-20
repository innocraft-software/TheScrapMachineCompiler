package com.idt.compiler.expressionEvaluation.domain.expressionParsing.bool;

import com.idt.compiler.expressionEvaluation.domain.NonOperationException;
import com.idt.compiler.expressionEvaluation.domain.bool.booleanOperation.AndOperation;
import com.idt.compiler.expressionEvaluation.domain.bool.booleanOperation.BooleanOperation;
import com.idt.compiler.expressionEvaluation.domain.bool.booleanOperation.NotOperation;
import com.idt.compiler.expressionEvaluation.domain.bool.booleanOperation.OrOperation;
import com.idt.compiler.expressionEvaluation.domain.expressionParsing.OperationDetector;
import com.idt.compiler.processorAbstraction.application.port.in.InstructionGenerationUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.VariableManagementUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BooleanOperationParserService implements OperationDetector {

    private final InstructionGenerationUseCase instructionGenerationUseCase;
    private final VariableManagementUseCase variableManagementUseCase;

    @Override
    public boolean isOperation(Character character) {
        try {
            parseOperation(character);
            return true;
        } catch (NonOperationException e) {
            return false;
        }
    }

    public BooleanOperation parseOperation(Character operationCharacter) throws NonOperationException {
        return switch (operationCharacter) {
            case '&' -> new AndOperation(this.instructionGenerationUseCase, this.variableManagementUseCase);
            case '|' -> new OrOperation(this.instructionGenerationUseCase, this.variableManagementUseCase);
            case '!' -> new NotOperation(this.instructionGenerationUseCase, this.variableManagementUseCase);
            default -> throw new NonOperationException();
        };
    }
}
