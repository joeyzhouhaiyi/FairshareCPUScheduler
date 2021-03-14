package com.company;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

// singleton class Outputlogger, logging to file output.txt
public class OutputLogger {

    private static OutputLogger instance;

    // making it synchronized so that only one thread can access it at a time and create the instance
    public synchronized static OutputLogger getInstance() {
        if(instance == null) {
            try {
                instance = new OutputLogger();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    static BufferedWriter writer = null;

    private OutputLogger() throws IOException {
        File outputFile = new File("output.txt");
        outputFile.createNewFile();
        writer = new BufferedWriter(new FileWriter(outputFile.getName()));
    }

    // write a line of the given string to the file
    public synchronized void writeLine(String txt) {
        try {
            writer.write(txt);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // close BufferedWriter
    public void close() throws IOException {
        if(writer != null)
            writer.close();
    }


}
