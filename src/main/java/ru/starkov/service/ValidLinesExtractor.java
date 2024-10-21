package ru.starkov.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * The {@code ValidLinesExtractor} class is responsible for extracting valid lines from a file,
 * processing them, and converting the valid strings into a list of {@code Long} numbers.
 * Each line in the file is expected to be a semicolon-separated string of quoted numbers.
 */
public class ValidLinesExtractor {

    /**
     * Reads a file and extracts valid numbers. Each line of the file is split by semicolons
     * and processed to extract strings that match a specific pattern (i.e., quoted numbers).
     * Valid strings are converted into {@code Long} and returned as a list of lists of numbers.
     *
     * @param file the input file to read
     * @return a list of lists containing valid {@code Long} numbers extracted from the file
     * @throws IOException if an I/O error occurs during reading the file
     * @throws NullPointerException if the input file is {@code null}
     */
    public List<List<Long>> getValidNumbers(File file) throws IOException {
        Objects.requireNonNull(file, "File can't be null");
        try (final var bufferedReader = new BufferedReader(new FileReader(file))) {
            final var validStringPredicate = new ValidStringPredicate();
            return bufferedReader.lines()
                    .map(s -> s.split(";"))
                    .map(List::of)
                    .filter(strings -> strings.stream().allMatch(validStringPredicate))
                    .map(strings -> strings.stream()
                            .map(s -> s.replaceAll("\"", ""))
                            .map(this::parseStringToLong)
                            .toList())
                    .distinct()
                    .toList();
        } catch (IOException e) {
            System.err.printf("Error reading file: %s", file.getAbsolutePath());
            throw e;
        }
    }

    private Long parseStringToLong(String s) {
        if (s.isEmpty()) {
            return 0L;
        }
        return Long.parseLong(s);
    }
    /**
     * A {@code Predicate} that tests whether a string matches a valid pattern.
     * The valid pattern is a quoted number (e.g., "123").
     */
    public static class ValidStringPredicate implements Predicate<String> {

        private static final Pattern STRING_PATTERN = Pattern.compile("^\"\\d*\"$");

        @Override
        public boolean test(String s) {
            Matcher matcher = STRING_PATTERN.matcher(s);
            return matcher.matches();
        }
    }
}
