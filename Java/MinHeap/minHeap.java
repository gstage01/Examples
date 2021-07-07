import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Garett on 10/14/2017.
 */
public class minHeap {
        private Node[] pq;
        private int size = 0;

        public minHeap() {
            pq = new Node[1000000];
        }

        public void heapify(int index) {
            Node hold;
            int i = index;
            int iLeft;
            int iRight;
            if (i == 0) {
                iLeft = 1;
                iRight = 2;
            } else {
                iLeft = 2 * index - 1;
                iRight = 2 * index;
                i--;
            }


            Node max = pq[i];
            int iMin = i;
            if (pq[iLeft] != null && (pq[iLeft].getPriority() < max.getPriority())) {
                max = pq[iLeft];
                iMin = iLeft;
            }
            if (pq[iRight] != null && (pq[iRight].getPriority() < max.getPriority())) {
                max = pq[iRight];
                iMin = iRight;
            }
            if (!pq[i].equals(max)) {
                hold = pq[i];
                pq[i] = max;
                pq[iMin] = hold;
                heapify(iMin - 1);
            }

        }

        public Node extract_min() {
            Node ret = pq[0];
            pq[0] = pq[size - 1];
            pq[size - 1] = null;
            size--;
            if (size != 0) {
                heapify(0);
            }
            return ret;
        }

        public void insert(Node p) {

            if (size == 0) {
                pq[0] = p;
                size++;
            } else if (size == 1) {
                pq[1] = p;
                if (p.getPriority() > pq[0].getPriority()) {
                    pq[2] = pq[0];
                    pq[0]=pq[1];
                    pq[1]=pq[2];
                    pq[2] = null;
                }
                size++;
            } else if (size == 2) {
                pq[2] = p;
                size++;
                heapify(0);
            } else {
                pq[size] = p;
                size++;
                for (int i = size / 2; i > 0; i--) {
                    heapify(i);
                }
            }
        }

        public void huffman() {
            Node node1 = extract_min();
            Node node2 = extract_min();
            Node internal = new Node("in", node1.getPriority() + node2.getPriority());
            internal.setLeft(node1);
            internal.setRight(node2);
            insert(internal);
            if (size > 1) {
                huffman();
            }

        }

        public void writeOutput(FileOutputStream f, String current, Node n) {
            try {

                //Checks right and left children for inner nodes
                //If the node found is an inner node, writeOutput is recursively called on the children

                if (n.getLeft() != null && n.getLeft().getName().equals("in")) {
                    writeOutput(f, current.concat("0"), n.getLeft());

                    //If the node is not an inner, it must be null or a characte
                    //The character path is maintained, and wrote to the file
                } else if (n.getLeft() != null) {
                    f.write(String.format("%s:%s", n.getLeft().getName(), current.concat("0\n")).getBytes());
                }

                //Repeat for right child
                if (n.getRight() != null && n.getRight().getName().equals("in")) {
                    writeOutput(f, current.concat("1"), n.getRight());
                } else if (n.getRight() != null) {
                    f.write(String.format("%s:%s", n.getRight().getName(), current.concat("1\n")).getBytes());
                }
            } catch (IOException e) {
                System.out.println("Print failed");
            }
        }

        public static void main(String[] args) {
            try {

                //Set up input scanner
                Scanner in = new Scanner(new File(args[0]));
                in.useDelimiter("");    //Allows for reading in a single character

                //Set up output file
                File outFile = new File("output.txt");
                outFile.createNewFile();
                FileOutputStream outStream = new FileOutputStream(outFile);

                //Scan input
                String inString;
                ArrayList<Node> list = new ArrayList<>(5);
                boolean found = false;
                while (in.hasNext()) {
                    found = false;
                    inString = in.next();
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getName().equals(inString)) {
                            list.get(i).addPriority();
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        list.add(new Node(inString, 1));
                    }
                }
                list.remove(list.size()-1);     //remove null terminator
                minHeap heap = new minHeap();

                //Inserts into the min heap. Insert maintains heap property
                for (int i=0; i<list.size(); i++) {
                    heap.insert(list.get(i));
                }

                //huffman begins the Huffman algorithm on the heap
                heap.huffman();

                //Writes the output to the file. pq[] is a list of nodes. pq[0] is the root node
                heap.writeOutput(outStream, "", heap.pq[0] );
            } catch (IOException f) {
                System.out.println("File not found");
            }


        }
}