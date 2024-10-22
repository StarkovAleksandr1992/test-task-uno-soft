package ru.starkov.model;

/**
 * Enumeration representing different types of data that can be used in the application.
 *
 * <p>The {@code DataType} enum provides three possible data types:</p>
 * <ul>
 *     <li><b>LONG</b>: Represents a long integer value.</li>
 *     <li><b>DOUBLE</b>: Represents a floating-point value.</li>
 *     <li><b>STRING</b>: Represents a sequence of characters (text).</li>
 * </ul>
 *
 * <p>This enum can be used to specify the type of data being processed, ensuring that
 * the correct type is handled appropriately in the union-find or other data structures.</p>
 */
public enum DataType {
    LONG, DOUBLE, STRING
}
