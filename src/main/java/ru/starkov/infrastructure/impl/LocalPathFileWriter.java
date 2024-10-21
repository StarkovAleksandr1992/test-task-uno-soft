package ru.starkov.infrastructure.impl;

import ru.starkov.infrastructure.PathFileWriter;
import ru.starkov.model.DataType;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Universal implementation of the PathFileWriter interface for writing data of any type to a file.
 * The groups are sorted and their contents are formatted as strings.
 * <p>
 * If the output path is not specified, the file will be created in the current directory.
 */
public class LocalPathFileWriter<T> implements PathFileWriter<List<List<List<T>>>> {

    private static final String DEFAULT_OUTPUT_FILE = "output.txt";

    /**
     * Writes a list of grouped data to a file.
     * <p>
     * If the output path is not specified, the file will be created in the current directory.
     *
     * @param groups     the list of groups to write to the file
     * @param outputPath the path to the output file; if null or empty, the file will be created in the current directory
     * @throws IOException if an I/O error occurs during file writing
     */
    @Override
    public void writeToTxtFile(List<List<List<T>>> groups, String outputPath, Duration executionTime, DataType dataType) throws IOException {
        String filePath = Optional.ofNullable(outputPath)
                .filter(path -> !path.isEmpty())
                .orElse(DEFAULT_OUTPUT_FILE);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            int groupCounter = 1;

            long groupsWithTwoOrMoreElements = groups.stream()
                    .filter(group -> group.size() > 1)
                    .count();

            writer.write(String.format("Number of groups with more than 1 element: %d%n", groupsWithTwoOrMoreElements));
            writer.write(String.format("Execution time (excluding file writing): %d seconds%n",
                    executionTime.get(ChronoUnit.SECONDS)));
            writer.write(System.lineSeparator());

            for (List<List<T>> group : groups) {
                writeGroup(writer, group, groupCounter++, dataType);
            }
        } catch (IOException e) {
            System.err.printf("Error writing to file: %s", filePath);
            throw e;
        }
    }

    private void writeGroup(BufferedWriter writer, List<List<T>> group, int groupCounter, DataType dataType) throws IOException {
        writer.write(String.format("Group %d:%n", groupCounter));
        for (List<T> line : group) {
            writer.write(formatLine(line, dataType) + System.lineSeparator());
        }
        writer.write(System.lineSeparator());  // Add an empty line between groups for readability
    }

    private String formatLine(List<T> line, DataType dataType) {
        switch (dataType) {
            case LONG -> {
                return line.stream()
                        .map(obj -> (Long) obj)
                        .map(Objects::toString)
                        .map(string -> {
                            if (string.equals("0")) {
                                return "\"           \"";
                            }
                            return String.format("\"%s\"", string);
                        }).collect(Collectors.joining(";"));
            }
            case DOUBLE -> {
                return line.stream()
                        .map(obj -> (Double) obj)
                        .map(Objects::toString)
                        .map(string -> {
                            if (string.equals("0")) {
                                return "\"\"";
                            }
                            return String.format("\"%s\"", string);
                        }).collect(Collectors.joining(";"));
            }
            default -> {
                return line.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(";"));
            }
        }
    }
}
