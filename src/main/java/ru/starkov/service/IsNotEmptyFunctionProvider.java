package ru.starkov.service;

import ru.starkov.model.DataType;

import java.util.function.Predicate;

public class IsNotEmptyFunctionProvider {

    public static <T> Predicate<T> getIsNotEmptyPredicate(DataType dataType) {
        return switch (dataType) {
            case LONG -> value -> value instanceof Long && (Long) value != 0L;
            case DOUBLE -> value -> value instanceof Double && ((Double) value).compareTo(0.0) != 0;
            case STRING -> value -> value instanceof String && !((String) value).isEmpty();
            default -> throw new IllegalArgumentException("Unsupported data type: " + dataType);
        };
    }
}
