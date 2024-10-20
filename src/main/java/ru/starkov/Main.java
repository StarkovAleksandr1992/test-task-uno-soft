package ru.starkov;

import ru.starkov.infrastructure.impl.LocalFileLoader;
import ru.starkov.infrastructure.impl.LocalPathFileWriter;
import ru.starkov.service.UnionFind;
import ru.starkov.service.ValidLinesExtractor;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class Main {

    public static void main(String[] args) throws IOException {
        final var startTime = LocalTime.now();

        final var fileLoader = new LocalFileLoader();
        final var fileWriter = new LocalPathFileWriter();
        final var validStringExtractor = new ValidLinesExtractor();

        final var file = fileLoader.loadFile(args[0]);
        final var validNumbers = validStringExtractor.getValidNumbers(file);

        final var unionFind = new UnionFind(validNumbers);
        final var groupedValues = unionFind.groupValues();

        final var endTime = LocalTime.now();

        fileWriter.writeToTxtFile(groupedValues,
                "Output.txt",
                Duration.between(startTime, endTime));

        System.out.printf("Job done. Execution time: %d seconds",
                Duration.between(startTime, LocalTime.now()).get(ChronoUnit.SECONDS));
    }
}
