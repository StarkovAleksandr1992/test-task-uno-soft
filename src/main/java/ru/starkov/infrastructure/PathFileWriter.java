package ru.starkov.infrastructure;

import ru.starkov.model.DataType;

import java.io.IOException;
import java.time.Duration;

/**
 * Interface for writing objects to a file at a specified path.
 * <p>
 * If the output path is not specified, the file will be created in the current directory.
 *
 * @param <T> the type of object to be written to the file
 */
public interface PathFileWriter<T> {

    /**
     * Writes the specified object to a file at the given output path.
     * <p>
     * If the output path is not specified, the file will be created in the current directory.
     *
     * @param t the object to write to the file
     * @param outputPath the path to the output file; if null or empty, the file will be created in the current directory
     * @throws IOException if an I/O error occurs
     */
    void writeToTxtFile(T t, String outputPath, Duration executionTime, DataType dataType) throws IOException;
}
