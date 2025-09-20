package com.idt.compiler.processorAbstraction.domain.registerManagement;

import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RegisterModule {

    private final List<Register> registers;

    public static final int REGISTER_COUNT = 4;

    private RegisterModule() {
        List<Register> registers = new ArrayList<>(REGISTER_COUNT);
        for (int registerIndex = 0; registerIndex < REGISTER_COUNT; registerIndex++) {
            registers.add(new Register(registerIndex, this));
        }
        this.registers = registers;
    }

    public static RegisterModule initialize() {
        return new RegisterModule();
    }

    public Register findFreeRegister() throws RegisterOverflowException {
        for (Register register : registers) {
            if (register.isContentEquivalentToRam()) {
                return register;
            }
        }
        throw new RegisterOverflowException();
    }

    public List<Register> findFreeRegisters() {
        List<Register> freeRegisters = new ArrayList<>();
        for (Register register : registers) {
            if (register.isContentEquivalentToRam()) {
                freeRegisters.add(register);
            }
        }
        return freeRegisters;
    }


    public Register attemptReUse(Variable variable) {
        for (Register reUseRegister : registers) {
            if (reUseRegister.getStatus() == RegisterStatus.variable && reUseRegister.getVariable() == variable) {
                return reUseRegister;
            }
        }
        return null;
    }

    protected Register attemptReUse(int immediate) {
        for (Register reUseRegister : this.getRegisters()) {
            if (reUseRegister.getStatus() == RegisterStatus.immediate && reUseRegister.getImmediate() == immediate) {
                reUseRegister.reUse(immediate);

                return reUseRegister;
            }
        }
        return null;
    }

    public ArrayList<Register> findVariableRegisters() {
        ArrayList<Register> workList = new ArrayList<>();
        for (Register r : registers) {
            if (r.getStatus() == RegisterStatus.variable) {
                workList.add(r);
            }
        }
        return new ArrayList<>(workList);
    }

    protected Register assignEmptyImmediate(int immediate) {
        // attempt re-use
        Register reUseAttempt = attemptReUse(immediate);
        if (reUseAttempt != null) {
            return reUseAttempt;
        }
        // assign new if unsuccessful
        for (Register register : this.getRegisters()) {
            if (register.isContentEquivalentToRam()) {
                register.setImmediate(immediate);

                return register;
            }
        }
        return null;
    }


    protected void freeRegisters() {
        for (Register register : registers) {
            register.free();
        }
    }

    protected void clearRegisters() {
        for (Register register : registers) {
            register.clear();
        }
    }


}
