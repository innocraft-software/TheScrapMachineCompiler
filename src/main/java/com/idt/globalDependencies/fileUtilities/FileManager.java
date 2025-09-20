package com.idt.globalDependencies.fileUtilities;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileManager {
    public static void createFile(String absolutePath, String data) throws IOException {
        FileOutputStream outputFile = new FileOutputStream(absolutePath);
        DataOutputStream output = new DataOutputStream(outputFile);
        output.writeBytes(data);
        outputFile.close();
    }
}
