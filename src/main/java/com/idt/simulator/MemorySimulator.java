package com.idt.simulator;

public abstract class MemorySimulator {
    private final short[] memory;

    public MemorySimulator(short memorySize) {

        memory = new short[memorySize];

        for (short index = 0; index < memorySize; index++) {
            memory[index] = 0;
        }

    }

    public void write(short index, short value) {
        this.memory[index] = value;
    }

    public short read(short index) {
        return memory[index];
    }

    public void printContent() {
        int index = 0;
        for (int cell : this.memory) {
            System.out.println(index++ + " : " + cell);
        }
    }
}
