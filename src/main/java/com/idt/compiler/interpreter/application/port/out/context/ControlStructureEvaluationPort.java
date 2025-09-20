package com.idt.compiler.interpreter.application.port.out.context;

public interface ControlStructureEvaluationPort {
    void startIf(String expression);

    void startWhile(String expression);

    void endCurrentControlStatement();
}
