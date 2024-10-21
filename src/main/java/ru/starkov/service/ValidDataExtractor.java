package ru.starkov.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The {@code ValidDataExtractor} class is responsible for extracting valid lines from a file,
 * processing them, and transforming them into a list of numbers of type {@code T}.
 *
 * @param <T> the type of the numbers to be extracted
 */
public class ValidDataExtractor<T> {

    private final Function<String, T> parser;
    private final Predicate<String> validStringPredicate;

    /**
     * Constructs an instance of {@code ValidDataExtractor} with the specified parsing and validation functions.
     *
     * @param parser              a function to parse strings into the type {@code T}
     * @param validStringPredicate a predicate to validate the strings
     */
    public ValidDataExtractor(Function<String, T> parser, Predicate<String> validStringPredicate) {
        this.parser = parser;
        this.validStringPredicate = validStringPredicate;
    }

    /**
     * Reads a file and extracts valid data. Each line of the file is split by semicolons,
     * and only strings that match the given validation pattern are processed. Valid strings
     * are parsed into the type {@code T} and returned as a list of lists.
     *
     * @param file the file to read
     * @return a list of lists containing valid data parsed as {@code T}
     * @throws IOException if an I/O error occurs while reading the file
     * @throws NullPointerException if the provided file is {@code null}
     */
    public List<List<T>> getValidData(File file) throws IOException {
        Objects.requireNonNull(file, "File can't be null");
        try (final var bufferedReader = new BufferedReader(new FileReader(file))) {
            return bufferedReader.lines()
                    .map(s -> s.split(";"))
                    .map(List::of)
                    .filter(strings -> strings.stream().allMatch(validStringPredicate))
                    .map(strings -> strings.stream()
                            .map(s -> s.replaceAll("\"", ""))
                            .map(parser)
                            .toList())
                    .distinct()
                    .toList();
        } catch (IOException e) {
            System.err.printf("Error reading file: %s", file.getAbsolutePath());
            throw e;
        }
    }
}
