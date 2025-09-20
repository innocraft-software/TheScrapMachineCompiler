package com.idt;

import com.idt.compiler.assembly.application.port.in.ManageApplicationUseCase;
import com.idt.compiler.expressionEvaluation.application.port.in.ExpressionEvaluationUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.InstructionGenerationUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.ScopeManagementUseCase;
import com.idt.compiler.processorAbstraction.application.port.in.VariableManagementUseCase;
import com.idt.simulator.Simulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ScrapMachineCompilerApplicationTest {
    // system tests for the application


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

    public static Set<Short> readResultRegisterFromFile(String filePath) {
        Set<Short> shortSet = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    shortSet.add(Short.parseShort(line));
                } catch (NumberFormatException e) {
                    System.err.println("Skipping invalid short value: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred while reading the file: " + e.getMessage());
        }
        return shortSet;
    }

    @BeforeEach
    void setSimSpeed() {
        Simulator.sleepTime = 0;
    }


    @Test
    @DirtiesContext
    void test_compileEmptyScript() {
        String testScript = "empty";

        ScrapMachineCompilerApplication.compileScript(String.format("src/test/java/com/idt/testScripts/%s.smscript", testScript), testPath);

        // run simulator
        System.setProperty("java.awt.headless", "false");
        Simulator.simulate("src/test/processAssemblyLocation/binary.txt");

        // search result in output
        short expectedResult = 0;
        assertTrue(readResultRegisterFromFile("src/test/processAssemblyLocation/simOutputRegisters.txt").contains(expectedResult));
    }

    @Test
    @DirtiesContext
    void test_compileExponentScript() {
        String testScript = "exponent";

        ScrapMachineCompilerApplication.compileScript(String.format("src/test/java/com/idt/testScripts/%s.smscript", testScript), testPath);

        // run simulator
        System.setProperty("java.awt.headless", "false");
        Simulator.simulate("src/test/processAssemblyLocation/binary.txt");


        // ––––––––––––– JAVA RESULT –––––––––––––
        // declarations
        int base;
        int exponent;
        int result = 0;

        // input
        base = 8;
        exponent = 2;


        // exception case exponent = 0
        if (exponent == 0) {
            result = 1;
        }

        // calculation for all other exponents
        if (exponent > 0) {
            // initialize result
            result = base;

            // initialize counter
            int ctr;
            ctr = 1;

            // calculate loop
            while (ctr < exponent) {
                result = result * base;
                ctr = ctr + 1;
            }
        }

        // stupid way of loading the result to register so I can see it
        result = result + 1;
        // ––––––––––– END JAVA RESULT –––––––––––


        // search result in output
        short expectedResult = (short) result;
        assertTrue(readResultRegisterFromFile("src/test/processAssemblyLocation/simOutputRegisters.txt").contains(expectedResult));
    }

    @Test
    @DirtiesContext
    void test_compileExponentPowerZeroScript() {
        String testScript = "exponentPowerZero";

        ScrapMachineCompilerApplication.compileScript(String.format("src/test/java/com/idt/testScripts/%s.smscript", testScript), testPath);

        // run simulator
        System.setProperty("java.awt.headless", "false");
        Simulator.simulate("src/test/processAssemblyLocation/binary.txt");


        // ––––––––––––– JAVA RESULT –––––––––––––
        // declarations
        int base;
        int exponent;
        int result = 0;

        // input
        base = 8;
        exponent = 0;


        // exception case exponent = 0
        if (exponent == 0) {
            result = 1;
        }

        // calculation for all other exponents
        if (exponent > 0) {
            // initialize result
            result = base;

            // initialize counter
            int ctr;
            ctr = 1;

            // calculate loop
            while (ctr < exponent) {
                result = result * base;
                ctr = ctr + 1;
            }
        }

        // stupid way of loading the result to register so I can see it
        result = result + 1;
        // ––––––––––– END JAVA RESULT –––––––––––


        // search result in output
        short expectedResult = (short) result;
        assertTrue(readResultRegisterFromFile("src/test/processAssemblyLocation/simOutputRegisters.txt").contains(expectedResult));
    }


    @Test
    @DirtiesContext
    void test_compileSortNumbersScript() {
        String testScript = "arraySort";

        ScrapMachineCompilerApplication.compileScript(String.format("src/test/java/com/idt/testScripts/%s.smscript", testScript), testPath);

        // run simulator
        System.setProperty("java.awt.headless", "false");
        Simulator.simulate("src/test/processAssemblyLocation/binary.txt");


        // ––––––––––––– JAVA RESULT –––––––––––––
        // note in this example we do not care if we mess up the array
        // declare the array
        int a1;
        int a2;
        int a3;
        int a4;

        // populate the array
        a1 = 6;
        a2 = 4;
        a3 = 8;
        a4 = 2;

        // declare the output
        int o1 = 0;
        int o2 = 0;
        int o3 = 0;
        int o4 = 0;

        // find minimum
        int minimum;


        int loopIndex;
        loopIndex = 0;
        while (loopIndex < 4) {
            minimum = a1;

            // "iterate" the pseudo array and find minimum
            // we can not do for loops so we cave to copy paste
            if (minimum > a2) {
                minimum = a2;
            }

            if (minimum > a3) {
                minimum = a3;
            }

            if (minimum > a4) {
                minimum = a4;
            }
            // end for loop

            int knownToBeBigger;
            knownToBeBigger = 100;
            // "iterate" the pseudo array and eliminate the minimum
            // we can not do for loops so we cave to copy paste
            if (minimum == a1) {
                a1 = knownToBeBigger;
            }

            if (minimum == a2) {
                a2 = knownToBeBigger;
            }

            if (minimum == a3) {
                a3 = knownToBeBigger;
            }

            if (minimum == a4) {
                a4 = knownToBeBigger;
            }
            // end for loop


            // "iterate" the pseudo array and write minimum
            // we can not do for loops so we cave to copy paste
            if (loopIndex == 0) {
                o1 = minimum;
            }

            if (loopIndex == 1) {
                o2 = minimum;
            }

            if (loopIndex == 2) {
                o3 = minimum;
            }

            if (loopIndex == 3) {
                o4 = minimum;
            }
            // end for loop

            // increment
            loopIndex = loopIndex + 1;
        }

        int output;
        output = o1 + 10 * o2 + 100 * o3 + 1000 * o4;
        // ––––––––––– END JAVA RESULT –––––––––––


        // search result in output
        short expectedResult = (short) output;
        assertTrue(readResultRegisterFromFile("src/test/processAssemblyLocation/simOutputRegisters.txt").contains(expectedResult));
    }

    @Test
    @DirtiesContext
    void test_compileBasicArithmeticScript() {
        String testScript = "basicArithmetic";

        ScrapMachineCompilerApplication.compileScript(String.format("src/test/java/com/idt/testScripts/%s.smscript", testScript), testPath);

        // run simulator
        System.setProperty("java.awt.headless", "false");
        Simulator.simulate("src/test/processAssemblyLocation/binary.txt");


        // ––––––––––––– JAVA RESULT –––––––––––––
        // Test basic arithmetic operations
        int x;
        int y;
        int result;

        x = 5;
        y = 3;
        result = x + y;
        result = result - 2;
        result = result * 4;
        result = result / 2;
        // ––––––––––– END JAVA RESULT –––––––––––

        // stupid way of loading the result to register so I can see it
        result = result + 1;


        // search result in output
        short expectedResult = (short) result;
        assertTrue(readResultRegisterFromFile("src/test/processAssemblyLocation/simOutputRegisters.txt").contains(expectedResult));
    }

    @Test
    @DirtiesContext
    void test_compileBasicBooleanScript() {
        String testScript = "basicArithmetic";

        ScrapMachineCompilerApplication.compileScript(String.format("src/test/java/com/idt/testScripts/%s.smscript", testScript), testPath);

        // run simulator
        System.setProperty("java.awt.headless", "false");
        Simulator.simulate("src/test/processAssemblyLocation/binary.txt");


        // ––––––––––––– JAVA RESULT –––––––––––––
        // Test basic arithmetic operations
        // Test boolean operations
        boolean a;
        boolean b;
        boolean c;
        boolean result;

        a = true;
        b = false;
        c = true;

        result = a && b;
        result = a || b;
        result = !c;
        result = (a && b) || (!c);

        // stupid way of loading the result to register so I can see it
        result = !result;

        // ––––––––––– END JAVA RESULT –––––––––––

        // convert result to int
        short resultS = 0;
        if (result) {
            resultS = 1;
        }


        // search result in output
        assertTrue(readResultRegisterFromFile("src/test/processAssemblyLocation/simOutputRegisters.txt").contains(resultS));
    }

    @Test
    @DirtiesContext
    void test_compileNestedIfScript() {
        String testScript = "nestedIfs";

        ScrapMachineCompilerApplication.compileScript(String.format("src/test/java/com/idt/testScripts/%s.smscript", testScript), testPath);

        // run simulator
        System.setProperty("java.awt.headless", "false");
        Simulator.simulate("src/test/processAssemblyLocation/binary.txt");


        // ––––––––––––– JAVA RESULT –––––––––––––
        // Test nested if statements
        int x;
        int y;
        int z;

        x = 10;
        y = 20;
        z = 0;

        if (x < y) {
            z = z + 1;
            if (x + z > y) {
                z = z + 10;
            }
        }

        // stupid way of loading the result to register so I can see it
        z = z + 1;


        // ––––––––––– END JAVA RESULT –––––––––––


        // search result in output
        short expectedResult = (short) z;
        assertTrue(readResultRegisterFromFile("src/test/processAssemblyLocation/simOutputRegisters.txt").contains(expectedResult));
    }

    @Test
    @DirtiesContext
    void test_compileNestedWhileScript() {
        String testScript = "nestedWhile";

        ScrapMachineCompilerApplication.compileScript(String.format("src/test/java/com/idt/testScripts/%s.smscript", testScript), testPath);

        // run simulator
        System.setProperty("java.awt.headless", "false");
        Simulator.simulate("src/test/processAssemblyLocation/binary.txt");


        // ––––––––––––– JAVA RESULT –––––––––––––
        // Test nested while loops
        int outer;
        int inner;
        int result;

        outer = 0;
        inner = 0;
        result = 0;

        while (outer < 3) {
            inner = 0;
            while (inner < 2) {
                result = result + (outer * inner);
                inner = inner + 1;
            }
            outer = outer + 1;
        }

        // stupid way of loading the result to register so I can see it
        result = result + 1;
        // ––––––––––– END JAVA RESULT –––––––––––


        // search result in output
        short expectedResult = (short) result;
        assertTrue(readResultRegisterFromFile("src/test/processAssemblyLocation/simOutputRegisters.txt").contains(expectedResult));
    }

    @Test
    @DirtiesContext
    void test_compileArithmeticBooleanControlScript() {
        String testScript = "mixedArithmeticBooleanControl";

        ScrapMachineCompilerApplication.compileScript(String.format("src/test/java/com/idt/testScripts/%s.smscript", testScript), testPath);

        // run simulator
        System.setProperty("java.awt.headless", "false");
        Simulator.simulate("src/test/processAssemblyLocation/binary.txt");


        // ––––––––––––– JAVA RESULT –––––––––––––
        // Test arithmetic combined with boolean logic
        int a;
        int b;
        int result = 0;

        a = 7;
        b = 3;

        if ((a > b) && (b + 2 == 5)) {
            result = a * b;
        }

        // stupid way of loading the result to register so I can see it
        result = result + 1;
        // ––––––––––– END JAVA RESULT –––––––––––


        // search result in output
        short expectedResult = (short) result;
        assertTrue(readResultRegisterFromFile("src/test/processAssemblyLocation/simOutputRegisters.txt").contains(expectedResult));
    }

    @Test
    @DirtiesContext
    void test_compileEdgeCase1Script() {
        String testScript = "edgeCase1";

        ScrapMachineCompilerApplication.compileScript(String.format("src/test/java/com/idt/testScripts/%s.smscript", testScript), testPath);

        // run simulator
        System.setProperty("java.awt.headless", "false");
        Simulator.simulate("src/test/processAssemblyLocation/binary.txt");


        // ––––––––––––– JAVA RESULT –––––––––––––
        // Test negative numbers and zero
        int x;
        int y;
        int z;

        x = -5;
        y = 0;
        z = x + y;

        if (z < 0) {
            z = z * (-1);
        }

        // stupid way of loading the result to register so I can see it
        z = z + 1;
        // ––––––––––– END JAVA RESULT –––––––––––


        // search result in output
        short expectedResult = (short) z;
        assertTrue(readResultRegisterFromFile("src/test/processAssemblyLocation/simOutputRegisters.txt").contains(expectedResult));
    }

    @Test
    @DirtiesContext
    void test_compileComplexWhileLoopScript() {
        String testScript = "complexWhileLoopCondition";

        ScrapMachineCompilerApplication.compileScript(String.format("src/test/java/com/idt/testScripts/%s.smscript", testScript), testPath);

        // run simulator
        System.setProperty("java.awt.headless", "false");
        Simulator.simulate("src/test/processAssemblyLocation/binary.txt");


        // ––––––––––––– JAVA RESULT –––––––––––––
        // Test complex conditions in while loop
        int x;
        int y;

        x = 0;
        y = 10;

        while ((x < y) && (y > 5)) {
            x = x + 1;
            y = y - 1;
        }

        // make the result readable
        int result;
        result = x + y;
        // ––––––––––– END JAVA RESULT –––––––––––


        // search result in output
        short expectedResult = (short) result;
        assertTrue(readResultRegisterFromFile("src/test/processAssemblyLocation/simOutputRegisters.txt").contains(expectedResult));
    }

    @Test
    @DirtiesContext
    void test_compileFactorialScript() {
        String testScript = "factorial";

        ScrapMachineCompilerApplication.compileScript(String.format("src/test/java/com/idt/testScripts/%s.smscript", testScript), testPath);

        // run simulator
        System.setProperty("java.awt.headless", "false");
        Simulator.simulate("src/test/processAssemblyLocation/binary.txt");


        // ––––––––––––– JAVA RESULT –––––––––––––
        // Calculate factorial of a number
        int n;
        int factorial;

        n = 5;
        factorial = 1;

        while (n > 0) {
            factorial = factorial * n;
            n = n - 1;
        }

        // stupid way to move the result to register
        factorial = factorial + 1;
        // ––––––––––– END JAVA RESULT –––––––––––


        // search result in output
        short expectedResult = (short) factorial;
        assertTrue(readResultRegisterFromFile("src/test/processAssemblyLocation/simOutputRegisters.txt").contains(expectedResult));
    }

    @Test
    @DirtiesContext
    void test_compileFibonacciScript() {
        String testScript = "fibonacci";

        ScrapMachineCompilerApplication.compileScript(String.format("src/test/java/com/idt/testScripts/%s.smscript", testScript), testPath);

        // run simulator
        System.setProperty("java.awt.headless", "false");
        Simulator.simulate("src/test/processAssemblyLocation/binary.txt");


        // ––––––––––––– JAVA RESULT –––––––––––––
        // Compute the nth Fibonacci number
        int n;
        int a;
        int b;
        int temp;
        int count;

        // Find the 7th Fibonacci number
        n = 7;
        a = 0;
        b = 1;
        count = 2;

        while (count <= n) {
            temp = b;
            b = a + b;
            a = temp;
            count = count + 1;
        }
        // Result will be in 'b'
        b = b + 1;
        // ––––––––––– END JAVA RESULT –––––––––––


        // search result in output
        short expectedResult = (short) b;
        assertTrue(readResultRegisterFromFile("src/test/processAssemblyLocation/simOutputRegisters.txt").contains(expectedResult));
    }

    @Test
    @DirtiesContext
    void test_compileSumOfDigitsScript() {
        String testScript = "sumOfDigits";

        ScrapMachineCompilerApplication.compileScript(String.format("src/test/java/com/idt/testScripts/%s.smscript", testScript), testPath);

        // run simulator
        System.setProperty("java.awt.headless", "false");
        Simulator.simulate("src/test/processAssemblyLocation/binary.txt");


        // ––––––––––––– JAVA RESULT –––––––––––––
        // Sum all digits of a number
        int n;
        int digit;
        int sum;

        n = 12345;
        sum = 0;

        while (n > 0) {
            // modulo
            digit = n - (n / 10) * 10;
            sum = sum + digit;
            n = n / 10;
        }

        // load stupid way
        sum = sum + 1;
        // ––––––––––– END JAVA RESULT –––––––––––


        // search result in output
        short expectedResult = (short) sum;
        assertTrue(readResultRegisterFromFile("src/test/processAssemblyLocation/simOutputRegisters.txt").contains(expectedResult));
    }


}