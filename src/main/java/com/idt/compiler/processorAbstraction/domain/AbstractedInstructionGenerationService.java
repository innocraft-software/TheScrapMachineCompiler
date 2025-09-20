package com.idt.compiler.processorAbstraction.domain;

import com.idt.compiler.common.globalDomain.Instruction;
import com.idt.compiler.processorAbstraction.application.port.out.domainConnectors.AddInstructionPort;
import com.idt.compiler.processorAbstraction.application.port.out.domainConnectors.GetNextInstructionPort;
import com.idt.compiler.processorAbstraction.application.port.out.domainConnectors.ModifyInstructionPort;
import com.idt.globalDependencies.assemblyProcessor.AssemblyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AbstractedInstructionGenerationService {

    private final AddInstructionPort addInstructionPort;

    private final GetNextInstructionPort getNextInstructionPort;

    private final ModifyInstructionPort modifyInstructionPort;


    /**
     * Each abstracted instruction returns the base address of the next instruction this can be used for chaining
     **/
    public void mul(int sourceRegister, int repetitionRegister, int destinationRegister, int incrementerRegister) {
        // setup
        write2sComplement(destinationRegister, 0); // initialize the output
        int baseAddress = getNextInstructionPort.getNextInstruction();

        // check signs case positive, negative
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.cmp(repetitionRegister, destinationRegister))); // check against zero (init of destination)              // baseAddress
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.beq(baseAddress + 32))); // skip computation if repetition is zero                              // +2
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.ble(baseAddress + 20))); // skip inversions if repetition is positive                        // +4

        // swap registers
        uncheckedSwapRegisters(sourceRegister, repetitionRegister, incrementerRegister, destinationRegister); // re-initializes destinationRegister   // +6

        // re-check signs in case of negative-negative
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.cmp(repetitionRegister, destinationRegister))); // check against zero (init of destination)             // +12
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.ble(baseAddress + 20))); // skip second inversion if repetition is positive                 // +14

        // invert both input registers against zero in destination (subtract the number to invert from zero)
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.sub(destinationRegister, sourceRegister, sourceRegister)));                                             // +16
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.sub(destinationRegister, repetitionRegister, repetitionRegister)));                                     // +18


        // setup for loop
        write2sComplement(incrementerRegister, 1); // store 1 to increment and compare to                    // +20

        // loop
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.add(sourceRegister, destinationRegister, destinationRegister))); // perform the addition               // +24
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.sub(repetitionRegister, incrementerRegister, repetitionRegister))); // decrement the counter           // +26
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.cmp(repetitionRegister, incrementerRegister))); // check for remaining cycle                         // +28
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.ble(baseAddress + 24))); // repeat if needed                                               // +30

        // (next instruction) +32
    }

    public void write2sComplement(int register, int number) {
        int complement = ((short) number) & 0xFFFF;
        int lower = complement & 0x00FF;
        int upper = (complement & 0xFF00) >>> 8;
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.mov(register, lower)));
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.movt(register, upper)));
    }

    public void swapRegisters(int swapRegister1, int swapRegister2, int register3, int register4) {
        // ensure register4 is clear
        write2sComplement(register4, 0);

        // perform unsafe swap
        uncheckedSwapRegisters(swapRegister1, swapRegister2, register3, register4);
    }

    private void uncheckedSwapRegisters(int swapRegister1, int swapRegister2, int register3, int register4) {

        // buffer register1
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.add(swapRegister1, register4, register3)));

        // move register2 to register1
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.add(swapRegister2, register4, swapRegister1)));

        // restore register1 into register2
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.add(register3, register4, swapRegister2)));
    }

    public void invertRegister(int inversionRegister, int emptyRegister) {
        // ensure emptyRegister is clear
        write2sComplement(emptyRegister, 0);

        // subtract the number to invert from zero
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.sub(emptyRegister, inversionRegister, inversionRegister)));
    }

    public void div(int dividendRegister, int divisorRegister, int destinationRegister, int incrementerRegister) {
        // setup
        write2sComplement(destinationRegister, 0); // initialize the output
        int baseAddress = getNextInstructionPort.getNextInstruction();

        // check zero or negative dividend
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.cmp(dividendRegister, destinationRegister)));                                        // baseAddress
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.beq(baseAddress + 36))); // skip computation if dividend == 0                // +2
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.ble(baseAddress + 10))); // skip register flip if dividend >= 0           // +4
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.sub(destinationRegister, dividendRegister, dividendRegister))); // flip dividend      // +6
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.sub(destinationRegister, divisorRegister, divisorRegister))); // flip divisor         // +8

        // check zero or positive divisor
        write2sComplement(incrementerRegister, 1); // add increment                        // +10
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.cmp(divisorRegister, destinationRegister)));                                        // +14
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.beq(baseAddress + 36))); // skip computation if divisor == 0                // +16
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.ble(baseAddress + 24))); // skip negative compensation if divisor >= 0    // +18
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.sub(destinationRegister, divisorRegister, divisorRegister))); // flip divisor         // +20
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.sub(destinationRegister, incrementerRegister, incrementerRegister))); // flip incrementer // +22

        // skip to computation start
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.cmp(incrementerRegister, incrementerRegister))); // compare to generate zero carry   // +24
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.beq(baseAddress + 32))); // jump to comparison to start computation           // + 26

        // compute result
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.sub(dividendRegister, divisorRegister, dividendRegister))); // dividend - divisor     // +28
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.add(destinationRegister, incrementerRegister, destinationRegister))); // incrementer  // +30
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.cmp(dividendRegister, divisorRegister)));                                            // +32
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.ble(baseAddress + 28))); // repeat if divisor <= dividend                //  +34

        // (next instruction) +36
    }

    public void equals(int source1register, int source2register, int destinationRegister) {

        int baseAddress = getNextInstructionPort.getNextInstruction();

        // compare and branch to true block if equal
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.cmp(source1register, source2register)));                    // base address
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.beq(baseAddress + 12)));                           // +2

        // false block and branch over true block
        write2sComplement(destinationRegister, 0);                // +4
        // compare to generate zero carry to skip true block
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.cmp(destinationRegister, destinationRegister)));            // +8
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.beq(baseAddress + 16)));                           // +10

        // true block
        write2sComplement(destinationRegister, 1);                // +12

        // (next instruction) +16

    }

    public void greaterThan(int source1register, int source2register, int destinationRegister) {
        int baseAddress = getNextInstructionPort.getNextInstruction();

        // compare and branch to true false if not greater than (less than)
        // Note scrap machine compares B against A, thus we compare 2 to 1
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.cmp(source2register, source1register)));                    // base address
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.ble(baseAddress + 12)));                           // +2

        // true block and branch over false block
        write2sComplement(destinationRegister, 1);                // +4
        // compare to generate zero carry to skip true block
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.cmp(destinationRegister, destinationRegister)));            // +8
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.beq(baseAddress + 16)));                           // +10

        // false block
        write2sComplement(destinationRegister, 0);                // +12

        // (next instruction) +16
    }


    // Control Structures

    // IF:
    // Manual: Before running if, the comparison must be committed to the application.
    // given the result, startIf will create the start of the if clause
    // after this commit the code in the clause
    // finally, call endIf with the instruction returned from the startIf such that the code can add the branch address.
    public Instruction startIfOrWhile(int comparisonResultRegister, int freeRegister1_sourceBuffer, int freeRegister2_zeroReference) {
        // create an empty register
        write2sComplement(freeRegister1_sourceBuffer, 0);

        // extract the boolean bit from the comparison result to the empty register
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.xor(comparisonResultRegister, freeRegister1_sourceBuffer, freeRegister1_sourceBuffer)));

        // compare against false (skip if condition not met)
        write2sComplement(freeRegister2_zeroReference, 0);
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.cmp(freeRegister1_sourceBuffer, freeRegister2_zeroReference)));

        // branch with temporary address
        int baseAddress = getNextInstructionPort.getNextInstruction();
        // include the current address to ensure the branch instruction is unique
        Instruction incompleteBranchInstruction = Instruction.initialize(AssemblyGenerator.beq(baseAddress));
        addInstructionPort.commitInstructionRow(incompleteBranchInstruction);

        return incompleteBranchInstruction;
    }

    public void endIf(Instruction incompleteBranch) {
        // find the end of the block
        int afterBlockAddress = getNextInstructionPort.getNextInstruction();

        // fill in the block end to the branch instruction at the beginning
        modifyInstructionPort.updateInstruction(incompleteBranch, Instruction.initialize(AssemblyGenerator.beq(afterBlockAddress)));
    }

    public void endWhile(Instruction incompleteBranch, int blockStartAddress) {
        // force equals flag - create force branch to start
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.cmp(0, 0)));
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.beq(blockStartAddress)));


        // find the end of the block
        int afterBlockAddress = getNextInstructionPort.getNextInstruction();

        // fill in the block end to the branch instruction at the beginning
        modifyInstructionPort.updateInstruction(incompleteBranch, Instruction.initialize(AssemblyGenerator.beq(afterBlockAddress)));
    }
}
