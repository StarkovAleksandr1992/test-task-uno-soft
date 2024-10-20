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

public class ValidLinesExtractor {

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

    public static class ValidStringPredicate implements Predicate<String> {

        private static final Pattern STRING_PATTERN = Pattern.compile("^\"\\d*\"$");

        @Override
        public boolean test(String s) {
            Matcher matcher = STRING_PATTERN.matcher(s);
            return matcher.matches();
        }
    }
}
