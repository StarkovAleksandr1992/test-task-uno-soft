package ru.starkov.infrastructure.impl;

import ru.starkov.infrastructure.PathFileWriter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the PathFileWriter interface for writing a Map of groups
 * to a file, where each group is sorted by size and its contents are formatted with quotes.
 * <p>
 * If the output path is not specified, the file will be created in the current directory.
 */
public class LocalPathFileWriter implements PathFileWriter<Map<Integer, Set<List<String>>>> {

    private static final String FORMATTED_EMPTY_STRING = "\"           \"";
    private static final String FORMATTED_VALUE_STRING = "\"%s\"";
    private static final String DEFAULT_OUTPUT_FILE = "output.txt";
    /**
     * Writes a Map of integer keys and Set of List of Strings to a file.
     * The groups are sorted by size in descending order, and each list is
     * formatted by wrapping its elements in quotes.
     * <p>
     * If the output path is not specified, the file will be created in the current directory.
     *
     * @param groups the map of groups to write to the file
     * @param outputPath the path to the output file; if null or empty, the file will be created in the current directory
     * @throws IOException if an I/O error occurs during file writing
     */
    @Override
    public void writeToTxtFile(Map<Integer, Set<List<String>>> groups, String outputPath) throws IOException {
        final var filePath = Optional.ofNullable(outputPath)
                .filter(path -> !path.isEmpty())
                .orElse(DEFAULT_OUTPUT_FILE);

        final var sortedGroups = groups.values().stream()
                .sorted((l1, l2) -> Integer.compare(l2.size(), l1.size()))
                .toList();

        try (final var writer = new BufferedWriter(new FileWriter(filePath))) {
            int groupCounter = 1;
            for (final var group : sortedGroups) {
                writeGroup(writer, group, groupCounter++);
            }
        }

    }

    private void writeGroup(BufferedWriter writer, Set<List<String>> group, int groupCounter) throws IOException {
        writer.write("Group " + groupCounter + "\n");
        for (final var line : group) {
            writer.write(formatLine(line) + "\n");
        }
        writer.write("\n");
    }

    private String formatLine(List<String> line) {
        return line.stream()
                .map(element -> element.isEmpty() ? FORMATTED_EMPTY_STRING : FORMATTED_VALUE_STRING.formatted(element))
                .collect(Collectors.joining(";"));
    }
}
