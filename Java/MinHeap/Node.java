/**
 * Created by Garett on 10/13/2017.
 */

//Node is used for minHeap. Children are used for huffman()
public class Node {
    private Node left,right;
    private String name;
    private int priority;

    public Node(String n, int p) {
        name = n;
        priority = p;
    }

    public void addPriority() {
        priority++;
    }
    public void setLeft(Node n) {
        left = n;
    }

    public void setRight(Node n) {
        right = n;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public void setName(String n) {
        name = n;
    }

    public String getName() {
        return name;
    }
    public int getPriority() {
        return priority;
    }
    public void setPriority(int p) {
        priority = p;
    }
}
