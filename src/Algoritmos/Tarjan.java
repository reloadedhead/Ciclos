package Algoritmos;

import java.util.Stack;

/**
 * La clase Tarjan es la implementación del algoritmo de Tarjan para encontrar componentes fuertemente conectados
 * dentro de un grafo dirigido, a partir de una matriz de adyacencia.
 */
public class Tarjan {
    private int n, pre, count = 0;
    private int[] compoonentes, low;
    private boolean[] marcados;
    private boolean[][] adj;
    private Stack<Integer> stack = new Stack<>();

    /**El input del algoritmo es una matriz de adjacencias de tamaño n
     * @param adj matriz de adyacencias
     */
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

    /**
     * Depth First Search
     * @param u nodo
     */
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

    /** Este metodo devuelve en un arreglo de enteros los ciclos. Si compoonentes[i] == compoonentes[j] entonces los
     * nodos i y j son parte del mismo componente fuertemente conectado.
     * @return arreglo de componentes fuertemente conectadas.
     */
    public int[] getStronglyConnectedComponents() {
        return compoonentes.clone();
    }


    /**
     * Este método devuelve la cantidad de ciclos.
     * @return cantidad de ciclos del grafo.
     */
    public int countStronglyConnectedComponents() {
        return count;
        }
}