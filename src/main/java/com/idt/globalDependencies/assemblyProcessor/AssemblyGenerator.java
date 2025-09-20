package com.idt.globalDependencies.assemblyProcessor;

public class AssemblyGenerator {
    private static int clampRegister(int register) {
        if (register < 0) {
            register = 0;
        } else if (register > 3) {
            register = 3;
        }
        return register;
    }


    // direct operations
    private static int logicOperation(int sourceRegister1, int sourceRegister2, int destinationRegister, int operation) {
        int baseInstruction = baseInstruction(7, sourceRegister1, sourceRegister2, destinationRegister, 0, 10, 8, 6, 0);
        return baseInstruction + (operation << 4);
    }

    private static int baseInstruction(int instructionCode, int register1, int register2, int register3, int immediate, int shiftRegister1, int shiftRegister2, int shiftRegister3, int shiftImmediate) {
        int basicOperationBin = instructionCode << 12;
        int register1Bin = clampRegister(register1) << shiftRegister1;
        int register2Bin = clampRegister(register2) << shiftRegister2;
        int register3Bin = clampRegister(register3) << shiftRegister3;
        int immediateBin = immediate << shiftImmediate;
        return basicOperationBin + register1Bin + register2Bin + register3Bin + immediateBin;
    }

    public static int mov(int register, int immediate) {
        return baseInstruction(0, register, 0, 0, immediate, 10, 0, 0, 2);
    }

    public static int movt(int register, int immediate) {
        return baseInstruction(1, register, 0, 0, immediate, 10, 0, 0, 2);
    }

    public static int add(int sourceRegister1, int sourceRegister2, int destinationRegister) {
        return baseInstruction(2, sourceRegister1, sourceRegister2, destinationRegister, 0, 10, 8, 6, 0);
    }

    public static int sub(int sourceRegister1, int sourceRegister2, int destinationRegister) {
        return baseInstruction(3, sourceRegister1, sourceRegister2, destinationRegister, 0, 10, 8, 6, 0);
    }

    public static int cmp(int sourceRegister1, int sourceRegister2) {
        return baseInstruction(4, sourceRegister1, sourceRegister2, 0, 0, 10, 8, 0, 0);
    }

    public static int ld(int address, int destinationRegister) {
        return baseInstruction(5, 0, 0, destinationRegister, address, 0, 0, 10, 4);
    }

    public static int str(int address, int sourceRegister) {
        return baseInstruction(6, sourceRegister, 0, 0, address, 10, 0, 0, 4);
    }

    public static int and(int sourceRegister1, int sourceRegister2, int destinationRegister) {
        return logicOperation(sourceRegister1, sourceRegister2, destinationRegister, 0);
    }

    public static int or(int sourceRegister1, int sourceRegister2, int destinationRegister) {
        return logicOperation(sourceRegister1, sourceRegister2, destinationRegister, 1);
    }

    public static int xor(int sourceRegister1, int sourceRegister2, int destinationRegister) {
        return logicOperation(sourceRegister1, sourceRegister2, destinationRegister, 2);
    }

    public static int not(int sourceRegister, int destinationRegister) {
        return logicOperation(sourceRegister, 0, destinationRegister, 3);
    }

    public static int terminate() {
        return baseInstruction(8, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public static int ble(int pcAddressHex) {
        return baseInstruction(9, 0, 0, 0, pcAddressHex / 2, 0, 0, 0, 0);
    }

    public static int beq(int pcAddress) {
        return baseInstruction(14, 0, 0, 0, pcAddress / 2, 0, 0, 0, 0);
    }

    public static int gpioSet(int pin) {
        return baseInstruction(10, 0, 0, 0, pin, 0, 0, 0, 6);
    }

    public static int gpioReset(int pin) {
        return baseInstruction(11, 0, 0, 0, pin, 0, 0, 0, 6);
    }

    public static int gpioRead(int pin, int destinationRegister) {
        int baseInstruction = baseInstruction(11, 0, 0, destinationRegister, pin, 0, 0, 4, 6);
        return baseInstruction + (1 << 2);
    }

    public static int shiftUp(int register) {
        return baseInstruction(12, register, 0, 0, 0, 10, 0, 0, 0);
    }

    public static int shiftDown(int register) {
        int baseInstruction = baseInstruction(12, register, 0, 0, 0, 10, 0, 0, 0);
        return baseInstruction + (1 << 9);
    }
}
