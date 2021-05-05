/**
 * Created by Garett on 11/29/2017.
 */
public class Vertex {
    private String value;
    private Vertex[] edges;
    boolean discovered;

    public Vertex(String v) {
        value = v;
        discovered = false;
    }

    public void setEdges(Vertex[] e) {
        edges = e;
    }

    public String getValue() {
        return value;
    }

    public Vertex[] getEdges() {
        return edges;
    }

    public boolean isDiscovered() {
        return discovered;
    }

    public void discover() {
        discovered = true;
    }
}
