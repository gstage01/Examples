import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;


/**
 * Created by Garett on 11/30/2017.
 */
public class Johnson {
    public void johnson(int[][] G) {
        // Make G'
        int[][] gPrime = makeGPrime(G);

        //Bellman-Ford to find smallest distances (h)
        int[] h = bellmanFord(gPrime, gPrime.length);
        if (h[h.length-1] == 1) {           //If Bellman-ford returns 1 (last element of h is placeholder for this)
            writeOutput("Negative cycle");  //Print negative cycle and return.
            return;
        }

        //Calculate new weights
        int[][] uwGraph = new int[h.length-1][h.length-1];
        for (int i=0; i<h.length-1; i++) {
            for (int j=0; j<h.length-1; j++) {
                uwGraph[i][j] = gPrime[i][j] - h[i] + h[j];
            }
        }

        //Perform Dijkstra's algorithm
        int[][] dijkstraGraph = new int[G.length][G.length];
        int[] hold;
        for (int i=0; i<G.length; i++) {
            hold = dijkstra(uwGraph, i, h);         // hold is a buffer variable for each row of dijkstra outputs
            for (int j=0; j<G.length; j++) {        // This allows us to do step 3 and 4 in the same line by iteration
                dijkstraGraph[i][j] = hold[j] + h[i]-h[j];
            }
        }

        //Set up string output
        String out = "";
        for (int i=0; i<G.length; i++) {
            out += dijkstraGraph[i][0];
            for (int j=1; j<G.length; j++) {
                out += " " + dijkstraGraph[i][j];
            }
            if (i<G.length-1) {
                out += "\n";
            }
        }
        //Write and return
        writeOutput(out);
        return;
    }

    //Simple function for writing output to a file
    public void writeOutput(String s) {
        try {
            File outFile = new File("output.txt");
            outFile.createNewFile();
            FileOutputStream outStream = new FileOutputStream(outFile);
            outStream.write(s.getBytes());
        } catch (IOException e) {}
    }

    //Dijkstra's algorithm
    public int[] dijkstra(int[][] G, int src, int[] BFdist) {

        // Initialize the graph and necessary variables
        int[][] dijkstraGraph = new int[G.length][G.length];
        boolean[] visited = new boolean[G.length];
        int[] pi = new int[G.length];
        int unvisited = 1;
        int max = 2000000;

        // Initialize pi and set source variable
        for (int i = 0; i < G.length; i++) {
            pi[i] = max;
        }

        pi[src] = 0;

        for (int srcN = 0; srcN < G.length; srcN++) {
            for (int dest = 0; dest < G.length; dest++) {
                dijkstraGraph[srcN][dest] = G[srcN][dest];
            }
        }

        int min = 0;
        int vertex = 0;
        while (unvisited > 0) {         //While Q not empty

            //Extract min:
            min = max;
            for (int i=0; i<G.length; i++) {
                if (visited[i] != true && pi[i] < min) {
                    vertex = i;
                    min = pi[i];
                }
            }

            // S U {u}
            unvisited--;
            visited[vertex] = true;


            int next = -1;
            for (int i=0; i<G.length; i++) {
                // Relax and update queue
                if (!visited[i]) {
                    if (G[vertex][i] != max) {
                        min = G[vertex][i];
                        next = pi[vertex] + min;

                        if (min < pi[i]) {
                            pi[i] = next;
                        }
                        unvisited++;
                    }
                }
            }

        }

        // Returns distance array
        return pi;
    }

    //Bellman-Ford algorithm
    public int[] bellmanFord(int[][] G, int src) {

        //Initialize graph
        int size = G.length - 1;
        int shortest[] = new int[G.length];
        int max = 2000000;
        for (int i = 0; i <= size; i++) {
            shortest[i] = max;
        }

        // Set starting point
        shortest[src-1] = 0;

        // For each vertex
        for (int i = 0; i < size-1; i++) {
            // For each edge
            for (int srcN = 0; srcN < size; srcN++) {
                for (int dest = 0; dest < size; dest++) {
                    // Relax
                    if (G[srcN][dest] != max) {
                        if (shortest[dest] > (shortest[srcN] + G[srcN][dest])) {
                            shortest[dest] = shortest[srcN] + G[srcN][dest];
                        }
                    }
                }
            }
        }
        // For each edge
        for (int srcN = 0; srcN < size; srcN++) {
            for (int dest = 0; dest < size; dest++) {
                // Test for negative cycles. Put a 1 in the placeholder value in the array.
                if (G[srcN][dest] != max) {
                    if (shortest[dest] > shortest[srcN] + G[srcN][dest]) {
                        shortest[shortest.length - 1] = 1;
                    }
                }
            }
        }
        return shortest;

    }

    // Make G'
    public int[][] makeGPrime(int[][] G) {
        //Initialize G'
        int[][] gPrime = new int[G.length+1][G.length+1];
        // Copy contents of 3x3 matrix
        for (int src=0; src<G.length; src++) {
            for (int dest=0; dest<G.length; dest++) {
                gPrime[src][dest] = G[src][dest];
            }
        }
        //Add source vertex
        for (int dest=0; dest<G.length; dest++) {
            gPrime[G.length][dest] = 0;
        }
        return gPrime;
    }

    public static void main(String[] args) {
        try {
            //Scan input
            Scanner s = new Scanner(new File(args[0]));
            String in = "";
            while (s.hasNextLine()) {
                in += s.nextLine() + "\n";
            }
            String[] lines = in.split("\n");

            // Create graph from input
            String[][] stringGraph = new String[lines.length][];
            for (int i = 0; i < lines.length; i++) {
                stringGraph[i] = lines[i].split(" ");
            }

            // Turn input from string -> int
            int[][] graph = new int[stringGraph.length][stringGraph[0].length];
            for (int i = 0; i < graph.length; i++) {
                for (int j = 0; j < graph[i].length; j++) {
                    graph[i][j] = Integer.parseInt(stringGraph[i][j]);
                }
            }

            // Perform Johnson's algorithm
            Johnson j = new Johnson();
            j.johnson(graph);
        } catch (IOException e) {
        }
    }
}
