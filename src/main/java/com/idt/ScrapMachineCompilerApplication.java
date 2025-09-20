package com.idt;

import com.idt.compiler.common.globalDomain.variableManagement.VariableScope;
import com.idt.simulator.Simulator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScrapMachineCompilerApplication {

    private static final boolean SIMULATE_AFTER_COMPILE = false;
    private static final int SIM_SPEED = 10;

    public static void main(String[] args) {
        SpringApplication.run(ScrapMachineCompilerApplication.class, args);

        compileScript(
                "PATH-TO-SCRIPT/scriptname.smscript",
                "PATH-TO-OUTPUT-FOLDER"
        );


        if (ScrapMachineCompilerApplication.SIMULATE_AFTER_COMPILE) {
            // run simulator
            Simulator.sleepTime = ScrapMachineCompilerApplication.SIM_SPEED;
            System.setProperty("java.awt.headless", "false");
            Simulator.simulate();
        }
    }

    public static void compileScript(String scriptPath, String exportLocation) {
        // create the new application
        TestContext.getManageApplicationUseCase().newApplication();
        // create a new interpreter
        TestContext.getManageInterpreterSessionUseCase().createInterpreterSession(scriptPath);
        // create the global variable scope
        VariableScope globalScope = TestContext.getScopeManagementUseCase().createScope("GLOBAL");


        // interpret the provided smscript
        while (TestContext.getControlInterpreterSessionUseCase().parseLine()) {
            System.out.println("\n");
        }


        // exit the global scope
        TestContext.getScopeManagementUseCase().exitScope(globalScope);
        // export the application
        TestContext.getManageApplicationUseCase().exportApplication(exportLocation);
        // close the interpreter
        TestContext.getManageInterpreterSessionUseCase().closeInterpreterSession();
    }

}