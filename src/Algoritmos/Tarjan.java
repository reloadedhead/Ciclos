package Algoritmos;

import java.util.Stack;

public class Tarjan {
    private int n, pre, count = 0;
    private int[] id, low;
    private boolean[] marked;
    private boolean[][] adj;
    private Stack<Integer> stack = new Stack<>();

    // Tarjan input requires an NxN adjacency matrix
    public Tarjan(boolean[][] adj) {
        n = adj.length;
        this.adj = adj;
        marked = new boolean[n];
        id = new int[n];
        low = new int[n];
        for (int u = 0; u < n; u++)
            if (!marked[u]) dfs(u);
    }

    private void dfs(int u) {
        marked[u] = true;
        low[u] = pre++;
        int min = low[u];
        stack.push(u);
        for (int v = 0; v < n; v++) {
            if (adj[u][v]) {
                if (!marked[v]) dfs(v);
                if (low[v] < min) min = low[v];
            }
        }
        if (min < low[u]) { low[u] = min; return; }
        int v;
        do { v = stack.pop(); id[v] = count; low[v] = n; } while (v != u);
        count++;
    }

    // Returns the id array with the strongly connected components.
    // If id[i] == id[j] then nodes i and j are part of the same strongly connected component.
    public int[] getStronglyConnectedComponents() {
        return id.clone();
    }

    // Returns the number of strongly connected components in this graph
    public int countStronglyConnectedComponents() {
        return count;
        }
}