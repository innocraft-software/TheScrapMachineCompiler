package com.idt.compiler.interpreter.domain;

import com.idt.compiler.interpreter.domain.controlStructures.ControlStructureManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class InterpreterSession {

    private final ControlStructureManager controlStructureManager;

    private int currentLineNumber;

    private BufferedReader bufferedReader;

    public InterpreterSession(String filePath) {
        controlStructureManager = new ControlStructureManager();
        currentLineNumber = 0;
        try {
            bufferedReader = new BufferedReader(new FileReader(filePath));
        } catch (IOException e) {
            System.err.println("Interpreter: An error occurred while opening the file: " + e.getMessage());
        }
    }

    public InterpreterLine readNextLine() {
        if (bufferedReader != null) {
            try {
                String line = bufferedReader.readLine();
                if (line != null) {
                    return new InterpreterLine(line, ++currentLineNumber);
                } else {
                    System.out.println("Interpreter: End of file reached.");
                }
            } catch (IOException e) {
                System.err.println("Interpreter: An error occurred while reading the file: " + e.getMessage());
            }
        }
        return null;
    }

    public void closeFile() {
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                System.err.println("Interpreter: An error occurred while closing the file: " + e.getMessage());
            }
        }
    }

    public ControlStructureManager getControlStructureManager() {
        return controlStructureManager;
    }
}
