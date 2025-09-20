package com.idt.compiler.interpreter.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InterpreterSessionTest {

    @Test
    void newSessionTest() {
        new InterpreterSession("src/test/java/com/idt/compiler/interpreter/domain/test.smscript");
    }

    @Test
    void readNextLine() {
        InterpreterSession interpreterSession = new InterpreterSession("src/test/java/com/idt/compiler/interpreter/domain/test.smscript");
        assertEquals(interpreterSession.readNextLine().getContent(), "TestLine1");
    }

    @Test
    void readNextTwoLines() {
        InterpreterSession interpreterSession = new InterpreterSession("src/test/java/com/idt/compiler/interpreter/domain/test.smscript");
        assertEquals(interpreterSession.readNextLine().getContent(), "TestLine1");
        assertEquals(interpreterSession.readNextLine().getContent(), "TestLine2");
    }

    @Test
    void readNextLinesWithEmptyLineTest() {
        InterpreterSession interpreterSession = new InterpreterSession("src/test/java/com/idt/compiler/interpreter/domain/test.smscript");
        assertEquals(interpreterSession.readNextLine().getContent(), "TestLine1");
        assertEquals(interpreterSession.readNextLine().getContent(), "TestLine2");
        assertEquals(interpreterSession.readNextLine().getContent(), "");
        assertEquals(interpreterSession.readNextLine().getContent(), "TestLine3");
    }

    @Test
    void closeFile() {
        InterpreterSession interpreterSession = new InterpreterSession("src/test/java/com/idt/compiler/interpreter/domain/test.smscript");
        interpreterSession.closeFile();
    }
}