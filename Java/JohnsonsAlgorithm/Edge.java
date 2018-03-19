/**
 * Created by Garett on 12/1/2017.
 */
public class Edge {
    private int src, dest, w;
    public Edge(int s, int d, int w) {
        src = s;
        dest = d;
        this.w = w;
    }

    public int getW() {
        return w;
    }
}
