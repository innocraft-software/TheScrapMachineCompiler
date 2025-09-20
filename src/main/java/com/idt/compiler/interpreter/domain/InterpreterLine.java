package com.idt.compiler.interpreter.domain;

import com.idt.compiler.interpreter.application.port.out.context.ControlStructureEvaluationPort;
import com.idt.compiler.interpreter.application.port.out.context.DeclareVariablePort;
import com.idt.compiler.interpreter.application.port.out.context.EvaluateToIntVariablePort;
import com.idt.compiler.interpreter.application.port.out.context.GPIOPort;
import com.idt.compiler.interpreter.domain.keywords.KeywordParser;
import com.idt.compiler.interpreter.domain.keywords.KeywordSplit;


public class InterpreterLine {

    private final String content;

    private final int lineNumber;

    private final KeywordSplit operation;

    private String splitBuffer;


    public InterpreterLine(String lineContent, int lineNumber) {
        lineContent = lineContent.strip();
        this.lineNumber = lineNumber;
        this.content = lineContent;

        this.splitBuffer = content;

        operation = new KeywordParser().getSplit(this);

    }

    public boolean isEmpty() {
        return content.isBlank();
    }

    public String getNextToken() {
        String[] tokens = splitBuffer.split(" ", 2);
        splitBuffer = (tokens.length > 1) ? tokens[1] : "";
        return tokens.length > 0 ? tokens[0] : "";
    }

    public boolean hasNextToken() {
        return !splitBuffer.equals("");
    }

    public String getContent() {
        return content;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void process(DeclareVariablePort declareVariablePort, EvaluateToIntVariablePort evaluateToIntVariablePort, ControlStructureEvaluationPort controlStructureEvaluationPort, GPIOPort gpioPort) {
        System.out.println("Interpreter: Processing line: " + this.lineNumber);
        try {
            this.operation.process(declareVariablePort, evaluateToIntVariablePort, controlStructureEvaluationPort, gpioPort);
        } catch (NullPointerException e) {
            throw new RuntimeException("Interpreter: Syntax Error. Can not identify operand in line: " + this.lineNumber);
        }
    }

    public String getRemainingBuffer() {
        return splitBuffer;
    }

}
