package ru.starkov.service;

import ru.starkov.model.Key;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A class that implements the Union-Find (or Disjoint Set Union) data structure to group rows of data
 * based on shared non-empty column values.
 * <p>
 * Each unique non-empty value in a column is treated as an identifier, and rows with the same identifier
 * in the same column are grouped together. The groups are then represented as sets of lists.
 */
public class UnionFind<T> {
    private final Map<Key<T>, Integer> columnValueToIndex = new HashMap<>();
    private final int[] parent;
    private final List<List<T>> rawData;
    private final Predicate<T> isNonEmptyFunction;

    /**
     * Constructs an instance of the UnionFind class for the given raw data.
     *
     * @param rawData a list of lists where each inner list represents a row of data, and each element is a column value.
     */
    public UnionFind(List<List<T>> rawData, Predicate<T> isNonEmptyFunction) {
        this.rawData = rawData;
        this.isNonEmptyFunction = isNonEmptyFunction;
        this.parent = initializeParents(rawData.size());
        initializeUnionFind();
    }

    /**
     * Группирует строки на основе общих непустых значений столбцов.
     *
     * @return список групп, где каждая группа — это список строк.
     */
    public List<List<List<T>>> groupValues() {
        Map<Integer, List<List<T>>> groupedRows = groupRowsByRoot();
        sortGroups(groupedRows);
        return groupedRows.values()
                .stream()
                .sorted(this::compareGroups)
                .collect(Collectors.toList());
    }

    private int[] initializeParents(int size) {
        int[] parents = new int[size];
        for (int i = 0; i < size; i++) {
            parents[i] = i;
        }
        return parents;
    }

    private void initializeUnionFind() {
        for (int rowIndex = 0; rowIndex < rawData.size(); rowIndex++) {
            processRow(rowIndex, rawData.get(rowIndex));
        }
    }

    private void processRow(int rowIndex, List<T> columns) {
        for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
            T value = columns.get(colIndex);
            if (isNonEmptyFunction.test(value)) {
                handleNonEmptyValue(rowIndex, colIndex, value);
            }
        }
    }

    private void handleNonEmptyValue(int rowIndex, int colIndex, T value) {
        Key<T> key = new Key<>(colIndex, value);
        columnValueToIndex.putIfAbsent(key, rowIndex);
        int existingRow = columnValueToIndex.get(key);
        union(rowIndex, existingRow);
    }

    private int find(int index) {
        int root = index;
        while (parent[root] != root) {
            root = parent[root];
        }
        while (index != root) {
            int next = parent[index];
            parent[index] = root;
            index = next;
        }
        return root;
    }

    private void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX != rootY) {
            parent[rootY] = rootX;
        }
    }

    private Map<Integer, List<List<T>>> groupRowsByRoot() {
        Map<Integer, List<List<T>>> groups = new HashMap<>();
        for (int i = 0; i < rawData.size(); i++) {
            int root = find(i);
            groups.computeIfAbsent(root, k -> new ArrayList<>()).add(rawData.get(i));
        }
        return groups;
    }

    private void sortGroups(Map<Integer, List<List<T>>> groups) {
        groups.values().forEach(group ->
                group.sort((l1, l2) -> Integer.compare(l2.size(), l1.size()))
        );
    }

    private int compareGroups(List<List<T>> group1, List<List<T>> group2) {
        int groupCountComparison = Integer.compare(group2.size(), group1.size());
        if (groupCountComparison != 0) {
            return groupCountComparison;
        }

        long elementCountGroup1 = group1.stream().mapToLong(List::size).sum();
        long elementCountGroup2 = group2.stream().mapToLong(List::size).sum();
        return Long.compare(elementCountGroup2, elementCountGroup1);
    }
}
