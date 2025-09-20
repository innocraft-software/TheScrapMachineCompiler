package com.idt.compiler.processorAbstraction.application.port.in;

import com.idt.compiler.common.globalDomain.Instruction;
import com.idt.compiler.common.globalDomain.variableManagement.Variable;

@SuppressWarnings("unused")
public interface InstructionGenerationUseCase {
    void writeIntVar(int value, Variable variable);

    void not(Variable source, Variable destination);

    void equals(Variable source1, Variable source2, Variable destination);

    void isGreaterThan(Variable source1, Variable source2, Variable destination);

    void add(Variable source1, Variable source2, Variable destination);

    void sub(Variable source1, Variable source2, Variable destination);

    void mul(Variable factor1, Variable factor2, Variable destination);

    void div(Variable dividend, Variable divisor, Variable destination);

    void and(Variable source1, Variable source2, Variable destination);

    void or(Variable source1, Variable source2, Variable destination);

    void xor(Variable source1, Variable source2, Variable destination);

    void loadVariableToRegister(Variable variable);

    void setHighGPIO(int address);

    void setLowGPIO(int address);

    void readGPIO(int address, Variable storeTo);

    Instruction ifStart(Variable comparison);

    Instruction whileStart(Variable comparison);

    void ifEnd(Instruction incompleteBranch);

    void whileEnd(Instruction incompleteBranch, int blockStartBranchAddress);
}
