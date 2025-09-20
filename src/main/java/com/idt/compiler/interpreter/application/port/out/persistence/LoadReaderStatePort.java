package com.idt.compiler.interpreter.application.port.out.persistence;

import com.idt.compiler.interpreter.domain.InterpreterSession;

public interface LoadReaderStatePort {
    InterpreterSession getReader();
}
