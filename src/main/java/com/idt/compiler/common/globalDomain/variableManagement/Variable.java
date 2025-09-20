package com.idt.compiler.common.globalDomain.variableManagement;

import com.idt.compiler.processorAbstraction.domain.registerManagement.Register;
import lombok.Getter;

import java.util.UUID;

@Getter
public class Variable {


    /**
     * tracks if the variable is safe to be moved to ram
     **/
    private boolean safeToMoveToRam;

    /**
     * tracks if the variable has been moved to memory at least once
     **/
    private boolean isInitialized;

    private VarStat status;
    private Register register;
    private final int ramCell;
    private final String name;

    /**
     * mark variables which reference to a cell of another variable
     **/
    private final boolean isCopy;


    private Variable(int cellAddress, String name, boolean isCopy) {
        this.ramCell = cellAddress;
        this.name = name;
        this.status = VarStat.unassigned;
        this.isInitialized = false;
        this.isCopy = isCopy;
    }

    public Variable(int cellAddress, String name) {
        this.ramCell = cellAddress;
        this.name = name;
        this.status = VarStat.unassigned;
        this.isInitialized = false;
        this.isCopy = false;
    }

    // copy constructor (this must only be accessible through the management domain service)
    @SuppressWarnings("CopyConstructorMissesField")
    protected Variable(Variable copySource) {

        this(copySource.getRamCell(), copySource.getName() + "_CP_" + UUID.randomUUID(), true);

        // to guarantee copy is loaded correctly it is initialized to the same ram cell
        this.isInitialized = true;
        this.status = VarStat.inRam;
    }

    private void initialize() {
        this.isInitialized = true;
    }

    private void assignToRegister(Register register) {
        this.status = VarStat.inRegister;
        this.register = register;
    }


    /**
     * control the status of there the variable currently is
     **/
    protected void setMovedToRam() {
        // INV: ensure the variable has been initialized before
        if (!this.isInitialized) {
            throw new IllegalCallerException("Attempt to move un initialized variable to ram");
        }

        // free the associated register
        this.register.free();

        this.status = VarStat.inRam;
        this.register = null;
    }

    protected void setMovedToRegister(Register register) {

        // update the variable status
        assignToRegister(register);
        this.safeToMoveToRam = false;

        // initialize the variable the first time it is used
        initialize();
    }

    public void unlock() {
        this.safeToMoveToRam = true;
    }


}
