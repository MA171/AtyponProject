package System.Components;

import System.General.Commands;
import System.General.ProtocolMsg;
import System.Sockets.SocketServer;
import System.Sockets.SocketServerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class ShuffleManager {


    private static ShuffleManager instance;
    private static Queue<ArrayList<KeyVal<String,Integer>>> data = new LinkedList<>();
    private static int numOfReduce;
    private static SocketServer socketServerReducer;

    private ShuffleManager(int numOfReduce){
        this.numOfReduce = numOfReduce;
    }

    public static void createManager(int numOfReduce){
        if(instance == null){
            instance = new ShuffleManager(numOfReduce);
        }
    }

    public void start(){
        socketServerReducer = SocketServerFactory.getSocketServerReducer();
        new Thread(() -> {
            sendDataToReducers();
        }).start();
    }

    public static ShuffleManager getInstance(){
        return instance;
    }

    public void addItem(ArrayList<KeyVal<String,Integer>> item){
        if(item == null) throw new IllegalArgumentException("ITEM NULL FOUND IN SHUFFLER");
        data.add(item);
    }

    private void getNextItem(){
        if(data.isEmpty())return;
        ArrayList<KeyVal<String,Integer>> val = data.poll();
        if(socketServerReducer == null) System.out.println("SOCKET SERVER IS NULL IN SHUFFLER");
        if(val == null){ System.out.println("NULL VALUE IN ARRAYLIST IN SHUFFLER");}
        for(KeyVal<String,Integer> item : val){
            socketServerReducer.sendObj(getIdToSend(item.getKey()),item);
        }
    }

    private void sendDataToReducers(){
        while(SocketServerFactory.getMappersStatus() == 0 || !data.isEmpty()){
            if(!data.isEmpty()){
                    getNextItem();
            }
        }
        socketServerReducer.sendObj(new ProtocolMsg(Commands.getEndReading()));
        System.out.println(".:::-- All Data Sent to Reducers Successfully --:::.");
    }

    private static int getIdToSend(String val){
        int nums = (int)Math.ceil(25.0/numOfReduce);
        val = val.toUpperCase();
        int res = (val.charAt(0)-'A')/nums;
        return res%numOfReduce;
    }

}
