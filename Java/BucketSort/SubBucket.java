import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Garett on 10/15/2017.
 */
public class SubBucket {
    private ArrayList<String> strings;
    private String s;
    private int stringIndex;
    private int size = 0;

    public SubBucket(int type, int i) {
        if (type == 1) {
            s = "a";
        } else  if (type==2) {
            s = "b";
        } else  if (type==3) {
            s = "c";
        } else  if (type==4) {
            s = "d";
        } else  if (type==5) {
            s = "e";
        } else  if (type==6) {
            s = "f";
        } else  if (type==7) {
            s = "g";
        } else  if (type==8) {
            s = "h";
        } else  if (type==9) {
            s = "i";
        } else  if (type==10) {
            s = "j";
        } else  if (type==11) {
            s = "k";
        } else  if (type==12) {
            s = "l";
        } else  if (type==13) {
            s = "m";
        } else  if (type==14) {
            s = "n";
        } else  if (type==15) {
            s = "o";
        } else  if (type==16) {
            s = "p";
        } else  if (type==17) {
            s = "q";
        } else  if (type==18) {
            s = "r";
        } else  if (type==19) {
            s = "s";
        } else  if (type==20) {
            s = "t";
        } else  if (type==21) {
            s = "u";
        } else  if (type==22) {
            s = "v";
        } else  if (type==23) {
            s = "w";
        } else  if (type==24) {
            s = "x";
        } else  if (type==25) {
            s = "y";
        } else  if (type==26) {
            s = "z";
        }
        stringIndex = i;
        strings  = new ArrayList<String>();
    }
    public String[] getStrings()
    {
        String[] out = new String[size];
        out = strings.toArray(out);
        return out;
    }

    public void insert(String s) {
        strings.add(s);
    }

    //Function to merge 2 buckets into one
    public SubBucket merge(SubBucket A) {
        SubBucket out = new SubBucket(1, stringIndex);
        if (getSize() == 0) {
            return out;
        }
        for (int i = 0; i<strings.size(); i++) {
            out.insert(strings.get(i));
        }
        if (A.getSize() == 0) {
            return out;
        }
        for (int i = 0; i<A.getSize(); i++) {
            out.insert(A.getStrings()[i]);
        }
        return out;
    }

    public int getSize() {
        return strings.size();
    }

    //Sorts the current bucket using insertion sort. Bucket should never have more than 10 charactersa
    public SubBucket sort() {
        String temp;

        for (int i=1; i<strings.size(); i++) {
            for (int j = i; j>0; j--) {
                if (strings.get(j).compareTo(strings.get(j-1)) < 0) {
                    temp = strings.get(j);
                    strings.set(j, strings.get(j-1));

                    strings.set(j-1, strings.get(j));
                }
            }
        }
        return this;
    }
    public boolean full() {
        if (strings.size() > 10) {
            return true;
        }
        return false;
    }
}
