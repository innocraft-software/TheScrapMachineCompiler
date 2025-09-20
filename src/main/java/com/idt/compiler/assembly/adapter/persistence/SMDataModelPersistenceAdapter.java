package com.idt.compiler.assembly.adapter.persistence;

import com.idt.compiler.assembly.application.port.out.persistence.LoadSMAPort;
import com.idt.compiler.assembly.application.port.out.persistence.ResetSMAPort;
import com.idt.compiler.assembly.domain.ScrapMachineApplication;
import org.springframework.stereotype.Component;

@Component
public class SMDataModelPersistenceAdapter implements LoadSMAPort, ResetSMAPort {

    private ScrapMachineApplication scrapMachineApplication;

    public SMDataModelPersistenceAdapter() {
        this.scrapMachineApplication = ScrapMachineApplication.newApplication();
    }

    @Override
    public ScrapMachineApplication loadSMA() {
        return this.scrapMachineApplication;
    }

    @Override
    public void reset() {
        this.scrapMachineApplication = ScrapMachineApplication.newApplication();
    }
}
