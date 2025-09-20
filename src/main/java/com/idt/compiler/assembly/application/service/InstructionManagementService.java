package com.idt.compiler.assembly.application.service;

import com.idt.compiler.assembly.application.port.in.AppendInstructionUseCase;
import com.idt.compiler.assembly.application.port.in.GetNextInstructionUseCase;
import com.idt.compiler.assembly.application.port.in.ManageApplicationUseCase;
import com.idt.compiler.assembly.application.port.out.persistence.LoadSMAPort;
import com.idt.compiler.assembly.application.port.out.persistence.ResetSMAPort;
import com.idt.compiler.assembly.domain.ScrapMachineApplication;
import com.idt.compiler.common.globalDomain.Instruction;
import com.idt.globalDependencies.fileUtilities.FileManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InstructionManagementService implements ManageApplicationUseCase, AppendInstructionUseCase, GetNextInstructionUseCase {

    private final ResetSMAPort resetSMAPort;
    private final LoadSMAPort loadSMAPort;


    @Override
    public void newApplication() {
        resetSMAPort.reset();
    }

    @Override
    public void exportApplication(String exportPath) {
        ScrapMachineApplication application = loadSMAPort.loadSMA();
        application.build();

        try {
            FileManager.createFile(exportPath + "/translation.txt", application.translate());
            List<String> blueprintContent = application.createBlueprint();
            int blueprintCount = 0;
            for (String blueprintText : blueprintContent) {
                FileManager.createFile(exportPath + "/blueprint" + blueprintCount++ + ".json", blueprintText);
            }
            FileManager.createFile(exportPath + "/binary.txt", application.createBinary());
        } catch (IOException e) {
            throw new IllegalCallerException("Unable to create file. IO Exception");
        }
    }

    @Override
    public void appendInstruction(Instruction instruction) {
        ScrapMachineApplication application = loadSMAPort.loadSMA();
        application.addInstruction(instruction);
    }

    @Override
    public void replaceInstruction(Instruction instruction, Instruction newInstruction) {
        loadSMAPort.loadSMA().replaceInstruction(instruction, newInstruction);
    }


    @Override
    public int getNextInstruction() {
        ScrapMachineApplication application = loadSMAPort.loadSMA();
        return application.getNewInstructionAddress();
    }
}
