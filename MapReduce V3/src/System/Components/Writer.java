package System.Components;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.TreeMap;

public class Writer {

    TreeMap<String, Integer> wordCount;
    ObjectOutputStream output;
    public Writer(ObjectOutputStream output, TreeMap<String, Integer> items){
        wordCount = items;
        this.output = output;
        System.out.println("Data Received Successfully in Writer , start Writing .");
    }

    public void sendResult(){
        ArrayList<KeyVal<String,Integer>> list = new ArrayList<>();
        wordCount.forEach((s, integer) -> {
            KeyVal<String,Integer> keyValue = new KeyVal<>(s,integer);
            list.add(keyValue);
        });

        try{
            output.writeObject(new ArrayListSocket(2,list));
        }catch (IOException ex){
            System.out.println("Error in sending the keyValue Obj" + ex.fillInStackTrace());
        }

    }



}
