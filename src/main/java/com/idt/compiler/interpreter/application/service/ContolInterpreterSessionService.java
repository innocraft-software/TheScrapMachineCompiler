package com.idt.compiler.interpreter.application.service;

import com.idt.compiler.interpreter.application.port.in.ControlInterpreterSessionUseCase;
import com.idt.compiler.interpreter.application.port.out.context.ControlStructureEvaluationPort;
import com.idt.compiler.interpreter.application.port.out.context.DeclareVariablePort;
import com.idt.compiler.interpreter.application.port.out.context.EvaluateToIntVariablePort;
import com.idt.compiler.interpreter.application.port.out.context.GPIOPort;
import com.idt.compiler.interpreter.application.port.out.persistence.LoadReaderStatePort;
import com.idt.compiler.interpreter.domain.InterpreterLine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContolInterpreterSessionService implements ControlInterpreterSessionUseCase {
    private final LoadReaderStatePort loadReaderStatePort;
    private final DeclareVariablePort declareVariablePort;
    private final EvaluateToIntVariablePort evaluateToIntVariablePort;
    private final ControlStructureEvaluationPort controlStructureEvaluationPort;
    private final GPIOPort gpioPort;

    @Override
    public boolean parseLine() {
        InterpreterLine line = loadReaderStatePort.getReader().readNextLine();
        if (line == null) {
            return false;
        } else {
            line.process(declareVariablePort, evaluateToIntVariablePort, controlStructureEvaluationPort, gpioPort);
            return true;
        }
    }
}
