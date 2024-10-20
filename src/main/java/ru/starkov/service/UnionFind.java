package ru.starkov.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that implements the Union-Find (or Disjoint Set Union) data structure to group rows of strings
 * based on shared non-empty column values.
 * <p>
 * Each unique non-empty value in a column is treated as an identifier, and rows with the same identifier
 * in the same column are grouped together. The groups are then represented as sets of lists.
 */
public class UnionFind {
    private final Map<Key, Integer> columnValueToIndex = new HashMap<>();
    private final int[] parent;
    private final List<List<Long>> rawData;

    public UnionFind(List<List<Long>> rawData) {
        this.rawData = rawData;
        int size = rawData.size();
        parent = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
        }
        initializeUnionFind();
    }

    private void initializeUnionFind() {
        int rowCount = rawData.size();
        for (int i = 0; i < rowCount; i++) {
            final var columns = rawData.get(i);
            for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
                Long value = columns.get(colIndex);
                if (value != 0L) {
                    final var key = new Key(colIndex, value);
                    if (!columnValueToIndex.containsKey(key)) {
                        columnValueToIndex.put(key, i);
                    } else {
                        int existingRow = columnValueToIndex.get(key);
                        union(i, existingRow);
                    }
                }
            }
        }
    }

    public int find(int index) {
        int root = index;
        while (parent[root] != root) {
            root = parent[root];
        }
        while (index != root) {
            int parentOfX = parent[index];
            parent[index] = root;
            index = parentOfX;
        }
        return root;
    }

    public void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX != rootY) {
            parent[rootY] = rootX;
        }
    }

    public Map<Integer, List<List<Long>>> groupValues() {
        Map<Integer, List<List<Long>>> groups = new HashMap<>();
        int rowCount = rawData.size();
        for (int i = 0; i < rowCount; i++) {
            int root = find(i);
            groups.computeIfAbsent(root, k -> new ArrayList<>()).add(rawData.get(i));
        }
        return groups;
    }

    private record Key(Integer colIndex, Long value) {
    }
}
