package com.idt.compiler.processorAbstraction.domain.registerManagement;

public class FutureRegister {
    private Register reg;

    public void setReg(Register register) {
        reg = register;
    }

    public Register getReg() {
        if (this.reg == null) {
            throw new IllegalCallerException("Attempt to get register of unassigned future register. Ensure the Variable assignment was correctly committed");
        }
        return this.reg;
    }
}
