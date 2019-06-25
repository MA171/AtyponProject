package System.Components;

import java.io.Serializable;
import java.util.ArrayList;

public class ArrayListSocket implements Serializable {
    private ArrayList<KeyVal<String,Integer>> ar = new ArrayList<>();
    private int id;

    public ArrayListSocket(int id, ArrayList<KeyVal<String,Integer>> arr){
        ar = arr;
        this.id = id;
    }

    public ArrayList<KeyVal<String, Integer>> getAr() {
        return ar;
    }

    public int getId() {
        return id;
    }
}
