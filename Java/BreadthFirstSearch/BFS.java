import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Garett on 11/29/2017.
 */
public class BFS {
    private ArrayList<String> out = new ArrayList<String>();
    private Vertex[] graph;


    public String getOut() {
        String[] output = out.toArray(new String[out.size()]);
        String out = output[0];
        for (int i=1; i<output.length; i++) {
            out += " " + output[i];
        }
        return out;
    }

    public void makeGraph(HashMap hm) {
        Set<String> keySet = hm.keySet();
        String[] keys = keySet.toArray(new String[keySet.size()]);
        Vertex current;
        graph = new Vertex[hm.size()];
        for (int i=0; i<keys.length; i++) {
            graph[i] = new Vertex(keys[i]);
        }
        String[] pre;
        Vertex[] preV;
        boolean found;
        int i_prereqs = 0;
        for (int i=0; i<graph.length; i++) {
            pre = (String[]) hm.get(keys[i]);
            preV = new Vertex[pre.length];

            for (int j=0; j<pre.length; j++) {
                for (int k=0; k<graph.length; k++) {
                    if (pre[j].equals(graph[k].getValue())) {
                        preV[i_prereqs] = graph[k];
                        i_prereqs++;
                    }
                }

            }
            graph[i].setEdges(preV);
            i_prereqs = 0;
        }
    }

    public void doSearch(Vertex v) {
        if (v.isDiscovered()) {
            return;
        } else {
            if (v.getEdges().length == 0) {
                v.discover();
                out.add(v.getValue());
                return;
            } else {
                for (int i=0; i<v.getEdges().length; i++) {
                    doSearch(v.getEdges()[i]);
                }
            }
            v.discover();
            out.add(v.getValue());
        }
    }

    public static void main(String[] args) {
        try {
            Scanner s = new Scanner(new File(args[0]));
            String in = "";
            while (s.hasNextLine()) {
                in += s.nextLine() + "\n";
            }

            HashMap<String, String[]> hm = new HashMap<String, String[]>();
            String[] classes = in.split("\n");
            String name;
            String[] pre;
            String[] check;
            for (int i=0; i<classes.length; i++) {
                check = classes[i].split(":");
                name = check[0];
                if (check.length == 1) {
                    hm.put(name, new String[0]);
                } else {
                    pre = check[1].split(" ");

                    hm.put(name, pre);
                }
            }
            BFS bfs = new BFS();
            bfs.makeGraph(hm);
            for (int i=0; i<classes.length; i++) {
                bfs.doSearch(bfs.graph[i]);
            }
            String out = bfs.getOut();
            File outFile = new File("output.txt");
            outFile.createNewFile();
            FileOutputStream outStream = new FileOutputStream(outFile);
            outStream.write(out.getBytes());
        } catch (IOException f) {}

    }
}
