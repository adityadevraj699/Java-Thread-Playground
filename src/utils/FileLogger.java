package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import main.Metrics;

public class FileLogger {
    private static final String OUTPUT_DIR = "C:/Users/Dell/Desktop/Eclipse project/JavaThreadPlayground/src/output";
    private static final String FILE_PATH = OUTPUT_DIR + "/results.txt";

    public static void log(Metrics result) {
        try {
            // Ensure the directory exists
            File directory = new File(OUTPUT_DIR);
            if (!directory.exists()) {
                directory.mkdirs(); // create directories if they don't exist
            }

            // Write or append to the results.txt file
            try (PrintWriter out = new PrintWriter(new FileWriter(FILE_PATH, true))) {
                out.println(result.format());
                out.println("-------------------------------");
            }

        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
