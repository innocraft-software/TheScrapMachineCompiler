package com.idt.simulator;

public class InstructionExecutor {
    private final ScrapMachineSimulator scrapMachine;

    public InstructionExecutor(ScrapMachineSimulator scrapMachine) {
        this.scrapMachine = scrapMachine;
    }

    public void execute(int instruction, short immediate, short source1, short source2, short destination, short logicFlag, boolean readMode, boolean down) {
        LogicOperations logicOperation = null;
        switch (logicFlag) {
            case (0):
                // AND
                logicOperation = LogicOperations.AND;
                break;
            case (1):
                // OR
                logicOperation = LogicOperations.OR;
                break;
            case (2):
                // XOR
                logicOperation = LogicOperations.XOR;
                break;
            case (3):
                // NOT
                logicOperation = LogicOperations.NOT;
                break;
            default:
                System.out.println("LogicInstructionError");
                throw new RuntimeException();
        }


        switch (instruction) {
            case (0):
                // mov
                scrapMachine.mov(destination, immediate);
                scrapMachine.incrementPC();
                break;
            case (1):
                // movt
                scrapMachine.movT(destination, immediate);
                scrapMachine.incrementPC();
                break;
            case (2):
                scrapMachine.add(source1, source2, destination);
                scrapMachine.incrementPC();
                break;
            case (3):
                scrapMachine.sub(source1, source2, destination);
                scrapMachine.incrementPC();
                break;
            case (4):
                scrapMachine.cmp(source1, source2);
                scrapMachine.incrementPC();
                break;
            case (5):
                scrapMachine.ld(destination, immediate);
                scrapMachine.incrementPC();
                break;
            case (6):
                scrapMachine.str(source1, immediate);
                scrapMachine.incrementPC();
                break;
            case (7):
                scrapMachine.logic(source1, source2, destination, logicOperation);
                scrapMachine.incrementPC();
                break;
            case (8):
                scrapMachine.terminate();
                scrapMachine.incrementPC();
                break;
            case (9):
                scrapMachine.ble(immediate);
                break;
            case (10):
                scrapMachine.gpioSet(immediate);
                scrapMachine.incrementPC();
                break;
            case (11):
                scrapMachine.gpioReset(immediate, destination, readMode);
                scrapMachine.incrementPC();
                break;
            case (12):
                scrapMachine.shift(destination, immediate, down);
                scrapMachine.incrementPC();
                break;
            case (13):
                break;
            case (14):
                scrapMachine.beq(immediate);
                break;
            case (15):
                break;
            default:
                System.out.println("InstructionError");
                throw new RuntimeException();
        }
    }
}
