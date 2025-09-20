package com.idt.simulator;

import com.idt.globalDependencies.assemblyProcessor.AssemblyTranslator;
import com.idt.globalDependencies.assemblyProcessor.InstructionMasks;

import java.io.FileNotFoundException;
import java.io.IOException;

import static com.idt.globalDependencies.assemblyProcessor.AssemblyTranslator.computeMaskShift;

public class ExecutorService {


    BinaryFile binary;
    ScrapMachineSimulator scrapMachine;
    InstructionExecutor executor;

    short logicFlag;
    boolean readFlag;
    boolean shiftDown;

    public ExecutorService(String filepath, ScrapMachineSimulator scrapMachine) throws FileNotFoundException {
        this.scrapMachine = scrapMachine;
        this.executor = new InstructionExecutor(this.scrapMachine);
        this.binary = new BinaryFile(filepath);
    }


    private String translateFlags(int instruction) {

        int operation = getInstructionCode(instruction);

        if (operation == 7) {

            // logic instruction
            this.logicFlag = (short) ((instruction & 0b0000000000110000) >>> 4);


        } else if (operation == 11) {

            // READ instruction
            int readFlag = (instruction & 0b0000000000000100) >>> 2;
            this.readFlag = (readFlag == 1);

        } else if (operation == 12) {
            int shiftOperation = (instruction & 0b0000001000000000) >>> 9;
            this.shiftDown = (shiftOperation == 1);
        }
        return "";
    }

    public boolean next() throws IOException {

        String line = this.binary.get(scrapMachine.getPC());

        int instruction;

        if (line != null) {
            try {
                instruction = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Incorrect instruction file");
                throw new RuntimeException();
            }

            translateFlags(instruction);

            executor.execute(getInstructionCode(instruction), getImmediate(instruction), getSource1(instruction), getSource2(instruction), getDestination(instruction), this.logicFlag, this.readFlag, this.shiftDown);

            scrapMachine.printRegisters();

            System.out.println(scrapMachine.getPC());

            AssemblyTranslator at = new AssemblyTranslator(instruction);
            System.out.println(at.translate() + "\n\n");

            return true;
        } else {
            return false;
        }
    }

    private int getInstructionCode(int instruction) {
        return (instruction & InstructionMasks.INSTRUCTION_CODE_MASK) >>> 12;
    }

    private short getMaskElement(int instruction, int[] mask) {
        return (short) ((instruction & mask[getInstructionCode(instruction)]) >>> computeMaskShift(mask, getInstructionCode(instruction)));
    }

    private short getImmediate(int instruction) {
        return getMaskElement(instruction, InstructionMasks.IMMEDIATE_MASKS);
    }

    private short getSource1(int instruction) {
        return getMaskElement(instruction, InstructionMasks.SOURCE_1_MASK);
    }

    private short getSource2(int instruction) {
        return getMaskElement(instruction, InstructionMasks.SOURCE_2_MASK);
    }

    private short getDestination(int instruction) {
        return getMaskElement(instruction, InstructionMasks.DEST_MASK);
    }

    public short getRegister(short register) {
        return scrapMachine.registers.read(register);
    }

}
