package ru.starkov.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ValidLinesExtractorTest {

    private ValidLinesExtractor validLinesExtractor;

    @BeforeEach
    void setUp() {
        validLinesExtractor = new ValidLinesExtractor();
    }

    @Test
    void testValidNumbersExtraction() throws IOException, URISyntaxException {
        // Arrange
        URL resource = Objects.requireNonNull(getClass().getClassLoader().getResource("testData.txt"));
        File file = Paths.get(resource.toURI()).toFile();
        // Act
        List<List<Long>> result = validLinesExtractor.getValidNumbers(file);
        // Assert
        assertEquals(4, result.size());
        assertEquals(List.of(123L, 456L, 789L), result.get(0));
        assertEquals(List.of(321L, 654L, 987L), result.get(1));
        assertEquals(List.of(111L, 222L, 333L), result.get(2));
        assertEquals(List.of(0L, 456L, 147L), result.get(3));
    }

    @Test
    void testNullFileThrowsException() {
        assertThrows(NullPointerException.class, () -> validLinesExtractor.getValidNumbers(null));
    }

    @Test
    void testValidStringPredicate() {
        ValidLinesExtractor.ValidStringPredicate predicate = new ValidLinesExtractor.ValidStringPredicate();

        assertTrue(predicate.test("\"\""));
        assertTrue(predicate.test("\"123\""));
        assertFalse(predicate.test("123"));
        assertFalse(predicate.test("\"abc\""));
        assertFalse(predicate.test("\"123abc\""));
        assertFalse(predicate.test("\"123\"1234\""));
    }
}
