package com.idt.globalDependencies.jsonGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JSONGenerator {

    private static JSONGenerator shared;

    private JSONGenerator() {
    }

    public static JSONGenerator getInstance() {
        if (shared == null) {
            shared = new JSONGenerator();
        }
        return shared;
    }

    // ---------------------------------------

    private final int WRAP_LENGTH = 255;

    private String readTextFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private String enterValue(String text, String valueName, String value) {
        return text.replaceAll("\\{\\{" + valueName + "\\}\\}", value);
    }

    private int[] intToBitArray(int num) {
        int[] bits = new int[12];

        for (int i = 11; i >= 0; i--) {
            bits[i] = (num & 1);
            num >>= 1;
        }

        return bits;
    }

    public int getAggregatorBaseId(int heightOffset) {
        // start indexing aggregators at 4096 as this is the PC limit of the ScrapMachine
        return heightOffset * (16 + 12) + 4096 * 29;
    }

    public String insertAggregatorIds(int heightOffset, String processData) {
        int aggregatorIndex = getAggregatorBaseId(heightOffset);

        for (int bit = 1; bit <= 16 + 12; bit++) {
            processData = enterValue(processData, "AGGREGATOR_ID_" + bit, String.valueOf(aggregatorIndex + bit));
        }

        return processData;

    }

    public String generateAggregator(int heightOffset, int rowCount) {

        String processData = readTextFile("src/main/java/com/idt/globalDependencies/jsonGenerator/_layerAggregator.txt");

        boolean needsComma;
        // generate and add the references to the rows
        for (int selectorBit = 0; selectorBit < 12; selectorBit++) {
            needsComma = false;
            int activeBit = (11 - selectorBit);
            String outgoingConnections;
            if (rowCount > 0) {

                outgoingConnections = "[";
                for (int i = (heightOffset-3) * (WRAP_LENGTH); i < Math.min(rowCount, ((heightOffset-3) + 1) * (WRAP_LENGTH)); i++) {
                    if (needsComma) {
                        outgoingConnections += ",";
                    }
                    outgoingConnections += "{\"id\":" + (128 + selectorBit + 29 * i) + "}";
                    needsComma = true;
                }
                outgoingConnections += "]";
            } else {
                outgoingConnections = "[]";
            }
            processData = enterValue(processData, "BASE_CONNECTIONS_BIT" + activeBit, outgoingConnections);
        }

        processData = insertAggregatorIds(heightOffset, processData);
        processData = enterValue(processData, "AGGREGATOR_HEIGHT", String.valueOf(heightOffset));
        return processData;
    }


    public String generateRow(int rowPosition, int rowIndex, int rowData) {

        // create the base data
        String processData = readTextFile("src/main/java/com/idt/globalDependencies/jsonGenerator/_romRow.txt");

        // row height
        int rowHeight = rowPosition / WRAP_LENGTH + 3;


        // wrapped row position
        processData = enterValue(processData, "ROW_POSITION", String.valueOf(rowPosition % WRAP_LENGTH + 15));
        processData = enterValue(processData, "ROW_HEIGHT", String.valueOf(rowHeight));
        int[] indexBinary = intToBitArray(rowIndex);
        int rowSelectorID = 140 + 29 * rowPosition;

        // create the selector data
        for (int selectorBit = 0; selectorBit < 12; selectorBit++) {
            int activeBit = (11 - selectorBit);
            int logicGateID = 128 + selectorBit + 29 * rowPosition;

            String mode = "4";
            if (indexBinary[selectorBit] == 1) {
                mode = "0";
            }

            processData = enterValue(processData, "ID_BIT" + activeBit, String.valueOf(logicGateID));
            processData = enterValue(processData, "MODE_BIT" + activeBit, mode);
        }

        // create the binary data
        String dataConnections = "[";
        boolean needsComma = false;
        for (int currentBit = 0; currentBit < 16; currentBit++) {

            int currentBitIndex = (141 + (15 - currentBit) + 29 * rowPosition);
            processData = enterValue(processData, "ID_CELL" + currentBit, String.valueOf(currentBitIndex));
            int currentBitMask = 1 << currentBit;

            // connecct to layer selector if bit active
            if (((rowData & currentBitMask) >>> currentBit) == 1) {
                if (needsComma) {
                    dataConnections += ",";
                }
                dataConnections += "{\"id\":" + currentBitIndex + "}";
                needsComma = true;
            }


        }
        dataConnections += "]";
        if (dataConnections.length() < 3) {
            dataConnections = "null";
        }

        processData = enterValue(processData, "DATA_CONNECTIONS", dataConnections);

        // enter the row selector ID
        processData = enterValue(processData, "ID_ROW_SELECTOR", String.valueOf(rowSelectorID));

        // link the aggregator
        processData = insertAggregatorIds(rowHeight, processData);

        return processData;
    }

    public String generateRomBlueprint(int rowCount, String rowData) {
        // create the base connection block with references to rows
        String dataOutput = readTextFile("src/main/java/com/idt/globalDependencies/jsonGenerator/_baseConnections.txt");
        dataOutput = enterValue(dataOutput, "BASE_LENGTH", String.valueOf(Math.min(rowCount + 3, WRAP_LENGTH + 3))); // cap the base because wrapping

        // aggregators
        boolean needsComma = false;
        String aggregators = "";
        int aggregatorCount = ((rowCount - 1) / WRAP_LENGTH) + 1;
        for (int i = 0; i<aggregatorCount; i++) {
            if (needsComma) {
                aggregators += ",\n";
            }
            aggregators += generateAggregator(i+3, rowCount);
            needsComma = true;
        }

        dataOutput = enterValue(dataOutput, "LAYER_AGGREGATORS", aggregators);

        // generate and add the references to the aggregators
        for (int selectorBit = 0; selectorBit < 12; selectorBit++) {
            needsComma = false;
            int activeBit = (11 - selectorBit);
            String outgoingConnections;
            if (rowCount > 0) {

                outgoingConnections = "[";
                for (int i = 0; i < rowCount; i++) {
                    if (i % WRAP_LENGTH == 0) {
                        if (needsComma) {
                            outgoingConnections += ",";
                        }
                        outgoingConnections += "{\"id\":" + (getAggregatorBaseId(i/WRAP_LENGTH + 3) + selectorBit + 16 + 1) + "}";
                        needsComma = true;
                    }
                }
                outgoingConnections += "]";
            } else {
                outgoingConnections = "null";
            }
            dataOutput = enterValue(dataOutput, "BASE_CONNECTIONS_BIT" + activeBit, outgoingConnections);
        }


        // add the rows
        dataOutput += rowData;


        // generate the blueprint
        String blueprint = readTextFile("src/main/java/com/idt/globalDependencies/jsonGenerator/_blueprintTemplate.txt");
        blueprint = enterValue(blueprint, "CONTENT", dataOutput);
        return blueprint;

    }
}
