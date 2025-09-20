package com.idt.compiler.interpreter.application.port.in;

public interface ManageInterpreterSessionUseCase {
    void createInterpreterSession(String filePath);

    void closeInterpreterSession();
}
