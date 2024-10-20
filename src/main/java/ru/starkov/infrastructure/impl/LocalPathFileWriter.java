package ru.starkov.infrastructure.impl;

import ru.starkov.infrastructure.PathFileWriter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the PathFileWriter interface for writing a Map of groups
 * to a file, where each group is sorted by size and its contents are formatted with quotes.
 * <p>
 * If the output path is not specified, the file will be created in the current directory.
 */
public class LocalPathFileWriter implements PathFileWriter<Map<Integer, List<List<Long>>>> {

    private static final String FORMATTED_EMPTY_STRING = "\"           \"";
    private static final String FORMATTED_VALUE_STRING = "\"%d\"";
    private static final String DEFAULT_OUTPUT_FILE = "output.txt";

    /**
     * Writes a Map of integer keys and Set of List of Longs to a file.
     * The groups are sorted by size in descending order, and each list is
     * formatted by wrapping its elements in quotes.
     * <p>
     * If the output path is not specified, the file will be created in the current directory.
     *
     * @param groups     the map of groups to write to the file
     * @param outputPath the path to the output file; if null or empty, the file will be created in the current directory
     * @throws IOException if an I/O error occurs during file writing
     */
    @Override
    public void writeToTxtFile(Map<Integer, List<List<Long>>> groups, String outputPath, Duration executionTime) throws IOException {
        String filePath = Optional.ofNullable(outputPath)
                .filter(path -> !path.isEmpty())
                .orElse(DEFAULT_OUTPUT_FILE);

        List<List<List<Long>>> sortedGroups = groups.values().stream()
                .sorted((l1, l2) -> Integer.compare(l2.size(), l1.size()))
                .toList();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            int groupCounter = 1;
            long groupsWithTwoOrMoreElements = groups.values().stream()
                    .filter(longs -> longs.size() > 1)
                    .count();

            writer.write(String.format("Number of groups with more than 1 element: %d%n", groupsWithTwoOrMoreElements));
            writer.write(String.format("Execution time (excluding file writing): %d seconds%n",
                    executionTime.get(ChronoUnit.SECONDS)));

            for (List<List<Long>> group : sortedGroups) {
                writeGroup(writer, group, groupCounter++);
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + filePath);
            throw e;
        }
    }

    /**
     * Writes a single group to the file.
     *
     * @param writer      the BufferedWriter used for file output
     * @param group       the list of lists representing the group
     * @param groupCounter the number of the group
     * @throws IOException if an I/O error occurs during writing
     */
    private void writeGroup(BufferedWriter writer, List<List<Long>> group, int groupCounter) throws IOException {
        writer.write(String.format("Group %d:%n", groupCounter));
        for (List<Long> line : group) {
            writer.write(formatLine(line) + System.lineSeparator());
        }
        writer.write(System.lineSeparator());  // Add an empty line between groups for readability
    }

    /**
     * Formats a single line by wrapping its elements in quotes.
     *
     * @param line the list of Longs to format
     * @return a formatted string of the line
     */
    private String formatLine(List<Long> line) {
        return line.stream()
                .map(element -> element == 0L ? FORMATTED_EMPTY_STRING : String.format(FORMATTED_VALUE_STRING, element))
                .collect(Collectors.joining(";"));
    }
}
