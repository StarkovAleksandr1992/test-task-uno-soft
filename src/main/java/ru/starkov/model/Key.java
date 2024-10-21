package ru.starkov.model;

public record Key<T>(Integer colIndex, T value) {
        @Override
        public String toString() {
            return colIndex + ":" + value;
        }
    }