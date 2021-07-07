import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Garett on 10/15/2017.
 */
public class Sort {
    public static SubBucket bucketSort(String[] A, int i) {

        //Initialize buckets for recursive iteration
        SubBucket B[] = new SubBucket[26];
        for (int j=0; j<26; j++) {
            B[j] = new SubBucket(j+1, i);
        }

        //Insert elements into buckets
        for (int j=0; j< A.length; j++) {
            switch (String.valueOf(Character.toLowerCase(A[j].charAt(i)))) {
                case "a":
                    B[0].insert(A[j]);
                    break;
                case "b":
                    B[1].insert(A[j]);
                    break;
                case "c":
                    B[2].insert(A[j]);
                    break;
                case "d":
                    B[3].insert(A[j]);
                    break;
                case "e":
                    B[4].insert(A[j]);
                    break;
                case "f":
                    B[5].insert(A[j]);
                    break;
                case "g":
                    B[6].insert(A[j]);
                    break;
                case "h":
                    B[7].insert(A[j]);
                    break;
                case "i":
                    B[8].insert(A[j]);
                    break;
                case "j":
                    B[9].insert(A[j]);
                    break;
                case "k":
                    B[10].insert(A[j]);
                    break;
                case "l":
                    B[11].insert(A[j]);
                    break;
                case "m":
                    B[12].insert(A[j]);
                    break;
                case "n":
                    B[13].insert(A[j]);
                    break;
                case "o":
                    B[14].insert(A[j]);
                    break;
                case "p":
                    B[15].insert(A[j]);
                    break;
                case "q":
                    B[16].insert(A[j]);
                    break;
                case "r":
                    B[17].insert(A[j]);
                    break;
                case "s":
                    B[18].insert(A[j]);
                    break;
                case "t":
                    B[19].insert(A[j]);
                    break;
                case "u":
                    B[20].insert(A[j]);
                    break;
                case "v":
                    B[21].insert(A[j]);
                    break;
                case "w":
                    B[22].insert(A[j]);
                    break;
                case "x":
                    B[23].insert(A[j]);
                    break;
                case "y":
                    B[24].insert(A[j]);
                    break;
                case "z":
                    B[25].insert(A[j]);
                    break;
            }
        }

        //Compiles all buckets into the bucket B[0], maintaining sort property
        for (int j=1; j<26; j++) {
            //If a bucket has more than one element
            if (B[j].full()) {
                //Bucketsort over that individual buckets strings
                //Bucketsort returns a sorted bucket, so we merge the sorted B[j] bucket
                B[0] = B[0].merge(bucketSort(B[j].getStrings(), i+1));
            } else {
                //else sort the bucket and merge it
                B[0] = B[0].merge(B[j].sort());
            }
        }
        return B[0];


    }

    //Basic insertion sort algorithm on a String[] array
    public static String[] sort(String[] strings) {
        String temp;
        int size = 0;
        while (strings[size] != null) {
            if (size == 9) {
                size++;
                break;
            } else {
                size++;
            }
        }
        for (int i=1; i<size; i++) {
            for (int j = i; j>0; j--) {
                if (strings[j].compareTo(strings[j-1]) < 0) {
                    temp = strings[j];
                    strings[j] = strings[j-1];
                    strings[j-1] = temp;
                }
            }
        }
        return strings;
    }


    //MergeStrings is used to merge buckets.
    public static String[] mergeStrings(String[] A, String[] B) {
        int aSize = 0;
        int bSize = 0;

            for (int i=0; i<A.length; i++){
                if (A[aSize] != null) {
                    aSize++;
                }
            }

            for (int i=0; i<B.length; i++){
                if (B[bSize] != null) {
                    bSize++;
                }
            }

        String[] out = new String[aSize+bSize];
        int index = 0;

        for (int i=0; i<aSize; i++) {
            out[index] = A[i];
            index++;
        }
        for (int i=0; i<bSize; i++) {
            out[index] = B[i];
            index++;
        }

        return out;
    }

    public static void main(String[] args) {
        try {
            //Scan input
            Scanner input = new Scanner(new File(args[0]));
            String inString = input.nextLine();
            String[] in = inString.split(" ");

            //Perform sort
            Sort s = new Sort();
            SubBucket s1 = s.bucketSort(in, 0);

            String[] out = s1.getStrings();
            //Set up output
            File outFile = new File("output.txt");
            outFile.createNewFile();
            FileOutputStream outStream = new FileOutputStream(outFile);

            //Write output
            outStream.write(out[0].getBytes());
            for (int i=1; i<out.length; i++) {
                outStream.write(" ".concat(out[i]).getBytes());
            }
        } catch (IOException e) {
            System.out.println("File not found");
        }
    }
}
