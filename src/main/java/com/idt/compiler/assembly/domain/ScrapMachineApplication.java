package com.idt.compiler.assembly.domain;

import com.idt.compiler.common.globalDomain.Instruction;
import com.idt.globalDependencies.assemblyProcessor.AssemblyTranslator;
import com.idt.globalDependencies.jsonGenerator.JSONGenerator;

import java.util.ArrayList;
import java.util.List;

public class ScrapMachineApplication {
    private final List<Instruction> instructions;
    private boolean hasBeenBuilt;

    private ScrapMachineApplication(List<Instruction> instructions, boolean hasBeenBuilt) {
        this.instructions = instructions;
        this.hasBeenBuilt = hasBeenBuilt;
    }


    public static ScrapMachineApplication newApplication() {
        return new ScrapMachineApplication(
                new ArrayList<>(),
                false
        );
    }


    public void addInstruction(Instruction instruction) {
        // INV: can not mutate built application
        if (this.hasBeenBuilt) {
            throw new IllegalCallerException("Can not add to built application");
        }

        this.instructions.add(instruction);
    }

    public void replaceInstruction(Instruction instruction, Instruction newInstruction) {
        // INV: can not mutate built application
        if (this.hasBeenBuilt) {
            throw new IllegalCallerException("Can not add to built application");
        }

        // find the oldInstruction
        for (int i = 0; i < instructions.size(); i++) {
            if (instructions.get(i).equals(instruction)) {
                // replace the instruction
                instructions.set(i, newInstruction);
                break;
            }
        }

    }

    public ScrapMachineApplication build() {
        this.instructions.add(Instruction.terminateInstruction());
        this.hasBeenBuilt = true;
        return this;
    }

    public List<String> createBlueprint() {
        // INV: application needs to be built before export
        if (!this.hasBeenBuilt) {
            throw new IllegalCallerException("Attempt to create blueprint of application before build");
        }

        // split the rom into multiple roms
        List<String> output = new ArrayList<>();

        StringBuilder rowData = new StringBuilder();
        // generate each row
        int rowIndex = 0;
        int rowPosition = 0;
        int activeRomSize = 0;
        for (Instruction instruction : this.instructions) {
            rowData.append(",").append(instruction.toJSON(rowPosition++, rowIndex++));
            activeRomSize++;
            if (rowPosition >= (3*255)) {
                output.add(JSONGenerator.getInstance().generateRomBlueprint(activeRomSize, rowData.toString()));
                rowData = new StringBuilder();
                rowPosition = 0;
                activeRomSize = 0;
            }
        }
        output.add(JSONGenerator.getInstance().generateRomBlueprint(activeRomSize, rowData.toString()));
        return output;
    }

    public String translate() {
        // INV: application needs to be built before export
        if (!this.hasBeenBuilt) {
            throw new IllegalCallerException("Attempt to translate application before build");
        }

        int addr = 0;
        StringBuilder translation = new StringBuilder("_ADDR_     _INST_   |   __TRANSLATION__");
        for (Instruction instruction : this.instructions) {
            translation.append("\n").append(AssemblyTranslator.getHexInstructionInformation(instruction.getBinary(), addr)).append("   |   ").append(instruction.translate());
            addr += 2;
        }
        return translation.toString();
    }

    public String createBinary() {
        // INV: application needs to be built before export
        if (!this.hasBeenBuilt) {
            throw new IllegalCallerException("Attempt to create binary of application before build");
        }

        StringBuilder output = new StringBuilder();
        for (Instruction instruction : instructions) {
            output.append(instruction.getInstructionBinary()).append("\n");
        }
        return output.toString();
    }

    public List<Instruction> export() {
        // INV: application needs to be built before export
        if (!this.hasBeenBuilt) {
            throw new IllegalCallerException("Attempt to export application before build");
        }

        return new ArrayList<>(this.instructions);
    }

    public int getNewInstructionAddress() {
        return (this.instructions.size() * 2);
    }

}
