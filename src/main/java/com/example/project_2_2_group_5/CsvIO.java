package com.example.project_2_2_group_5;

import java.io.*;

public class CsvIO {

    //Method to read string from csv file
    public static String readFile(String filePath){
        StringBuilder text = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Error reading file");
        }
        return text.toString();
    }

    //Method to write a string to a csv file
    public static void writeToFile(String content, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        } catch (IOException e) {
            System.out.println("Error writing to file");
        }
    }

    //Method to clear a csv file

    public static void clearFile(String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath, false)) {
            fileWriter.write("");
        } catch (IOException e) {
            System.err.println("Error clearing the CSV file: " + e.getMessage());
        }
    }

    //Method to check if file exists
    public static boolean isFileEmpty(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return true;
        }
        return file.length() == 0;
    }
}
