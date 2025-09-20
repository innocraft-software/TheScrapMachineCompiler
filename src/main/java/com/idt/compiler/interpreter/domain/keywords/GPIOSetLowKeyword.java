package com.idt.compiler.interpreter.domain.keywords;

import com.idt.compiler.interpreter.application.port.out.context.ControlStructureEvaluationPort;
import com.idt.compiler.interpreter.application.port.out.context.DeclareVariablePort;
import com.idt.compiler.interpreter.application.port.out.context.EvaluateToIntVariablePort;
import com.idt.compiler.interpreter.application.port.out.context.GPIOPort;
import com.idt.compiler.interpreter.domain.InterpreterLine;

public class GPIOSetLowKeyword implements KeywordSplit {

    private final InterpreterLine interpreterLine;

    public GPIOSetLowKeyword(InterpreterLine interpreterLine) {
        this.interpreterLine = interpreterLine;

    }


    @Override
    public void process(DeclareVariablePort declareVariablePort, EvaluateToIntVariablePort evaluateToIntVariablePort, ControlStructureEvaluationPort controlStructureEvaluationPort, GPIOPort gpioPort) {
        // guard empty
        if (!interpreterLine.hasNextToken()) {
            throw new RuntimeException("Syntax Error in line: " + interpreterLine.getLineNumber());
        }

        // guard non gpioSet declaration
        if (!interpreterLine.getNextToken().equals("gpioSetLow")) {
            throw new RuntimeException("Syntax Error. Invalid GPIO interaction in line: " + interpreterLine.getLineNumber());
        }

        // guard no address
        if (!interpreterLine.hasNextToken()) {
            throw new RuntimeException("Syntax Error. GPIO write with no address in line: " + interpreterLine.getLineNumber());
        }

        // extract address
        String address = interpreterLine.getNextToken();

        // guard this is EOL
        if (interpreterLine.hasNextToken()) {
            throw new RuntimeException("Syntax Error. GPIO write can not be followed by additional tokens. Line: " + interpreterLine.getLineNumber());
        }


        // commit the line
        gpioPort.setLowGPIO(Integer.parseInt(address));

    }

}
