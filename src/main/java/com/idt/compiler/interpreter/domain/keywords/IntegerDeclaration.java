package com.idt.compiler.interpreter.domain.keywords;

import com.idt.compiler.interpreter.application.port.out.context.ControlStructureEvaluationPort;
import com.idt.compiler.interpreter.application.port.out.context.DeclareVariablePort;
import com.idt.compiler.interpreter.application.port.out.context.EvaluateToIntVariablePort;
import com.idt.compiler.interpreter.application.port.out.context.GPIOPort;
import com.idt.compiler.interpreter.domain.InterpreterLine;

public class IntegerDeclaration implements KeywordSplit {

    private final InterpreterLine interpreterLine;

    public IntegerDeclaration(InterpreterLine interpreterLine) {
        this.interpreterLine = interpreterLine;

    }


    @Override
    public void process(DeclareVariablePort declareVariablePort, EvaluateToIntVariablePort evaluateToIntVariablePort, ControlStructureEvaluationPort controlStructureEvaluationPort, GPIOPort gpioPort) {
        // guard empty
        if (!interpreterLine.hasNextToken()) {
            throw new RuntimeException("Syntax Error in line: " + interpreterLine.getLineNumber());
        }

        // guard non integer declaration
        if (!interpreterLine.getNextToken().equals("var")) {
            throw new RuntimeException("Syntax Error. Invalid var declaration in line: " + interpreterLine.getLineNumber());
        }

        // guard no identifier
        if (!interpreterLine.hasNextToken()) {
            throw new RuntimeException("Syntax Error. Var declaration with no variable name in line: " + interpreterLine.getLineNumber());
        }

        // extract variableName
        String newVariableIdentifier = interpreterLine.getNextToken();

        // guard this is EOL
        if (interpreterLine.hasNextToken()) {
            throw new RuntimeException("Syntax Error. Integer declaration can not be followed by additional tokens. Line: " + interpreterLine.getLineNumber());
        }


        // commit the line
        declareVariablePort.declareIntVariable(newVariableIdentifier);

    }
}
