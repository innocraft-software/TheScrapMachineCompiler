package com.idt.compiler.processorAbstraction.domain.registerManagement;


import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import lombok.Getter;

import java.util.List;

@Getter
public class Register {

    private final RegisterModule parentRegisterModule;

    private Variable variable;
    private int immediate;
    private RegisterStatus status;

    private final int index;
    private boolean contentEquivalentToRam;

    public Register(int index, RegisterModule parentRegisterModule) {
        this.status = RegisterStatus.free;
        this.contentEquivalentToRam = true;
        this.index = index;
        this.parentRegisterModule = parentRegisterModule;
    }

    /**
     * set up current status
     **/
    public void setVariable(Variable variable) {
        // ensure the same variable is not already in another register
        List<Register> variableRegisters = parentRegisterModule.findVariableRegisters();
        for (Register register : variableRegisters) {
            if (register.getStatus().equals(RegisterStatus.variable)) {
                if (register.getVariable().equals(variable) && !register.equals(this)) {
                    // if the register is already archived, clear it to ensure no duplicates
                    if (register.isContentEquivalentToRam()) {
                        register.clear();
                    } else {
                        throw new IllegalCallerException("Attempt to assign variable to a register that is already in another register");
                    }
                }
            }
        }

        // INV: only allow setting variable if register unused
        if (!this.contentEquivalentToRam) {
            throw new IllegalCallerException("Attempting to assign a variable to a register which is in use");
        }

        this.variable = variable;
        this.status = RegisterStatus.variable;
        this.contentEquivalentToRam = false;
        this.immediate = 0;
    }

    public void setImmediate(int immediate) {
        // INV: only allow setting variable if register unused
        if (!this.contentEquivalentToRam) {
            throw new IllegalCallerException("Attempting to assign an immediate to a register which is in use");
        }

        this.immediate = immediate;
        this.status = RegisterStatus.immediate;
        this.contentEquivalentToRam = false;
        this.variable = null;
    }

    public void free() {
        this.contentEquivalentToRam = true;
    }

    public void reUse(Variable variable) {

        // ensure the same variable is not already in another register
        List<Register> variableRegisters = parentRegisterModule.findVariableRegisters();
        for (Register register : variableRegisters) {
            if (register.getStatus().equals(RegisterStatus.variable)) {
                if (register.getVariable().equals(variable) && !register.equals(this)) {
                    // if the register is already archived, clear it to ensure no duplicates
                    if (register.isContentEquivalentToRam()) {
                        register.clear();
                    } else {
                        throw new IllegalCallerException("Attempt to assign variable to a register that is already in another register");
                    }
                }
            }
        }

        // INV: check if the re-use is allowed
        if (this.variable != variable) {
            throw new IllegalCallerException("Attempting a re using a register for a variable that it does not hold");
        }
        this.contentEquivalentToRam = false;
    }

    public void reUse(int immediate) {
        // INV: check if the re-use is allowed
        if (this.immediate != immediate) {
            throw new IllegalCallerException("Attempting a re using a register for a variable that it does not hold");
        }
        this.contentEquivalentToRam = false;
    }

    public void clear() {
        // INV: only allow clearing if register free
        if (!this.contentEquivalentToRam) {
            throw new IllegalCallerException("Attempting to clear a register which is in use");
        }

        this.status = RegisterStatus.free;
        this.immediate = 0;
        this.variable = null;
    }
}
