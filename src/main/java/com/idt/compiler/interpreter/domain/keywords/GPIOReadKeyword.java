package com.idt.compiler.interpreter.domain.keywords;

import com.idt.compiler.interpreter.application.port.out.context.ControlStructureEvaluationPort;
import com.idt.compiler.interpreter.application.port.out.context.DeclareVariablePort;
import com.idt.compiler.interpreter.application.port.out.context.EvaluateToIntVariablePort;
import com.idt.compiler.interpreter.application.port.out.context.GPIOPort;
import com.idt.compiler.interpreter.domain.InterpreterLine;

public class GPIOReadKeyword implements KeywordSplit {

    private final InterpreterLine interpreterLine;

    public GPIOReadKeyword(InterpreterLine interpreterLine) {
        this.interpreterLine = interpreterLine;

    }


    @Override
    public void process(DeclareVariablePort declareVariablePort, EvaluateToIntVariablePort evaluateToIntVariablePort, ControlStructureEvaluationPort controlStructureEvaluationPort, GPIOPort gpioPort) {
        // guard empty
        if (!interpreterLine.hasNextToken()) {
            throw new RuntimeException("Syntax Error in line: " + interpreterLine.getLineNumber());
        }

        // guard non gpioSet declaration
        if (!interpreterLine.getNextToken().equals("gpioRead")) {
            throw new RuntimeException("Syntax Error. Invalid gpio interaction in line: " + interpreterLine.getLineNumber());
        }

        // guard no address
        if (!interpreterLine.hasNextToken()) {
            throw new RuntimeException("Syntax Error. GPIO read with no address in line: " + interpreterLine.getLineNumber());
        }

        // extract address
        String address = interpreterLine.getNextToken();

        // guard no varName
        if (!interpreterLine.hasNextToken()) {
            throw new RuntimeException("Syntax Error. GPIO read with no variable name in line: " + interpreterLine.getLineNumber());
        }

        // extract destination variable
        String destinationName = interpreterLine.getNextToken();

        // guard this is EOL
        if (interpreterLine.hasNextToken()) {
            throw new RuntimeException("Syntax Error. GPIO interaction can not be followed by additional tokens. Line: " + interpreterLine.getLineNumber());
        }


        // commit the line
        gpioPort.readGPIO(Integer.parseInt(address), destinationName);

    }

}
