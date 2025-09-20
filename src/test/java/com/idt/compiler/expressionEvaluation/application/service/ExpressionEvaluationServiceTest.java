package com.idt.compiler.expressionEvaluation.application.service;

import com.idt.compiler.assembly.application.port.in.ManageApplicationUseCase;
import com.idt.compiler.common.globalDomain.variableManagement.Variable;
import com.idt.compiler.common.globalDomain.variableManagement.VariableScope;
import com.idt.compiler.expressionEvaluation.application.port.in.ExpressionEvaluationUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.InstructionGenerationUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.ScopeManagementUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.VariableManagementUseCase;
import com.idt.compiler.processorAbstraction.domain.registerManagement.Register;
import com.idt.simulator.Simulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ExpressionEvaluationServiceTest {

    private static final String testPath = "src/test/processAssemblyLocation";

    @Autowired
    private InstructionGenerationUseCase instructionGenerationUseCase;

    @Autowired
    private ManageApplicationUseCase manageApplicationUseCase;

    @Autowired
    private ScopeManagementUseCase scopeManagementUseCase;

    @Autowired
    private VariableManagementUseCase variableManagementUseCase;

    @Autowired
    private ExpressionEvaluationUseCase expressionEvaluationUseCase;

    private record TestVariable(
            Variable variable,
            Integer simulatorVariable
    ) {
        public boolean getBooleanSimulatorVariable() {
            return simulatorVariable.equals(1);
        }

        public Integer getSimulatorVariable() {
            return simulatorVariable;
        }
    }


    private VariableScope setUpSimulator() {
        // create new app
        manageApplicationUseCase.newApplication();

        // create global scope
        return scopeManagementUseCase.createScope("GLOBAL");
    }

    private Short compileWithExpressionAndRunSimulator(String expression, VariableScope globalScope) {
        // evaluate the expression
        Variable result = expressionEvaluationUseCase.evaluateExpression(expression, globalScope);

        // find the result
        Register resultRegister = result.getRegister();

        // exit the scope
        scopeManagementUseCase.exitScope(globalScope);

        // export the application as assembly
        manageApplicationUseCase.exportApplication(testPath);

        // run the simulator with result return
        System.setProperty("java.awt.headless", "false");
        return Simulator.simulate(testPath + "/binary.txt", (short) resultRegister.getIndex());
    }

    private TestVariable createTestVariable(VariableScope variableScope, String name) {
        Variable variable = variableManagementUseCase.assignIntVar(variableScope, name);
        return new TestVariable(variable, 0);
    }

    private TestVariable assignTestVariable(TestVariable variable, Integer value) {
        instructionGenerationUseCase.writeIntVar(value, variable.variable);
        return new TestVariable(variable.variable, value);
    }

    @BeforeEach
    void setSimSpeed() {
        Simulator.sleepTime = 0;
    }

    @Test
    @DirtiesContext
    void test_basicArithmeticExpression() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");

        // variable assignment
        a = assignTestVariable(a, -10);
        b = assignTestVariable(b, 20);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("A+B", globalScope).intValue();
        int resultFromJava = a.getSimulatorVariable() + b.getSimulatorVariable();

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_basicArithmeticExpressionInBraces() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");

        // variable assignment
        a = assignTestVariable(a, -10);
        b = assignTestVariable(b, 20);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("(A+B)", globalScope).intValue();
        int resultFromJava = (a.getSimulatorVariable() + b.getSimulatorVariable());

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_basicArithmeticExpressionWithInlinedValue() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");

        // variable assignment
        a = assignTestVariable(a, -10);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("A+20", globalScope).intValue();
        int resultFromJava = a.getSimulatorVariable() + 20;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_basicArithmeticExpressionWithPreEvaluation() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("10+20", globalScope).intValue();
        int resultFromJava = 10 + 20;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_arithmeticExpressionWithPrecedence() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");
        TestVariable c = createTestVariable(globalScope, "C");

        // variable assignment
        a = assignTestVariable(a, -10);
        b = assignTestVariable(b, 20);
        c = assignTestVariable(c, 3);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("A+B*C", globalScope).intValue();
        int resultFromJava = a.getSimulatorVariable() + b.getSimulatorVariable() * c.getSimulatorVariable();

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_arithmeticExpressionWithBraces() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");
        TestVariable c = createTestVariable(globalScope, "C");

        // variable assignment
        a = assignTestVariable(a, -10);
        b = assignTestVariable(b, 20);
        c = assignTestVariable(c, 3);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("(A+B)*C", globalScope).intValue();
        int resultFromJava = (a.getSimulatorVariable() + b.getSimulatorVariable()) * c.getSimulatorVariable();

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_arithmeticExpressionWithDoubleBraces() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");
        TestVariable c = createTestVariable(globalScope, "C");
        TestVariable d = createTestVariable(globalScope, "D");
        TestVariable e = createTestVariable(globalScope, "E");

        // variable assignment
        a = assignTestVariable(a, -10);
        b = assignTestVariable(b, 20);
        c = assignTestVariable(c, 3);
        d = assignTestVariable(d, 6);
        e = assignTestVariable(e, 5);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("((A+B)*C+D)*E", globalScope).intValue();
        int resultFromJava = ((a.getSimulatorVariable() + b.getSimulatorVariable()) * c.getSimulatorVariable() + d.getSimulatorVariable()) * e.getSimulatorVariable();

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_basicBooleanExpression() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");

        // variable assignment
        a = assignTestVariable(a, 1);
        b = assignTestVariable(b, 0);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("A||B", globalScope).intValue() % 2;
        int resultFromJava = (a.getBooleanSimulatorVariable() || b.getBooleanSimulatorVariable()) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_basicBooleanExpressionInBraces() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");

        // variable assignment
        a = assignTestVariable(a, 1);
        b = assignTestVariable(b, 0);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("(A||B)", globalScope).intValue() % 2;
        int resultFromJava = ((a.getBooleanSimulatorVariable() || b.getBooleanSimulatorVariable())) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_basicBooleanExpressionWithInlinedValue() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");

        // variable assignment
        a = assignTestVariable(a, 0);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("A||true", globalScope).intValue() % 2;
        int resultFromJava = 1;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_booleanExpressionWithPrecedence() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");
        TestVariable c = createTestVariable(globalScope, "C");

        // variable assignment
        a = assignTestVariable(a, 1);
        b = assignTestVariable(b, 0);
        c = assignTestVariable(c, 1);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("A||B&&C", globalScope).intValue() % 2;
        int resultFromJava = (a.getBooleanSimulatorVariable() || b.getBooleanSimulatorVariable() && c.getBooleanSimulatorVariable()) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_booleanExpressionWithBraces() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");
        TestVariable c = createTestVariable(globalScope, "C");

        // variable assignment
        a = assignTestVariable(a, 1);
        b = assignTestVariable(b, 0);
        c = assignTestVariable(c, 1);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("(A||B)&&C", globalScope).intValue() % 2;
        int resultFromJava = ((a.getBooleanSimulatorVariable() || b.getBooleanSimulatorVariable()) && c.getBooleanSimulatorVariable()) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_booleanExpressionWithDoubleBraces() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");
        TestVariable c = createTestVariable(globalScope, "C");
        TestVariable d = createTestVariable(globalScope, "D");
        TestVariable e = createTestVariable(globalScope, "E");

        // variable assignment
        a = assignTestVariable(a, 1);
        b = assignTestVariable(b, 0);
        c = assignTestVariable(c, 1);
        d = assignTestVariable(d, 1);
        e = assignTestVariable(e, 1);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("((A||B)&&C||D)&&E", globalScope).intValue() % 2;
        int resultFromJava = (((a.getBooleanSimulatorVariable() || b.getBooleanSimulatorVariable()) && c.getBooleanSimulatorVariable() || d.getBooleanSimulatorVariable()) && e.getBooleanSimulatorVariable()) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_booleanNotExpression() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");

        // variable assignment
        a = assignTestVariable(a, 1);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("!A", globalScope).intValue() % 2;
        int resultFromJava = (!a.getBooleanSimulatorVariable()) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_booleanNotExpressionInBraces() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");

        // variable assignment
        a = assignTestVariable(a, 1);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("(!A)", globalScope).intValue() % 2;
        int resultFromJava = ((!a.getBooleanSimulatorVariable())) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_booleanNestedNotExpression() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");

        // variable assignment
        a = assignTestVariable(a, 1);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("!(!A)", globalScope).intValue() % 2;
        int resultFromJava = (a.getBooleanSimulatorVariable()) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_booleanExpressionContainingNot() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");

        // variable assignment
        a = assignTestVariable(a, 1);
        b = assignTestVariable(b, 0);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("!A||B", globalScope).intValue() % 2;
        int resultFromJava = (!a.getBooleanSimulatorVariable() || b.getBooleanSimulatorVariable()) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_applyingNotToBooleanExpression() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");

        // variable assignment
        a = assignTestVariable(a, 0);
        b = assignTestVariable(b, 0);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("!(A||B)", globalScope).intValue() % 2;
        int resultFromJava = (!(a.getBooleanSimulatorVariable() || b.getBooleanSimulatorVariable())) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_basicHappyGreaterThanComparisonExpression() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");

        // variable assignment
        a = assignTestVariable(a, 10);
        b = assignTestVariable(b, 9);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("A>B", globalScope).intValue() % 2;
        int resultFromJava = (a.getSimulatorVariable() > b.getSimulatorVariable()) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_basicUnhappyGreaterThanComparisonExpression() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");

        // variable assignment
        a = assignTestVariable(a, 10);
        b = assignTestVariable(b, 10);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("A>B", globalScope).intValue() % 2;
        int resultFromJava = (a.getSimulatorVariable() > b.getSimulatorVariable()) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_basicHappyLessThanComparisonExpression() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");

        // variable assignment
        a = assignTestVariable(a, 9);
        b = assignTestVariable(b, 10);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("A<B", globalScope).intValue() % 2;
        int resultFromJava = (a.getSimulatorVariable() < b.getSimulatorVariable()) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_basicUnhappyLessThanComparisonExpression() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");

        // variable assignment
        a = assignTestVariable(a, 10);
        b = assignTestVariable(b, 10);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("A<B", globalScope).intValue() % 2;
        int resultFromJava = (a.getSimulatorVariable() < b.getSimulatorVariable()) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_basicHappyGreaterThanOrEqualComparisonExpression() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");

        // variable assignment
        a = assignTestVariable(a, 10);
        b = assignTestVariable(b, 10);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("A>=B", globalScope).intValue() % 2;
        int resultFromJava = (a.getSimulatorVariable() >= b.getSimulatorVariable()) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_basicUnhappyGreaterThanOrEqualComparisonExpression() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");

        // variable assignment
        a = assignTestVariable(a, 9);
        b = assignTestVariable(b, 10);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("A>=B", globalScope).intValue() % 2;
        int resultFromJava = (a.getSimulatorVariable() >= b.getSimulatorVariable()) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_basicHappyLessThanOrEqualComparisonExpression() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");

        // variable assignment
        a = assignTestVariable(a, 10);
        b = assignTestVariable(b, 10);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("A<=B", globalScope).intValue() % 2;
        int resultFromJava = (a.getSimulatorVariable() <= b.getSimulatorVariable()) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_basicUnhappyLessThanOrEqualComparisonExpression() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");

        // variable assignment
        a = assignTestVariable(a, 10);
        b = assignTestVariable(b, 9);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("A<=B", globalScope).intValue() % 2;
        int resultFromJava = (a.getSimulatorVariable() <= b.getSimulatorVariable()) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_basicHappyEqualComparisonExpression() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");

        // variable assignment
        a = assignTestVariable(a, 10);
        b = assignTestVariable(b, 10);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("A==B", globalScope).intValue() % 2;
        int resultFromJava = (a.getSimulatorVariable() == b.getSimulatorVariable()) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_basicUnhappyEqualComparisonExpression() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");

        // variable assignment
        a = assignTestVariable(a, 10);
        b = assignTestVariable(b, 9);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("A==B", globalScope).intValue() % 2;
        int resultFromJava = (a.getSimulatorVariable() == b.getSimulatorVariable()) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_basicUnhappyEqualComparisonExpressionCase2() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");

        // variable assignment
        a = assignTestVariable(a, 9);
        b = assignTestVariable(b, 10);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("A==B", globalScope).intValue() % 2;
        int resultFromJava = (a.getSimulatorVariable() == b.getSimulatorVariable()) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_basicGreaterThanComparisonExpressionInBraces() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");

        // variable assignment
        a = assignTestVariable(a, 10);
        b = assignTestVariable(b, 9);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("(A>B)", globalScope).intValue() % 2;
        int resultFromJava = ((a.getSimulatorVariable() > b.getSimulatorVariable())) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_basicGreaterThanComparisonExpressionWithUnbalancedBraces() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");

        // variable assignment
        a = assignTestVariable(a, 10);
        b = assignTestVariable(b, 9);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("(A)>B", globalScope).intValue() % 2;
        int resultFromJava = (((a.getSimulatorVariable()) > b.getSimulatorVariable())) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_comparisonExpressionWithRHSArithmeticExpressionContent() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");
        TestVariable c = createTestVariable(globalScope, "C");

        // variable assignment
        a = assignTestVariable(a, 9);
        b = assignTestVariable(b, 4);
        c = assignTestVariable(c, 6);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("A<B+C", globalScope).intValue() % 2;
        int resultFromJava = (a.getSimulatorVariable() < b.getSimulatorVariable() + c.getSimulatorVariable()) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_comparisonExpressionWithLHSArithmeticExpressionContent() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");
        TestVariable c = createTestVariable(globalScope, "C");

        // variable assignment
        a = assignTestVariable(a, 4);
        b = assignTestVariable(b, 6);
        c = assignTestVariable(c, 10);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("A+B<C", globalScope).intValue() % 2;
        int resultFromJava = (a.getSimulatorVariable() + b.getSimulatorVariable() < c.getSimulatorVariable()) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_comparisonExpressionWithBothSidesArithmeticExpressionContent() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");
        TestVariable c = createTestVariable(globalScope, "C");
        TestVariable d = createTestVariable(globalScope, "D");

        // variable assignment
        a = assignTestVariable(a, 4);
        b = assignTestVariable(b, 6);
        c = assignTestVariable(c, 10);
        d = assignTestVariable(d, 1);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("A+B<C+D", globalScope).intValue() % 2;
        int resultFromJava = (a.getSimulatorVariable() + b.getSimulatorVariable() < c.getSimulatorVariable() + d.getSimulatorVariable()) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_booleanExpressionWithComparisonExpressionContent() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");
        TestVariable c = createTestVariable(globalScope, "C");

        // variable assignment
        a = assignTestVariable(a, 4);
        b = assignTestVariable(b, 6);
        c = assignTestVariable(c, 0);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("A<B||C", globalScope).intValue() % 2;
        int resultFromJava = (a.getSimulatorVariable() < b.getSimulatorVariable() || c.getBooleanSimulatorVariable()) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_booleanExpressionWithComparisonExpressionContentAndBraces() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");
        TestVariable c = createTestVariable(globalScope, "C");

        // variable assignment
        a = assignTestVariable(a, 4);
        b = assignTestVariable(b, 6);
        c = assignTestVariable(c, 0);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("(A<B||C)&&true", globalScope).intValue() % 2;
        int resultFromJava = ((a.getSimulatorVariable() < b.getSimulatorVariable() || c.getBooleanSimulatorVariable())) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_booleanExpressionWithTwoComparisonExpressionContent() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");
        TestVariable c = createTestVariable(globalScope, "C");
        TestVariable d = createTestVariable(globalScope, "D");

        // variable assignment
        a = assignTestVariable(a, 4);
        b = assignTestVariable(b, 6);
        c = assignTestVariable(c, 8);
        d = assignTestVariable(d, 0);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("A<B||C>D", globalScope).intValue() % 2;
        int resultFromJava = (a.getSimulatorVariable() < b.getSimulatorVariable() || c.getSimulatorVariable() > d.getSimulatorVariable()) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_booleanExpressionWithTwoIdenticalComparisonExpressionContent() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");

        // variable assignment
        a = assignTestVariable(a, 4);
        b = assignTestVariable(b, 6);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("A<B||A<B", globalScope).intValue() % 2;
        int resultFromJava = (a.getSimulatorVariable() < b.getSimulatorVariable() || a.getSimulatorVariable() > b.getSimulatorVariable()) ? 1 : 0;

        assertEquals(result, resultFromJava);
    }

    @Test
    @DirtiesContext
    void test_complexMixedExpression1() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");
        TestVariable c = createTestVariable(globalScope, "C");
        TestVariable d = createTestVariable(globalScope, "D");

        // variable assignment
        a = assignTestVariable(a, -10);
        b = assignTestVariable(b, 20);
        c = assignTestVariable(c, 100);
        d = assignTestVariable(d, 8);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("((C/2-B)*2-(A-B))/2>=10*D+A&&!(D>B)", globalScope).intValue() % 2;
        int resultFromJava = (((c.getSimulatorVariable() / 2 - b.getSimulatorVariable()) * 2 - (a.getSimulatorVariable() - b.getSimulatorVariable())) / 2 >= 10 * d.getSimulatorVariable() + a.getSimulatorVariable() && !(d.getSimulatorVariable() > b.getSimulatorVariable())) ? 1 : 0;

        assertEquals(result, resultFromJava);

    }

    @Test
    @DirtiesContext
    void test_complexMixedExpression2() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");
        TestVariable c = createTestVariable(globalScope, "C");
        TestVariable d = createTestVariable(globalScope, "D");
        TestVariable e = createTestVariable(globalScope, "E");

        // variable assignment
        a = assignTestVariable(a, 4);
        b = assignTestVariable(b, -20);
        c = assignTestVariable(c, 80);
        d = assignTestVariable(d, 2);
        e = assignTestVariable(e, 1);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator("(2-(A-B)*(C/2-B)+7)/2<=10*D+D-(5+C)+A&&(!(D>B)||!E)", globalScope).intValue() % 2;
        int resultFromJava = ((2 - (a.getSimulatorVariable() - b.getSimulatorVariable()) * (c.getSimulatorVariable() / 2 - b.getSimulatorVariable()) + 7) / 2 <= 10 * d.getSimulatorVariable() + d.getSimulatorVariable() - (5 + c.getSimulatorVariable()) + a.getSimulatorVariable() && (!(d.getSimulatorVariable() > b.getSimulatorVariable()) || !e.getBooleanSimulatorVariable())) ? 1 : 0;

        assertEquals(result, resultFromJava);

    }

    @Test
    @DirtiesContext
    void test_expressionWithMessedUpSpaceCharacters() {
        // set up scope
        VariableScope globalScope = setUpSimulator();

        // variable declaration
        TestVariable a = createTestVariable(globalScope, "A");
        TestVariable b = createTestVariable(globalScope, "B");
        TestVariable d = createTestVariable(globalScope, "D");

        // variable assignment
        a = assignTestVariable(a, -10);
        b = assignTestVariable(b, 20);
        d = assignTestVariable(d, 8);

        // expression evaluation
        int result = compileWithExpressionAndRunSimulator(" (2 ) / 2>= 10 *D+A &&!( D >B )", globalScope).intValue() % 2;
        int resultFromJava = (1 >= 10 * d.getSimulatorVariable() + a.getSimulatorVariable() && !(d.getSimulatorVariable() > b.getSimulatorVariable())) ? 1 : 0;

        assertEquals(result, resultFromJava);

    }

}