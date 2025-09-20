package com.idt.globalDependencies.assemblyProcessor;

public class AssemblyTranslator {
    private final int instruction;
    private static final String GPIORESET = "RSET";
    private static final String[] INSTRUCTION_NAME_LOOKUP = {
            "MOV ",
            "MOVT",
            "ADD ",
            "SUB ",

            "CMP ",
            "LD  ",
            "STR ",
            "",

            "TRM ",
            "BLE ",
            "GPIOSET ",
            "GPIO",

            "SHF",
            "    ",
            "BEQ ",
            "    "
    };

    private final String[] REGISTER_LOOKUP = {
            "R0",
            "R1",
            "R2",
            "R3"
    };
    private static final String[] LOGIC_LOOKUP = {
            "AND ",
            "OR  ",
            "XOR ",
            "NOT "
    };

    private String translateFlags(int operation) {

        if (operation == 7) {

            // logic instruction
            int logicOperation = (this.instruction & 0b0000000000110000) >>> 4;
            return LOGIC_LOOKUP[logicOperation];

        } else if (operation == 11) {

            // READ instruction
            int readFlag = (this.instruction & 0b0000000000000100) >>> 2;
            if (readFlag == 1) {
                return "READ";
            } else {
                return GPIORESET;
            }

        } else if (operation == 12) {
            int shiftOperation = (this.instruction & 0b0000001000000000) >>> 9;
            String shiftDir = "U";
            if (shiftOperation == 1) {
                shiftDir = "D";
            }
            return shiftDir;
        }
        return "";
    }

    public AssemblyTranslator(int instruction) {
        this.instruction = instruction;
    }

    private static String intToHex(int length, int integer) {
        String hexString = Integer.toHexString(integer);
        while (hexString.length() < length) {
            hexString = "0" + hexString;
        }
        hexString = "0x" + hexString.toUpperCase();
        return hexString;
    }

    public static String getHexInstructionInformation(int instruction, int instructionAddress) {
        String address = intToHex(4, instructionAddress);
        String instructionHex = intToHex(4, instruction);
        return String.format("%s     %s", address, instructionHex);

    }

    private int shiftMaskedData(int rawAddress, int[] mask, int operation) {
        int processAddress = rawAddress;
        processAddress >>>= computeMaskShift(mask, operation);
        return processAddress;
    }

    public static int computeMaskShift(int[] mask, int operation) {
        int shiftCounter = 0;
        int processAddress = mask[operation];
        for (int counter = 0; counter < 5; counter++) {
            if ((processAddress & 0b11) == 0) {
                processAddress >>>= 2;
                shiftCounter += 2;
            }
        }
        return shiftCounter;
    }

    private String readRegister(int operation, int[] registerMask) {
        String reg = REGISTER_LOOKUP[shiftMaskedData(this.instruction & registerMask[operation], registerMask, operation)] + " ";
        return removeZeroMask(operation, reg, registerMask);
    }

    private String removeZeroMask(int operation, String data, int[] REGISTER_MASK) {
        if (REGISTER_MASK[operation] == 0) {
            data = "";
        }
        return data;
    }

    private String readImmediate(int operation) {
        return removeZeroMask(operation, "#" + shiftMaskedData(this.instruction & InstructionMasks.IMMEDIATE_MASKS[operation], InstructionMasks.IMMEDIATE_MASKS, operation), InstructionMasks.IMMEDIATE_MASKS);
    }

    public String translate() {
        int operation = (this.instruction & InstructionMasks.INSTRUCTION_CODE_MASK) >>> 12;
        String source1 = readRegister(operation, InstructionMasks.SOURCE_1_MASK);
        String source2 = readRegister(operation, InstructionMasks.SOURCE_2_MASK);
        String destination = readRegister(operation, InstructionMasks.DEST_MASK);

        // convert to 2s complement
        String immediate = readImmediate(operation);
        String flagName = translateFlags(operation);
        if (flagName == LOGIC_LOOKUP[3]) {
            source2 = "";
        }
        if (flagName == GPIORESET) {
            destination = "";
        }
        String translatedBytes = String.format("%s%s %s%s%s%s", INSTRUCTION_NAME_LOOKUP[operation], flagName, source1, source2, destination, immediate);
        if (operation == 9 || operation == 14) {
            // if a program counter address is included, compute it as hex
            translatedBytes += " PC:" + intToHex(4, 2 * Integer.valueOf(immediate.replaceAll("#", "")));
        }
        return translatedBytes;
    }
}
