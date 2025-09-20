package com.idt.simulator;

public class RegisterSimulator extends MemorySimulator {

    public RegisterSimulator() {
        super((short) 4);
    }

    public void mov(short register, short immediate) {
        short content = this.read(register);
        short topHalfword = (short) (content & 0b1111111100000000);
        this.write(register, (short) (topHalfword + immediate));
    }

    public void movT(short register, short immediate) {
        short content = this.read(register);
        short bottomHalfword = (short) (content & 0b0000000011111111);
        this.write(register, (short) (bottomHalfword + (immediate << 8)));
    }

    public void printRegisters() {
        System.out.println("----- Registers: -----");
        this.printContent();
    }

}
