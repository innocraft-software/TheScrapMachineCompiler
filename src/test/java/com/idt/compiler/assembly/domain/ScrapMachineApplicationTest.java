package com.idt.compiler.assembly.domain;

import com.idt.compiler.common.globalDomain.Instruction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScrapMachineApplicationTest {

    @Test
    void newApplicationTest() {
        ScrapMachineApplication.newApplication();
    }

    @Test
    void buildTest() {
        ScrapMachineApplication scrapMachineApplication = ScrapMachineApplication.newApplication();
        assertThrows(IllegalCallerException.class, scrapMachineApplication::export);

        scrapMachineApplication.build();
        scrapMachineApplication.export();

    }

    @Test
    void emptyApplicationTest() {
        ScrapMachineApplication scrapMachineApplication = ScrapMachineApplication.newApplication();
        scrapMachineApplication.build();
        List<Instruction> export = scrapMachineApplication.export();
        assertEquals(1, export.size());
        assertEquals(Instruction.terminateInstruction(), export.get(0));
    }

    @Test
    void addInstructionTest() {
        ScrapMachineApplication scrapMachineApplication = ScrapMachineApplication.newApplication();
        scrapMachineApplication.addInstruction(Instruction.initialize(15));
        scrapMachineApplication.build();
        List<Instruction> export = scrapMachineApplication.export();
        assertEquals(2, export.size());
        assertEquals(Instruction.initialize(15), export.get(0));
        assertEquals(Instruction.terminateInstruction(), export.get(1));
    }

    @Test
    void addInstructionAfterBuildTest() {
        ScrapMachineApplication scrapMachineApplication = ScrapMachineApplication.newApplication();
        scrapMachineApplication.addInstruction(Instruction.initialize(12));
        scrapMachineApplication.build();
        assertThrows(IllegalCallerException.class, () -> scrapMachineApplication.addInstruction(Instruction.initialize(15)));
    }
}