package com.idt.simulator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class BinaryFile {
    private final ArrayList<String> instructions;

    public BinaryFile(String filepath) throws FileNotFoundException {
        this.instructions = new ArrayList<>();
        BufferedReader inputReader = new BufferedReader(new FileReader(filepath));
        String line = "";

        while (line != null) {
            try {
                line = inputReader.readLine();
            } catch (IOException e) {
                System.out.println("InvalidBinaryAccess");
                throw new RuntimeException(e);
            }
            this.instructions.add(line);
        }

    }

    public String get(int programCounter) {
        return instructions.get(programCounter);
    }
}
