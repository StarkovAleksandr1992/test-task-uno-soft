package ru.starkov.service;

import ru.starkov.model.DataType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DynamicTypeDetector {

    public static DataType determineDataType(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int linesToCheck = 10;
            int linesChecked = 0;

            while ((line = reader.readLine()) != null && linesChecked < linesToCheck) {
                String[] values = line.split(";");
                for (String value : values) {
                    String cleanedValue = value.replaceAll("\"", "");
                    if (cleanedValue.isEmpty()) {
                        continue;
                    }
                    if (isDouble(cleanedValue)) {
                        return DataType.DOUBLE;
                    } else if (isLong(cleanedValue)) {
                        return DataType.LONG;
                    } else {
                        return DataType.STRING;
                    }
                }
                linesChecked++;
            }
        }
        return DataType.STRING;
    }

    private static boolean isLong(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return value.contains(".") || value.toLowerCase().contains("e");
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
