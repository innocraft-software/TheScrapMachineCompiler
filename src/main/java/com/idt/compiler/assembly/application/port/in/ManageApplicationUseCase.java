package com.idt.compiler.assembly.application.port.in;

public interface ManageApplicationUseCase {
    void newApplication();

    void exportApplication(String exportPath);
}
