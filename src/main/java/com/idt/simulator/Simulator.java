package com.idt.simulator;


import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Simulator {
    private static final String defaultBinaryPath = "PATH-TO-SIMULATOR-BINARY/binary.txt";
    public static int sleepTime = 1000;

    public static void main(String[] args) {
        simulate(defaultBinaryPath);
    }

    public static void simulate(String binaryPath) {
        simulate(binaryPath, null);
    }

    public static void simulate() {
        simulate(defaultBinaryPath);
    }

    public static Short simulate(String binaryPath, Short returnOutput) {


        ExecutorService executorService;
        try {
            executorService = new ExecutorService(binaryPath, new ScrapMachineSimulator());
        } catch (FileNotFoundException e) {
            System.out.println("Binary not found");
            throw new RuntimeException(e);
        }


        try {

            while (executorService.next()) {
                Thread.sleep(sleepTime);
            }

            Set<Short> resultRegisters = new HashSet<>();
            for (short i = 0; i < 4; i++) {
                resultRegisters.add(executorService.getRegister(i));
            }


            writeResultRegisterToFile(resultRegisters, "src/test/processAssemblyLocation/simOutputRegisters.txt");

            if (returnOutput != null) {
                return executorService.getRegister(returnOutput);
            }


        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    public static void writeResultRegisterToFile(Set<Short> shortSet, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Short value : shortSet) {
                writer.write(value.toString());
                writer.newLine();
            }
            System.out.println("Set of Short values written to file successfully.");
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }
}
