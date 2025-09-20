package com.idt.compiler.interpreter.domain.keywords;

import com.idt.compiler.interpreter.application.port.out.context.ControlStructureEvaluationPort;
import com.idt.compiler.interpreter.application.port.out.context.DeclareVariablePort;
import com.idt.compiler.interpreter.application.port.out.context.EvaluateToIntVariablePort;
import com.idt.compiler.interpreter.application.port.out.context.GPIOPort;

public interface KeywordSplit {
    void process(DeclareVariablePort declareVariablePort, EvaluateToIntVariablePort evaluateToIntVariablePort, ControlStructureEvaluationPort controlStructureEvaluationPort, GPIOPort gpioPort);
}
