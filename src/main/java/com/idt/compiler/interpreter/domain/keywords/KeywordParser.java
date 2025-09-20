package com.idt.compiler.interpreter.domain.keywords;


import com.idt.compiler.interpreter.domain.InterpreterLine;

import java.util.Arrays;

public class KeywordParser {
    public KeywordParser() {

    }

    public KeywordSplit getSplit(InterpreterLine interpreterLine) {

        String[] keywordCandidates = interpreterLine.getContent().split(" ");

        // comment
        if (Arrays.asList(keywordCandidates).contains("//")) {
            return new Comment(interpreterLine);
        }

        // var declaration
        if (Arrays.asList(keywordCandidates).contains("var")) {
            return new IntegerDeclaration(interpreterLine);
        }

        // var assignment
        if (Arrays.asList(keywordCandidates).contains("=")) {
            return new IntegerAssignment(interpreterLine);
        }

        // if structure
        if (Arrays.asList(keywordCandidates).contains("if")) {
            return new IfKeyword(interpreterLine);
        }

        // while structure
        if (Arrays.asList(keywordCandidates).contains("while")) {
            return new WhileKeyword(interpreterLine);
        }

        // end
        if (Arrays.asList(keywordCandidates).contains("end")) {
            return new EndKeyword(interpreterLine);
        }

        // gpioSet
        if (Arrays.asList(keywordCandidates).contains("gpioSetHigh")) {
            return new GPIOSetHighKeyword(interpreterLine);
        }

        // gpioReset
        if (Arrays.asList(keywordCandidates).contains("gpioSetLow")) {
            return new GPIOSetLowKeyword(interpreterLine);
        }

        // gpioRead
        if (Arrays.asList(keywordCandidates).contains("gpioRead")) {
            return new GPIOReadKeyword(interpreterLine);
        }

        // empty line
        if (interpreterLine.isEmpty()) {
            return new EmptyLine();
        }


        return null;
    }
}
