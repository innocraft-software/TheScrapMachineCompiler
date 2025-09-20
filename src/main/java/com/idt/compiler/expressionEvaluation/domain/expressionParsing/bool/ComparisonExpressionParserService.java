package com.idt.compiler.expressionEvaluation.domain.expressionParsing.bool;

import com.idt.compiler.expressionEvaluation.domain.NonOperationException;
import com.idt.compiler.expressionEvaluation.domain.bool.comparisonOperation.*;
import com.idt.compiler.processorAbstraction.application.port.in.InstructionGenerationUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.VariableManagementUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ComparisonExpressionParserService {

    private final InstructionGenerationUseCase instructionGenerationUseCase;

    private final VariableManagementUseCase variableManagementUseCase;

    public ComparisonOperation parseOperation(Character operationCharacter) throws NonOperationException {
        return switch (operationCharacter) {
            case '=' -> new EqualsOperation(this.instructionGenerationUseCase, this.variableManagementUseCase);
            case '>' -> new GreaterThanOperation(this.instructionGenerationUseCase, this.variableManagementUseCase);
            case '<' -> new LessThanOperation(this.instructionGenerationUseCase, this.variableManagementUseCase);
            case '$' ->
                    new GreaterThanEqualOperation(this.instructionGenerationUseCase, this.variableManagementUseCase);
            case '£' -> new LessThanEqualOperation(this.instructionGenerationUseCase, this.variableManagementUseCase);
            default -> throw new NonOperationException();
        };
    }
}
