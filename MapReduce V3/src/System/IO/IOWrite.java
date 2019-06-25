package System.IO;

import System.Sockets.*;
import System.Components.*;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class IOWrite extends IO {

    private static IOWrite instance;
    private static BufferedWriter out;
    private static Queue<ArrayList<KeyVal<String, Integer>>> writingQ = new LinkedList<>();
    private boolean writingStatus = false;

    public static IOWrite getInstance(){
        if(instance == null)instance = new IOWrite();
        return instance;
    }

    private IOWrite(){

    }

    public static void createIO(){
        try{
            out = new BufferedWriter(new FileWriter("output/output.txt"));
        }catch (IOException ex){
            System.out.println("Error in System.IO.IOWrite : " + ex);
        }
    }

    public void startWriting(){
        new Thread(() -> {
            writeData();
        }).start();
    }
    public void addData(ArrayList<KeyVal<String, Integer>> data){
        writingQ.add(data);
    }

    public void writeData(){
        ArrayList<KeyVal<String, Integer>> data;
        try{
            while(!writingQ.isEmpty() || SocketServerFactory.getReducersStatus() == 0){
                if(!writingQ.isEmpty()){
                    System.out.println("Writing data");
                    data = writingQ.poll();
                    if(data == null) System.out.println("Null in arraylist in write");
                    for(KeyVal item: data){
                        out.write(item.getKey() + "," + item.getValue() + "\n");
                    }
                }
            }
                System.out.println("Writing data done");
                writingStatus = true;
                out.flush();
                out.close();
        }catch (IOException ex){
            System.out.println(ex);
        }
    }

    public boolean getWritingStatus() {
        return writingStatus;
    }
}
