package ru.starkov.model;

/**
 * Represents a key consisting of a column index and a value, used in a union-find data structure.
 *
 * <p>This record encapsulates a key with two components:</p>
 * <ul>
 *     <li><b>colIndex</b>: The index of the column (or another related identifier).</li>
 *     <li><b>value</b>: The value associated with the key, which can be of any generic type {@code T}.</li>
 * </ul>
 *
 * @param <T> The type of the value associated with the key.
 */
public record Key<T>(Integer colIndex, T value) {

    @Override
    public String toString() {
        return colIndex + ":" + value;
    }
}
