package com.idt.compiler.expressionEvaluation.domain;

public class SyntaxErrorException extends RuntimeException {
    public SyntaxErrorException() {
        super();
    }

    public SyntaxErrorException(String message) {
        super(message);
    }
}
