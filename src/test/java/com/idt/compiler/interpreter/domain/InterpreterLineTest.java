package com.idt.compiler.interpreter.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InterpreterLineTest {

    @Test
    void interpreterLineTest() {
        new InterpreterLine("helo", 1);
    }

    @Test
    void isEmptyTest() {
        InterpreterLine line = new InterpreterLine("", 1);
        assertTrue(line.isEmpty());
    }

    @Test
    void isEmptyWithSpaceTest() {
        InterpreterLine line = new InterpreterLine(" ", 1);
        assertTrue(line.isEmpty());
    }

    @Test
    void isNonEmptyTest() {
        InterpreterLine line = new InterpreterLine(" A", 1);
        assertFalse(line.isEmpty());
    }

    @Test
    void getNextTokenTest() {
        InterpreterLine line = new InterpreterLine("token1 token2 token3", 1);
        assertEquals(line.getNextToken(), "token1");
        assertEquals(line.getNextToken(), "token2");
        assertEquals(line.getNextToken(), "token3");
    }

    @Test
    void getInvalidTokenTest() {
        InterpreterLine line = new InterpreterLine("token1", 1);
        assertEquals(line.getNextToken(), "token1");
        assertEquals(line.getNextToken(), "");
        assertEquals(line.getNextToken(), "");
    }

    @Test
    void hasNextTokenTrue() {
        InterpreterLine line = new InterpreterLine("token1", 1);
        assertTrue(line.hasNextToken());
    }

    @Test
    void hasNextTokenFalse() {
        InterpreterLine line = new InterpreterLine("token1", 1);
        line.getNextToken();
        assertFalse(line.hasNextToken());
    }
}