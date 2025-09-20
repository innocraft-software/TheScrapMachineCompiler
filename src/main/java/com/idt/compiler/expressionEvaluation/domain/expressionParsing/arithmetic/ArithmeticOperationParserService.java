package com.idt.compiler.expressionEvaluation.domain.expressionParsing.arithmetic;

import com.idt.compiler.expressionEvaluation.domain.NonOperationException;
import com.idt.compiler.expressionEvaluation.domain.arithmetic.arithmeticOperation.*;
import com.idt.compiler.expressionEvaluation.domain.expressionParsing.OperationDetector;
import com.idt.compiler.processorAbstraction.application.port.in.InstructionGenerationUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.VariableManagementUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArithmeticOperationParserService implements OperationDetector {

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

    public ArithmeticOperation parseOperation(Character operationCharacter) throws NonOperationException {
        return switch (operationCharacter) {
            case '+' -> new AddOperation(this.instructionGenerationUseCase, this.variableManagementUseCase);
            case '-' -> new SubtractOperation(this.instructionGenerationUseCase, this.variableManagementUseCase);
            case '*' -> new MultiplyOperation(this.instructionGenerationUseCase, this.variableManagementUseCase);
            case '/' -> new DivideOperation(this.instructionGenerationUseCase, this.variableManagementUseCase);
            default -> throw new NonOperationException();
        };
    }
}
