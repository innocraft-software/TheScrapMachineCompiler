package com.idt.compiler.common.globalDomain;


import com.idt.globalDependencies.assemblyProcessor.AssemblyGenerator;
import com.idt.globalDependencies.assemblyProcessor.AssemblyTranslator;
import com.idt.globalDependencies.jsonGenerator.JSONGenerator;
import lombok.Value;

@Value
public class Instruction {
    private final int instructionBinary;

    private Instruction(int instructionBinary) {
        this.instructionBinary = instructionBinary;
    }

    public static Instruction initialize(int instructionBinary) {
        return new Instruction(instructionBinary);
    }

    public static Instruction terminateInstruction() {
        return new Instruction(AssemblyGenerator.terminate());
    }

    public String toJSON(int rowPosition, int rowIndex) {
        return JSONGenerator.getInstance().generateRow(rowPosition, rowIndex, this.instructionBinary);
    }

    public String translate() {
        AssemblyTranslator translator = new AssemblyTranslator(this.instructionBinary);
        return translator.translate();
    }

    public int getBinary() {
        return this.instructionBinary;
    }

}
