package ru.starkov;

import ru.starkov.infrastructure.impl.LocalFileLoader;
import ru.starkov.infrastructure.impl.LocalPathFileWriter;
import ru.starkov.service.UnionFind;
import ru.starkov.service.ValidStringExtractor;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        final var fileLoader = new LocalFileLoader();
        final var pathFileWriter = new LocalPathFileWriter();
        final var invalidStringFilter = new ValidStringExtractor();

        final var file = fileLoader.loadFile(args[0]);
        final var validStrings = invalidStringFilter.getValidStrings(file);

        final var unionFind = new UnionFind(validStrings);
        final var groups = unionFind.groupStrings();

        try {
            pathFileWriter.writeToTxtFile(groups, "output.txt");
        } catch (IOException e) {
            System.err.println("Error writing groups to file: " + e.getMessage());
        }
    }
}