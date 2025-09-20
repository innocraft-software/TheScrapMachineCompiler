package com.idt.compiler.common.globalDomain.variableManagement;

import com.idt.compiler.processorAbstraction.domain.registerManagement.Register;
import com.idt.compiler.processorAbstraction.domain.registerManagement.RegisterModule;
import com.idt.compiler.processorAbstraction.domain.registerManagement.RegisterOverflowException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VariableManagementSessionTest {

    private VariableManagementSession variableManagementSession;
    private RegisterModule registerModule;

    @BeforeEach
    void initialize() {
        registerModule = RegisterModule.initialize();
        variableManagementSession = VariableManagementSession.initialize(registerModule);
    }

    @Test
    void initializationTest() {
        assertEquals(VariableManagementSession.VariableManagementSessionState.INITIALIZED, variableManagementSession.getVariableManagementSessionState());
        assertEquals(0, variableManagementSession.getVariablesInAssignment().assignmentSize());
        assertEquals(0, variableManagementSession.getFinalAssignment().size());
        assertEquals(this.registerModule, variableManagementSession.getRegisterModule());
    }

    @Test
    void givenInitializedSession_whenRequestingVariable_thenRequestSuccessfulTest() {
        Variable variable = new Variable(0, "TestVar");
        variableManagementSession.requestVariable(variable);
        assertEquals(1, variableManagementSession.getVariablesInAssignment().assignmentSize());
        assertEquals(0, variableManagementSession.getFinalAssignment().size());
    }

    @Test
    void givenSessionNoLongerInInitPhase_whenRequestingVariable_thenRequestRejectedTest() {
        Variable variable = new Variable(0, "TestVar");
        variableManagementSession.requestReUse();
        assertThrows(IllegalCallerException.class, () -> variableManagementSession.requestVariable(variable));
    }

    @Test
    void givenReUsableVariable_whenAttemptingReUse_thenReUsableVariableFoundTest() throws RegisterOverflowException {
        // create variable and write it to the register
        Variable reUsable = new Variable(0, "ReUsable");
        Register freeRegister = registerModule.findFreeRegister();
        freeRegister.setVariable(reUsable);

        // free the register
        freeRegister.free();

        // request the variable from the VMS
        variableManagementSession.requestVariable(reUsable);

        // attempt re use
        Map<Variable, Register> toBeReUsed = variableManagementSession.requestReUse();

        assertTrue(toBeReUsed.containsKey(reUsable));
        assertEquals(VariableManagementSession.VariableManagementSessionState.AWAITING_REUSE_CONFIRMATION, variableManagementSession.getVariableManagementSessionState());
        assertEquals(0, variableManagementSession.getFinalAssignment().size());
        assertEquals(0, variableManagementSession.getVariablesInAssignment().assignmentSize());
    }

    @Test
    void givenNonReUsableVariable_whenAttemptingReUse_thenNoReusableVariablesFoundTest() {
        // create variable and write it to the register
        Variable nonReUsable = new Variable(0, "NonReUsable");

        // request the variable from the VMS
        variableManagementSession.requestVariable(nonReUsable);

        // attempt re use
        Map<Variable, Register> toBeReUsed = variableManagementSession.requestReUse();

        assertFalse(toBeReUsed.containsKey(nonReUsable));
        assertEquals(VariableManagementSession.VariableManagementSessionState.AWAITING_REUSE_CONFIRMATION, variableManagementSession.getVariableManagementSessionState());
        assertEquals(0, variableManagementSession.getFinalAssignment().size());
        assertEquals(1, variableManagementSession.getVariablesInAssignment().assignmentSize());
    }

    @Test
    void givenSessionAfterReUse_whenReUse_thenReUseFailsTest() {
        // attempt re use
        variableManagementSession.requestReUse();

        // redo re-use should fail
        assertThrows(IllegalCallerException.class, () -> variableManagementSession.requestReUse());
    }

    @Test
    void givenSessionBeforeReUse_whenReUseCommitted_thenConfirmationRejectedTest() {
        assertThrows(IllegalCallerException.class, () -> variableManagementSession.commitReUse());
    }

    @Test
    void givenSessionAwaitingReuseCommit_whenReUseCommitted_thenCommitIsSuccessfulTest() {
        variableManagementSession.requestReUse();
        variableManagementSession.commitReUse();
    }

    @Test
    void givenSessionAfterReUseCommit_whenReUseCommitted_thenReUseCommitRejectedTest() {
        variableManagementSession.requestReUse();
        variableManagementSession.commitReUse();
        assertThrows(IllegalCallerException.class, () -> variableManagementSession.commitReUse());
    }

    @Test
    void givenSessionBeforeReUseCommit_whenFreeingRequiredSpace_thenRequestFailsTest() {
        variableManagementSession.requestReUse();
        assertThrows(IllegalCallerException.class, () -> variableManagementSession.freeRequiredSpace());
    }

    @Test
    void givenSessionAfterReUseCommit_whenFreeingRequiredSpace_thenRequestSucceedsTest() {
        variableManagementSession.requestReUse();
        variableManagementSession.commitReUse();
        variableManagementSession.freeRequiredSpace();
    }

    @Test
    void givenSessionAfterFreeRequiredSpace_whenFreeingRequiredSpace_thenRequestFailsTest() {
        variableManagementSession.requestReUse();
        variableManagementSession.commitReUse();
        variableManagementSession.freeRequiredSpace();
        assertThrows(IllegalCallerException.class, () -> variableManagementSession.freeRequiredSpace());
    }

    @Test
    void givenRegisterSpaceAvailable_whenFreeingRequiredSpace_thenNoMemoryFreedTest() {
        // create variable and write it to the register
        Variable nonReUsable = new Variable(0, "NonReUsable");

        // request the variable from the VMS
        variableManagementSession.requestVariable(nonReUsable);

        // attempt re use (check if it fails)
        assertEquals(0, variableManagementSession.requestReUse().size());

        // commit re use
        variableManagementSession.commitReUse();

        // free required space
        List<Variable> toBeMovedToRam = variableManagementSession.freeRequiredSpace();

        assertEquals(0, toBeMovedToRam.size());

    }

    @Test
    void givenFullRegisters_whenFreeingRequiredSpace_thenRegisterIsChosenToBeMovedToRamTest() throws RegisterOverflowException {

        // fill the registers
        for (int registerIndex = 0; registerIndex < RegisterModule.REGISTER_COUNT; registerIndex++) {
            // assign variable to register
            Register freeRegister = this.registerModule.findFreeRegister();
            Variable variable = new Variable(registerIndex, String.valueOf(registerIndex));
            freeRegister.setVariable(variable);

            // pretend to use the variable
            variable.setMovedToRegister(freeRegister);

            // unlock
            variable.unlock();
        }

        // create variable and write it to the register
        Variable nonReUsable = new Variable(0, "NonReUsable");

        // request the variable from the VMS
        variableManagementSession.requestVariable(nonReUsable);

        // attempt re use (check if it fails)
        assertEquals(0, variableManagementSession.requestReUse().size());

        // commit re use
        variableManagementSession.commitReUse();

        // free required space
        List<Variable> toBeMovedToRam = variableManagementSession.freeRequiredSpace();

        assertEquals(1, toBeMovedToRam.size());

        assertEquals(0, variableManagementSession.getFinalAssignment().size());
        assertEquals(1, variableManagementSession.getVariablesInAssignment().assignmentSize());

        assertEquals(VariableManagementSession.VariableManagementSessionState.AWAITING_MEMORY_RESERVATION, variableManagementSession.getVariableManagementSessionState());

    }

    @Test
    void givenFullRegisters_whenFreeingRequiredSpaceForMultipleVariables_thenCorrectNumberIsChosenToBeMovedToRamTest() throws RegisterOverflowException {
        // fill the registers
        for (int registerIndex = 0; registerIndex < RegisterModule.REGISTER_COUNT; registerIndex++) {
            // assign variable to register
            Register freeRegister = this.registerModule.findFreeRegister();
            Variable variable = new Variable(registerIndex, String.valueOf(registerIndex));
            freeRegister.setVariable(variable);

            // pretend to use the variable
            variable.setMovedToRegister(freeRegister);

            // unlock
            variable.unlock();
        }

        // create variable and write it to the register
        Variable nonReUsable1 = new Variable(0, "NonReUsable1");
        Variable nonReUsable2 = new Variable(0, "NonReUsable2");
        Variable nonReUsable3 = new Variable(0, "NonReUsable3");

        // request the variable from the VMS
        variableManagementSession.requestVariable(nonReUsable1);
        variableManagementSession.requestVariable(nonReUsable2);
        variableManagementSession.requestVariable(nonReUsable3);


        // attempt re use (check if it fails)
        assertEquals(0, variableManagementSession.requestReUse().size());

        // commit re use
        variableManagementSession.commitReUse();

        // free required space
        List<Variable> toBeMovedToRam = variableManagementSession.freeRequiredSpace();

        assertEquals(3, toBeMovedToRam.size());

        assertEquals(0, variableManagementSession.getFinalAssignment().size());
        assertEquals(3, variableManagementSession.getVariablesInAssignment().assignmentSize());

        assertEquals(VariableManagementSession.VariableManagementSessionState.AWAITING_MEMORY_RESERVATION, variableManagementSession.getVariableManagementSessionState());

    }

    @Test
    void givenSessionAwaitingMemoryReservation_whenMemoryReservation_thenMemoryReservationSuccessfulTest() {
        variableManagementSession.requestReUse();
        variableManagementSession.commitReUse();
        variableManagementSession.freeRequiredSpace();
        variableManagementSession.reserveMemory();

        assertEquals(VariableManagementSession.VariableManagementSessionState.AWAITING_ASSIGNMENT_CONFIRMATION, variableManagementSession.getVariableManagementSessionState());
    }

    @Test
    void givenSessionBeforeFreeingMemory_whenMemoryReservation_thenMemoryReservationRejectedTest() {
        variableManagementSession.requestReUse();
        variableManagementSession.commitReUse();
        assertThrows(IllegalCallerException.class, () -> variableManagementSession.reserveMemory());
    }

    @Test
    void givenSessionAfterMemoryReservation_whenMemoryReservation_thenMemoryReservationRejectedTest() {
        variableManagementSession.requestReUse();
        variableManagementSession.commitReUse();
        variableManagementSession.freeRequiredSpace();
        variableManagementSession.reserveMemory();
        assertThrows(IllegalCallerException.class, () -> variableManagementSession.reserveMemory());
    }

    @Test
    void givenSessionAfterMemoryReservationWithInsufficientSpace_whenMemoryReservation_thenMemoryReservationFailsTest() throws RegisterOverflowException {
        // fill the registers
        for (int registerIndex = 0; registerIndex < RegisterModule.REGISTER_COUNT; registerIndex++) {
            Register freeRegister = this.registerModule.findFreeRegister();
            freeRegister.setVariable(new Variable(registerIndex, String.valueOf(registerIndex)));
        }

        // create variable and write it to the register
        Variable nonReUsable = new Variable(0, "NonReUsable");

        // request the variable from the VMS
        variableManagementSession.requestVariable(nonReUsable);

        // attempt re use (check if it fails)
        assertEquals(0, variableManagementSession.requestReUse().size());

        // commit re use
        variableManagementSession.commitReUse();

        // free required space
        variableManagementSession.freeRequiredSpace();

        // DO NOT move the variables to RAM and try to continue
        assertThrows(IllegalCallerException.class, () -> variableManagementSession.reserveMemory());

    }

    @Test
    void givenSessionAwaitingMemoryReservationWithCorrectNumberOfFreeCells_whenMemoryReservation_thenMemoryReservationCorrectlyAssignsCellsTest() {
        // create variable
        Variable nonReUsable = new Variable(0, "NonReUsable");

        // request the variable from the VMS
        variableManagementSession.requestVariable(nonReUsable);

        // attempt reuse (check if it fails)
        assertEquals(0, variableManagementSession.requestReUse().size());

        // commit re use
        variableManagementSession.commitReUse();

        // check if space needs to be freed (check that none need to be)
        assertEquals(0, variableManagementSession.freeRequiredSpace().size());

        // perform the reservation
        Map<Variable, Register> reservations = variableManagementSession.reserveMemory();

        assertTrue(reservations.containsKey(nonReUsable));

        assertEquals(1, variableManagementSession.getFinalAssignment().size());

        // check that the variable is not yet confirmed
        assertNull(nonReUsable.getRegister());

        assertEquals(VariableManagementSession.VariableManagementSessionState.AWAITING_ASSIGNMENT_CONFIRMATION, variableManagementSession.getVariableManagementSessionState());


    }

    @Test
    void givenSessionAwaitingMemoryReservationWithCorrectNumberOfFreeCellsMultipleVariables_whenMemoryReservation_thenMemoryReservationCorrectlyAssignsCellsTest() {
        // create variables
        Variable nonReUsable1 = new Variable(0, "NonReUsable1");
        Variable nonReUsable2 = new Variable(0, "NonReUsable2");
        Variable nonReUsable3 = new Variable(0, "NonReUsable3");

        // request the variable from the VMS
        variableManagementSession.requestVariable(nonReUsable1);
        variableManagementSession.requestVariable(nonReUsable2);
        variableManagementSession.requestVariable(nonReUsable3);

        // attempt reuse (check if it fails)
        assertEquals(0, variableManagementSession.requestReUse().size());

        // commit re use
        variableManagementSession.commitReUse();

        // check if space needs to be freed (check that none need to be)
        assertEquals(0, variableManagementSession.freeRequiredSpace().size());

        // perform the reservation
        Map<Variable, Register> reservations = variableManagementSession.reserveMemory();

        assertTrue(reservations.containsKey(nonReUsable1));
        assertTrue(reservations.containsKey(nonReUsable2));
        assertTrue(reservations.containsKey(nonReUsable3));

        // check individuality of registers
        assertNotEquals(reservations.get(nonReUsable1), reservations.get(nonReUsable2));
        assertNotEquals(reservations.get(nonReUsable2), reservations.get(nonReUsable3));
        assertNotEquals(reservations.get(nonReUsable1), reservations.get(nonReUsable3));

        assertEquals(3, variableManagementSession.getFinalAssignment().size());

        // check that the variables are not yet confirmed
        assertNull(nonReUsable1.getRegister());
        assertNull(nonReUsable2.getRegister());
        assertNull(nonReUsable3.getRegister());

    }

    @Test
    void givenSessionAwaitingAssignmentConfirmationAfterCorrectAssignment_whenAssignmentConfirmed_thenVariablesGetConfirmedTest() {
        // create variable and write it to the register
        Variable nonReUsable = new Variable(0, "NonReUsable");

        // request the variable from the VMS
        variableManagementSession.requestVariable(nonReUsable);

        // attempt reuse (check if it fails)
        assertEquals(0, variableManagementSession.requestReUse().size());

        // commit re use
        variableManagementSession.commitReUse();

        // check if space needs to be freed (check that none need to be)
        assertEquals(0, variableManagementSession.freeRequiredSpace().size());

        // perform the reservation
        Map<Variable, Register> reservations = variableManagementSession.reserveMemory();

        // do the assignment
        nonReUsable.setMovedToRegister(reservations.get(nonReUsable));

        // confirm the reservation
        variableManagementSession.confirmAssignment();


        // check that the variable is now confirmed
        assertNotNull(nonReUsable.getRegister());

        assertEquals(VariableManagementSession.VariableManagementSessionState.FINISHED, variableManagementSession.getVariableManagementSessionState());


    }

    @Test
    void givenSessionNotAwaitingAssignmentConfirmation_whenAssignmentConfirmed_thenConfirmationRequestRejected() {
        variableManagementSession.requestReUse();
        variableManagementSession.commitReUse();
        variableManagementSession.freeRequiredSpace();

        assertThrows(IllegalCallerException.class, () -> variableManagementSession.confirmAssignment());
    }

    @Test
    void givenSessionNotAfterAssignmentConfirmation_whenAssignmentConfirmed_thenConfirmationRequestRejected() {
        variableManagementSession.requestReUse();
        variableManagementSession.commitReUse();
        variableManagementSession.freeRequiredSpace();
        variableManagementSession.reserveMemory();
        variableManagementSession.confirmAssignment();

        assertThrows(IllegalCallerException.class, () -> variableManagementSession.confirmAssignment());
    }

    @Test
    void givenTestAfterFullExecutionCycle_whenResetCalls_thenSessionIsFullyReset() {

        // create variable and write it to the register
        Variable nonReUsable = new Variable(0, "NonReUsable");
        // request the variable from the VMS
        variableManagementSession.requestVariable(nonReUsable);
        // attempt reuse (check if it fails)
        variableManagementSession.requestReUse();
        // commit re use
        variableManagementSession.commitReUse();
        // check if space needs to be freed
        variableManagementSession.freeRequiredSpace();
        // perform the reservation
        Map<Variable, Register> reservations = variableManagementSession.reserveMemory();
        // do the assignment
        nonReUsable.setMovedToRegister(reservations.get(nonReUsable));
        // confirm the reservation
        variableManagementSession.confirmAssignment();

        // reset
        variableManagementSession.reset();

        // check post conditions
        assertEquals(VariableManagementSession.VariableManagementSessionState.INITIALIZED, variableManagementSession.getVariableManagementSessionState());
        assertEquals(0, variableManagementSession.getVariablesInAssignment().assignmentSize());
        assertEquals(0, variableManagementSession.getFinalAssignment().size());
    }


}