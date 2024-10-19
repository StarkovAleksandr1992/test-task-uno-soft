package ru.starkov.service;

import java.util.*;

/**
 * A class that implements the Union-Find (or Disjoint Set Union) data structure to group rows of strings
 * based on shared non-empty column values.
 * <p>
 * Each unique non-empty value in a column is treated as an identifier, and rows with the same identifier
 * in the same column are grouped together. The groups are then represented as sets of lists.
 */
public class UnionFind {
    private final Random random = new Random();
    private final Map<String, Integer> columnValueToIndex = new HashMap<>();
    private final int[] parent;
    private final List<List<String>> notUnitedGroups;

    /**
     * Initializes the UnionFind structure with a list of rows (groups) and sets up parent-child relationships
     * between rows that share common non-empty column values.
     *
     * @param notUnitedGroups a list of rows (each represented as a list of strings) to be grouped
     */
    public UnionFind(List<List<String>> notUnitedGroups) {
        this.notUnitedGroups = notUnitedGroups;
        int size = notUnitedGroups.size();
        parent = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
        }
        initializeUnionFind();
    }

    /**
     * Initializes the union-find structure by iterating through the rows and grouping rows that share
     * common non-empty column values.
     */
    private void initializeUnionFind() {
        int rowCount = notUnitedGroups.size();
        for (int i = 0; i < rowCount; i++) {
            List<String> columns = notUnitedGroups.get(i);
            for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
                String value = columns.get(colIndex);
                if (!value.isEmpty()) {
                    String key = colIndex + ":" + value;
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

    /**
     * Finds the root parent of the specified index.
     * This method implements path compression to flatten the tree structure, improving efficiency.
     *
     * @param index the index to find the root parent for
     * @return the root parent of the specified index
     */
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

    /**
     * Merges the sets containing elements x and y. Randomly assigns one root as the parent of the other.
     *
     * @param x the index of the first element to union
     * @param y the index of the second element to union
     */
    public void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX != rootY) {
            if (random.nextInt() % 2 == 0) {
                parent[rootY] = rootX;
            } else {
                parent[rootX] = rootY;
            }
        }
    }

    /**
     * Groups the rows of strings based on shared column values. Rows with common non-empty column values
     * are grouped together.
     *
     * @return a map where the keys are group identifiers and the values are sets of rows (lists of strings)
     */
    public Map<Integer, Set<List<String>>> groupStrings() {
        Map<Integer, Set<List<String>>> groups = new HashMap<>();
        int rowCount = notUnitedGroups.size();
        for (int i = 0; i < rowCount; i++) {
            int root = find(i);
            groups.computeIfAbsent(root, k -> new HashSet<>()).add(notUnitedGroups.get(i));
        }
        return groups;
    }
}
