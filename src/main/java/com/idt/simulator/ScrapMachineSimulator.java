package com.idt.simulator;

import com.idt.simulator.gpio.GPIOUI;

import javax.swing.*;

public class ScrapMachineSimulator {
    private int pc;
    public RegisterSimulator registers;
    public RamSimulator ram;

    public boolean zeroFlag;
    public boolean greaterThanFlag;
    private final GPIOUI gpio;

    public ScrapMachineSimulator() {
        pc = 0;
        zeroFlag = false;
        greaterThanFlag = false;
        this.registers = new RegisterSimulator();
        this.ram = new RamSimulator();

        this.gpio = new GPIOUI();

        SwingUtilities.invokeLater(() -> {
            gpio.setVisible(true);
        });

    }

    public void mov(short register, short immediate) {
        registers.mov(register, immediate);
    }

    public void movT(short register, short immediate) {
        registers.movT(register, immediate);
    }

    public void add(short source1, short source2, short destination) {
        registers.write(destination, (short) (registers.read(source1) + registers.read(source2)));
    }

    public void sub(short source1, short source2, short destination) {
        registers.write(destination, (short) (registers.read(source1) - registers.read(source2)));
    }


    public void cmp(short source1, short source2) {
        greaterThanFlag = (registers.read(source2) > registers.read(source1));
        zeroFlag = (registers.read(source2) == registers.read(source1));
    }

    public void ld(short register, short address) {
        registers.write(register, ram.read(address));
    }

    public void str(short register, short address) {
        ram.write(address, registers.read(register));
    }

    private short stripBits(short input) {
        return (short) (input & 0b1);
    }

    public void logic(short source1, short source2, short destination, LogicOperations operation) {

        source1 = stripBits(registers.read(source1));
        source2 = stripBits(registers.read(source2));

        short destinationContentNoLogic = (short) (registers.read(destination) & 0b1111111111111110);
        int result = 0;

        switch (operation) {
            case AND:
                result = source1 & source2;
                break;
            case OR:
                result = source1 | source2;
                break;
            case XOR:
                result = source1 ^ source2;
                break;
            case NOT:
                result = source1 == 0 ? 1 : 0; // Toggle the value (1 to 0, 0 to 1)
                break;
        }

        registers.write(destination, (short) (destinationContentNoLogic + result));

    }


    public void terminate() {
        System.out.println("Terminated");
    }

    public void ble(short pc) {
        if (!greaterThanFlag) {
            this.pc = pc;
        } else {
            incrementPC();
        }
    }

    public void gpioSet(short pin) {
        gpio.setPin(pin);
    }

    public void gpioReset(short pin, short storeRegister, boolean read) {
        if (!read) {
            gpio.resetPin(pin);
        } else {
            short pinState;
            if (gpio.getPin(pin)) {
                pinState = 1;
            } else {
                pinState = 0;
            }
            registers.write(storeRegister, pinState);
        }
    }

    public void shift(short register, short exponent, boolean down) {
        double exp;
        if (down) {
            exp = Math.pow(2, -exponent);
        } else {
            exp = Math.pow(2, exponent);
        }
        this.registers.write(register, (short) (this.registers.read(register) * exp));
    }

    public void beq(short pc) {
        if (zeroFlag) {
            this.pc = pc;
        } else {
            incrementPC();
        }
    }

    public int getPC() {
        return this.pc;
    }

    public void incrementPC() {
        this.pc += 1;
    }


    public void printRegisters() {
        registers.printRegisters();
    }
}
