package com.idt.compiler.processorAbstraction.application.service;

import com.idt.compiler.common.globalDomain.Instruction;
import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.common.globalDomain.variableManagement.VariableManagementService;
import com.idt.compiler.common.globalDomain.variableManagement.VariableScope;
import com.idt.compiler.processorAbstraction.application.port.in.InstructionGenerationUseCase;
import com.idt.compiler.processorAbstraction.application.port.out.domainConnectors.AddInstructionPort;
import com.idt.compiler.processorAbstraction.application.port.out.persistence.LoadPAModelPort;
import com.idt.compiler.processorAbstraction.domain.AbstractedInstructionGenerationService;
import com.idt.compiler.processorAbstraction.domain.registerManagement.FutureRegister;
import com.idt.compiler.processorAbstraction.domain.registerManagement.Register;
import com.idt.globalDependencies.assemblyProcessor.AssemblyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class InstructionGenerationService implements InstructionGenerationUseCase {

    private final LoadPAModelPort loadPAModelPort;
    private final VariableManagementService variableManagementService;
    private final AbstractedInstructionGenerationService abstractedInstructionGenerationService;

    private final AddInstructionPort addInstructionPort;


    protected void writeRegisterImmediate(Register register, int value) {

        abstractedInstructionGenerationService.write2sComplement(register.getIndex(), value);
    }

    @Override
    public void writeIntVar(int value, Variable variable) {
        // INV: must not write to copy
        if (variable.isCopy()) {
            throw new IllegalArgumentException("InstructionGenerator: Can not write to a copy variable: " + variable.getName());
        }

        // request the variable
        FutureRegister assignmentRegister = variableManagementService.requestVariable(variable);

        // indicate requests are done
        variableManagementService.assignFutureRegisters();

        writeRegisterImmediate(assignmentRegister.getReg(), value);


        // unlock variable
        variable.unlock();
    }

    private enum TwoSourceDestinationOperations {add, subtract, and, or, xor}

    private void twoSourceDestinationOperation(Variable source1, Variable source2, Variable destination, TwoSourceDestinationOperations operation) {
        // INV: destination must not be a copy
        if (destination.isCopy()) {
            throw new IllegalArgumentException("InstructionGenerator: Can not write to a copy variable: " + destination.getName());
        }

        // request the variables
        FutureRegister destinationRegister = variableManagementService.requestVariable(destination);
        FutureRegister source1Register = variableManagementService.requestVariable(source1);
        FutureRegister source2Register = variableManagementService.requestVariable(source2);

        // complete the future assignment
        variableManagementService.assignFutureRegisters();

        // perform the operation
        switch (operation) {
            case add ->
                    addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.add(source1Register.getReg().getIndex(), source2Register.getReg().getIndex(), destinationRegister.getReg().getIndex())));
            case subtract ->
                    addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.sub(source1Register.getReg().getIndex(), source2Register.getReg().getIndex(), destinationRegister.getReg().getIndex())));
            case and ->
                    addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.and(source1Register.getReg().getIndex(), source2Register.getReg().getIndex(), destinationRegister.getReg().getIndex())));
            case or ->
                    addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.or(source1Register.getReg().getIndex(), source2Register.getReg().getIndex(), destinationRegister.getReg().getIndex())));
            case xor ->
                    addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.xor(source1Register.getReg().getIndex(), source2Register.getReg().getIndex(), destinationRegister.getReg().getIndex())));
            default -> {
                System.out.println("InstructionGenerator: twoSourceDestinationOperation error");
                throw new RuntimeException();
            }
        }

        // unlock the variables
        destination.unlock();
        source1.unlock();
        source2.unlock();
    }

    @Override
    public void not(Variable source, Variable destination) {
        // INV: destination must not be a copy
        if (destination.isCopy()) {
            throw new IllegalArgumentException("InstructionGenerator: Can not write to a copy variable: " + destination.getName());
        }

        // request th variables
        FutureRegister sourceRegister = variableManagementService.requestVariable(source);
        FutureRegister destinationRegister = variableManagementService.requestVariable(destination);

        // complete the future assignment
        variableManagementService.assignFutureRegisters();

        // perform the operation
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.not(sourceRegister.getReg().getIndex(), destinationRegister.getReg().getIndex())));

        // unlock the variables
        source.unlock();
        destination.unlock();
    }

    @Override
    public void equals(Variable source1, Variable source2, Variable destination) {
        // INV: destination must not be a copy
        if (destination.isCopy()) {
            throw new IllegalArgumentException("InstructionGenerator: Can not write to a copy variable: " + destination.getName());
        }

        // request the variables
        FutureRegister destinationRegister = variableManagementService.requestVariable(destination);
        FutureRegister source1register = variableManagementService.requestVariable(source1);
        FutureRegister source2register = variableManagementService.requestVariable(source2);

        // complete the future assignment
        variableManagementService.assignFutureRegisters();

        // perform the operation
        abstractedInstructionGenerationService.equals(source1register.getReg().getIndex(), source2register.getReg().getIndex(), destinationRegister.getReg().getIndex());
        source1.unlock();
        source2.unlock();
        destination.unlock();
    }

    @Override
    public void isGreaterThan(Variable source1, Variable source2, Variable destination) {
        // INV: destination must not be a copy
        if (destination.isCopy()) {
            throw new IllegalArgumentException("InstructionGenerator: Can not write to a copy variable: " + destination.getName());
        }

        // request the variables
        FutureRegister destinationRegister = variableManagementService.requestVariable(destination);
        FutureRegister source1register = variableManagementService.requestVariable(source1);
        FutureRegister source2register = variableManagementService.requestVariable(source2);

        // complete the future assignment
        variableManagementService.assignFutureRegisters();

        // perform the operation
        abstractedInstructionGenerationService.greaterThan(source1register.getReg().getIndex(), source2register.getReg().getIndex(), destinationRegister.getReg().getIndex());
        source1.unlock();
        source2.unlock();
        destination.unlock();
    }


    @Override
    public void add(Variable source1, Variable source2, Variable destination) {
        twoSourceDestinationOperation(source1, source2, destination, TwoSourceDestinationOperations.add);
    }

    @Override
    public void sub(Variable source1, Variable source2, Variable destination) {
        twoSourceDestinationOperation(source1, source2, destination, TwoSourceDestinationOperations.subtract);
    }

    @Override
    public void mul(Variable factor1, Variable factor2, Variable destination) {
        mulDivBase(factor1, factor2, destination, false);
    }

    @Override
    public void div(Variable dividend, Variable divisor, Variable destination) {
        mulDivBase(dividend, divisor, destination, true);
    }

    private void mulDivBase(Variable firstInput, Variable secondInput, Variable destination, boolean divideMode) {
        // INV: destination must not be a copy
        if (destination.isCopy()) {
            throw new IllegalArgumentException("InstructionGenerator: Can not write to a copy variable: " + destination.getName());
        }

        // create copy session
        VariableScope localVariableScope = loadPAModelPort.getProcessorAbstraction().createScope("mulDivBase" + UUID.randomUUID());

        // create copies of the input variables to allow use of the same variable at multiple places
        Variable firstInputCopy = loadPAModelPort.getProcessorAbstraction().createTempCopy(localVariableScope, firstInput);
        Variable secondInputCopy = loadPAModelPort.getProcessorAbstraction().createTempCopy(localVariableScope, secondInput);

        // request the variable registers
        FutureRegister firstInputRegister = variableManagementService.requestVariable(firstInputCopy);
        FutureRegister secondInputRegister = variableManagementService.requestVariable(secondInputCopy);
        FutureRegister destinationRegister = variableManagementService.requestVariable(destination);

        // create a temporary variable for the incrementer and request a register for it
        Variable placeholder = loadPAModelPort.getProcessorAbstraction().allocateTemporaryVariable(localVariableScope, "INCREMENTER_" + UUID.randomUUID());
        FutureRegister incrementer = variableManagementService.requestVariable(placeholder);

        // assign the registers
        variableManagementService.assignFutureRegisters();

        if (divideMode) {
            // divide
            abstractedInstructionGenerationService.div(firstInputRegister.getReg().getIndex(), secondInputRegister.getReg().getIndex(), destinationRegister.getReg().getIndex(), incrementer.getReg().getIndex());
        } else {
            // multiply
            abstractedInstructionGenerationService.mul(firstInputRegister.getReg().getIndex(), secondInputRegister.getReg().getIndex(), destinationRegister.getReg().getIndex(), incrementer.getReg().getIndex());
        }

        // unlock the blocked variables
        firstInput.unlock();
        secondInput.unlock();
        destination.unlock();
        placeholder.unlock();

        // delete the local scope
        loadPAModelPort.getProcessorAbstraction().exitScope(localVariableScope);
    }

    @Override
    public void and(Variable source1, Variable source2, Variable destination) {
        twoSourceDestinationOperation(source1, source2, destination, TwoSourceDestinationOperations.and);
    }

    @Override
    public void or(Variable source1, Variable source2, Variable destination) {
        twoSourceDestinationOperation(source1, source2, destination, TwoSourceDestinationOperations.or);
    }

    @Override
    public void xor(Variable source1, Variable source2, Variable destination) {
        twoSourceDestinationOperation(source1, source2, destination, TwoSourceDestinationOperations.xor);
    }

    @Override
    public void loadVariableToRegister(Variable variable) {
        FutureRegister register = variableManagementService.requestVariable(variable);
        variableManagementService.assignFutureRegisters();
        variable.unlock();
    }

    @Override
    public void setHighGPIO(int address) {
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.gpioSet(address)));
    }

    @Override
    public void setLowGPIO(int address) {
        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.gpioReset(address)));
    }

    @Override
    public void readGPIO(int address, Variable storeTo) {

        FutureRegister storeDestination = variableManagementService.requestVariable(storeTo);

        variableManagementService.assignFutureRegisters();

        addInstructionPort.commitInstructionRow(Instruction.initialize(AssemblyGenerator.gpioRead(address, storeDestination.getReg().getIndex())));

        storeTo.unlock();
    }


    // controlStructures
    // NOTE: all registers must be safe cleared before and after changing to a conditional scope to prevent schroedingers register from occurring

    @Override
    public Instruction ifStart(Variable comparison) {

        // ensure no Schroedingers Register
        loadPAModelPort.getProcessorAbstraction().safeClearAllRegisters();

        // create copy session for temporary variables
        VariableScope localVariableScope = loadPAModelPort.getProcessorAbstraction().createScope("tempIfStart_" + UUID.randomUUID());


        // create a temporary variable for the reference and request a register for it
        Variable zeroReference = loadPAModelPort.getProcessorAbstraction().allocateTemporaryVariable(localVariableScope, "ZERO_REFERENCE_" + UUID.randomUUID());
        // create a temporary variable for the reference and request a register for it
        Variable sourceBuffer = loadPAModelPort.getProcessorAbstraction().allocateTemporaryVariable(localVariableScope, "SOURCE_BUFFER_" + UUID.randomUUID());

        // request the variable registers
        FutureRegister comparisonRegister = variableManagementService.requestVariable(comparison);
        FutureRegister zeroReferenceRegister = variableManagementService.requestVariable(zeroReference);
        FutureRegister sourceBufferRegister = variableManagementService.requestVariable(sourceBuffer);

        variableManagementService.assignFutureRegisters();

        Instruction incompleteBranch = abstractedInstructionGenerationService.startIfOrWhile(comparisonRegister.getReg().getIndex(), sourceBufferRegister.getReg().getIndex(), zeroReferenceRegister.getReg().getIndex());

        comparison.unlock();
        zeroReference.unlock();
        sourceBuffer.unlock();

        // delete the local scope
        loadPAModelPort.getProcessorAbstraction().exitScope(localVariableScope);

        return incompleteBranch;
    }

    @Override
    public Instruction whileStart(Variable comparison) {

        // ensure no Schroedingers Register
        loadPAModelPort.getProcessorAbstraction().safeClearAllRegisters();

        // create copy session for temporary variables
        VariableScope localVariableScope = loadPAModelPort.getProcessorAbstraction().createScope("tempWhileStart_" + UUID.randomUUID());


        // create a temporary variable for the reference and request a register for it
        Variable zeroReference = loadPAModelPort.getProcessorAbstraction().allocateTemporaryVariable(localVariableScope, "ZERO_REFERENCE_" + UUID.randomUUID());
        // create a temporary variable for the reference and request a register for it
        Variable sourceBuffer = loadPAModelPort.getProcessorAbstraction().allocateTemporaryVariable(localVariableScope, "SOURCE_BUFFER_" + UUID.randomUUID());

        // request the variable registers
        FutureRegister comparisonRegister = variableManagementService.requestVariable(comparison);
        FutureRegister zeroReferenceRegister = variableManagementService.requestVariable(zeroReference);
        FutureRegister sourceBufferRegister = variableManagementService.requestVariable(sourceBuffer);

        variableManagementService.assignFutureRegisters();

        Instruction incompleteBranch = abstractedInstructionGenerationService.startIfOrWhile(comparisonRegister.getReg().getIndex(), sourceBufferRegister.getReg().getIndex(), zeroReferenceRegister.getReg().getIndex());

        comparison.unlock();
        zeroReference.unlock();
        sourceBuffer.unlock();

        // delete the local scope
        loadPAModelPort.getProcessorAbstraction().exitScope(localVariableScope);

        return incompleteBranch;
    }

    @Override
    public void ifEnd(Instruction incompleteBranch) {
        // ensure no Schroedingers Register
        loadPAModelPort.getProcessorAbstraction().safeClearAllRegisters();

        // set the end of the if
        abstractedInstructionGenerationService.endIf(incompleteBranch);
    }

    @Override
    public void whileEnd(Instruction incompleteBranch, int blockStartBranchAddress) {
        // ensure no Schroedingers Register
        loadPAModelPort.getProcessorAbstraction().safeClearAllRegisters();

        // set the end of the if
        abstractedInstructionGenerationService.endWhile(incompleteBranch, blockStartBranchAddress);

    }


    // TODO Remove debug tool:
    public void printRam() {
        this.loadPAModelPort.getProcessorAbstraction().printRam();
    }


}
