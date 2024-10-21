package ru.starkov;

import ru.starkov.infrastructure.impl.LocalFileLoader;
import ru.starkov.infrastructure.impl.LocalPathFileWriter;
import ru.starkov.model.DataType;
import ru.starkov.service.DynamicTypeDetector;
import ru.starkov.service.IsNotEmptyFunctionProvider;
import ru.starkov.service.UnionFind;
import ru.starkov.service.ValidDataExtractor;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Main {

    public static void main(String[] args) throws IOException {
        final var startTime = LocalTime.now();

        final var fileLoader = new LocalFileLoader();
        final var file = fileLoader.loadFile(args[0]);

        final var dataType = DynamicTypeDetector.determineDataType(file);

        switch (dataType) {
            case STRING -> processData(
                    file,
                    startTime,
                    dataType,
                    s -> s.replaceAll("^\"|\"$", ""),
                    s -> !s.isEmpty()
            );
            case LONG -> processData(
                    file,
                    startTime,
                    dataType,
                    s -> s.isEmpty() ? 0L : Long.parseLong(s),
                    s -> s.matches("^\"\\d*\"$")
            );
            case DOUBLE -> processData(
                    file,
                    startTime,
                    dataType,
                    s -> s.isEmpty() ? 0.0 : Double.parseDouble(s),
                    s -> s.matches("^\"\\d+(\\.\\d+\"$)?")
            );
        }
    }

    private static <T> void processData(
            File file,
            LocalTime startTime,
            DataType dataType,
            Function<String, T> parseFunction,
            Predicate<String> validationPredicate
    ) throws IOException {
        final var validDataExtractor = new ValidDataExtractor<>(
                parseFunction,
                validationPredicate
        );
        final var validData = validDataExtractor.getValidData(file);
        final var unionFind = new UnionFind<>(
                validData,
                IsNotEmptyFunctionProvider.getIsNotEmptyPredicate(dataType)
        );
        final var groupedData = unionFind.groupValues();
        final var endTime = LocalTime.now();

        final var duration = Duration.between(startTime, endTime);
        final var fileWriter = new LocalPathFileWriter<T>();
        fileWriter.writeToTxtFile(
                groupedData,
                "Output.txt",
                duration,
                dataType
        );
        System.out.printf("Job done. Execution time: %d seconds%n", duration.getSeconds());
        System.out.printf(
                "Number of groups with more than 1 element: %d%n",
                groupedData.stream().filter(group -> group.size() > 1).count()
        );
    }
}
