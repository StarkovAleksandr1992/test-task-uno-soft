package ru.starkov.infrastructure.impl;

import ru.starkov.infrastructure.FileLoader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Implementation of the FileLoader interface for loading local files.
 */
public class LocalFileLoader implements FileLoader {

    /**
     * Loads a local file from the specified file path.
     *
     * @param filePath the path of the file to be loaded
     * @return the loaded File object
     * @throws NullPointerException if the file path is null
     * @throws IllegalArgumentException if the file does not exist, is not a file,
     * is not readable, or has an unsupported format (non-.txt file)
     */
    @Override
    public File loadFile(String filePath) {
        Objects.requireNonNull(filePath, "File path cannot be null");
        final var path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File does not exist: " + filePath);
        }
        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException("Provided path is not a file: " + filePath);
        }
        if (!filePath.toLowerCase().endsWith(".txt")) {
            throw new IllegalArgumentException("Unsupported file format. Expected a file with .txt extension: " + filePath);
        }
        if (!Files.isReadable(path)) {
            throw new IllegalArgumentException("File is not readable: " + filePath);
        }
        return path.toFile();
    }
}
