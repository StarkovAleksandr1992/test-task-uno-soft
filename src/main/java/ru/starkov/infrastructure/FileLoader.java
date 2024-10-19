package ru.starkov.infrastructure;

import java.io.File;

/**
 * Interface for loading files.
 */
public interface FileLoader {

    /**
     * Loads a file from the specified file path.
     *
     * @param filePath the path of the file to be loaded
     * @return the loaded File object
     * @throws NullPointerException if the file path is null
     * @throws IllegalArgumentException if the file does not exist, is not a file, is not readable,
     * or has an unsupported format
     */
    File loadFile(String filePath);
}
