package Algoritmos;

import java.util.Stack;

public class Tarjan {
    private int n, pre, count = 0;
    private int[] compoonentes, low;
    private boolean[] marcados;
    private boolean[][] adj;
    private Stack<Integer> stack = new Stack<>();
    
    //El input del algoritmo es una matriz de adjacencias de tamaño n
    public Tarjan(boolean[][] adj) {
        n = adj.length;
        this.adj = adj;
        marcados = new boolean[n];
        compoonentes = new int[n];
        low = new int[n];
        for (int u = 0; u < n; u++)
            if (!marcados[u])
            {
                dfs(u);
            }
    }

    private void dfs(int u) {
        marcados[u] = true;
        low[u] = pre++;
        int min = low[u];
        stack.push(u);
        for (int v = 0; v < n; v++) {
            if (adj[u][v]) {
                if (!marcados[v]) dfs(v);
                if (low[v] < min) min = low[v];
            }
        }
        if (min < low[u]) { low[u] = min; return; }
        int v;
        do { v = stack.pop(); compoonentes[v] = count; low[v] = n; } while (v != u);
        count++;
    }

    // Returns the compoonentes array with the strongly connected components.
    // If compoonentes[i] == compoonentes[j] then nodes i and j are part of the same strongly connected component.
    public int[] getStronglyConnectedComponents() {
        return compoonentes.clone();
    }

    // Returns the number of strongly connected components in this graph
    public int countStronglyConnectedComponents() {
        return count;
        }
}