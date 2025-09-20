package com.idt.compiler.interpreter.application.port.out.context;

public interface GPIOPort {
    void setHighGPIO(int gpioId);

    void setLowGPIO(int gpioId);

    void readGPIO(int gpioId, String destination);
}
