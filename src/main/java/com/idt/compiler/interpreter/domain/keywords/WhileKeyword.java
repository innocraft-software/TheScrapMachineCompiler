package com.idt.compiler.interpreter.domain.keywords;

import com.idt.compiler.interpreter.application.port.out.context.ControlStructureEvaluationPort;
import com.idt.compiler.interpreter.application.port.out.context.DeclareVariablePort;
import com.idt.compiler.interpreter.application.port.out.context.EvaluateToIntVariablePort;
import com.idt.compiler.interpreter.application.port.out.context.GPIOPort;
import com.idt.compiler.interpreter.domain.InterpreterLine;

public class WhileKeyword implements KeywordSplit {

    private final InterpreterLine interpreterLine;

    public WhileKeyword(InterpreterLine interpreterLine) {
        this.interpreterLine = interpreterLine;
    }

    @Override
    public void process(DeclareVariablePort declareVariablePort, EvaluateToIntVariablePort evaluateToIntVariablePort, ControlStructureEvaluationPort controlStructureEvaluationPort, GPIOPort gpioPort) {
        // guard empty
        if (!interpreterLine.hasNextToken()) {
            throw new RuntimeException("Syntax Error in line: " + interpreterLine.getLineNumber());
        }
        // guard incorrect operator
        if (!interpreterLine.getNextToken().equals("while")) {
            throw new RuntimeException("Syntax Error. Invalid var assignment in line: " + interpreterLine.getLineNumber());
        }

        String assignmentExpression = interpreterLine.getRemainingBuffer();

        controlStructureEvaluationPort.startWhile(assignmentExpression);

    }
}
