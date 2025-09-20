package com.idt.compiler.interpreter.application.service;

import com.idt.compiler.interpreter.application.port.in.ManageInterpreterSessionUseCase;
import com.idt.compiler.interpreter.application.port.out.persistence.CreateReaderStatePort;
import com.idt.compiler.interpreter.application.port.out.persistence.LoadReaderStatePort;
import com.idt.compiler.interpreter.domain.InterpreterSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InterpreterSessionManagementService implements ManageInterpreterSessionUseCase {

    private final CreateReaderStatePort createReaderStatePort;
    private final LoadReaderStatePort loadReaderStatePort;

    @Override
    public void createInterpreterSession(String filePath) {
        InterpreterSession interpreterSession = new InterpreterSession(filePath);
        createReaderStatePort.createReaderState(interpreterSession);
    }

    @Override
    public void closeInterpreterSession() {
        InterpreterSession interpreterSession = loadReaderStatePort.getReader();
        interpreterSession.closeFile();
    }
}
