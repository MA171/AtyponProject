package System;

import System.Components.ShuffleManager;
import System.Components.Splitter;
import System.General.ClassBuilder;
import System.General.Commands;
import System.General.ProtocolMsg;
import System.IO.IOWrite;
import System.Sockets.SocketServer;
import System.Sockets.SocketServerFactory;

import java.io.*;

public class MapReduceSystem {

    private BufferedReader in;
    private BufferedWriter out;
    private static int numOfMap,numOfReduce;
    private SocketServer socketServerMap,socketServerReduce;
    private boolean finished = false;
    private Splitter splitter;

    public MapReduceSystem(int numOfMap,int numOfReduce){
        this.numOfMap = numOfMap;
        this.numOfReduce = numOfReduce;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setInput(String fileName) throws IOException {
        if(fileName == null || fileName == "")throw new IllegalArgumentException("Input file name should not be Empty");
        in = new BufferedReader(new FileReader(fileName));
    }

    public void setOutput(String fileName) throws  IOException{
        if(fileName == null || fileName == "")throw new IllegalArgumentException("Output file name should not be Empty");
        out = new BufferedWriter(new FileWriter(fileName));
    }

    private void Write() {
        try{
            IOWrite.getInstance().startWriting();
            Thread.sleep(500);
            ProtocolMsg message = new ProtocolMsg(Commands.getDoneReducing());
            socketServerReduce.announcement(message);
            System.out.println("Announcement send to Reducers");
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finished = true;
    }

    private void Reduce(){
        try{
            if(SocketServerFactory.listenToReducerDone() == 1){
                System.out.println("\t\t .:: Start Writing ::. ");
                Write();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Shuffle() {
        try{
            if(SocketServerFactory.listenToReduceStart() == 1){
                ShuffleManager.getInstance().start();
                Thread.sleep(500);
                ProtocolMsg message = new ProtocolMsg(Commands.getStartReducing());
                socketServerMap.announcement(message);
                System.out.println("\t\t.::: Start Reducing :::. ");
                Reduce();
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void Map(){
            if(SocketServerFactory.listenToMapDone() == 1) {
                socketServerReduce = SocketServerFactory.getSocketServer(numOfMap, numOfReduce, 2);
                if (out == null) throw new IllegalArgumentException("Shuffler Can't work with empty files");
                System.out.println("\t\t .:::  Start Shuffling  :::.");
                Shuffle();
            }
        }

    private void Split() {
        try{
            System.out.println("\t\t.::: Start Splitting & Mapping :::.");
            splitter = new Splitter(socketServerMap,numOfMap,in);
            splitter.start();
            Map();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void start(){
        ShuffleManager.createManager(numOfReduce);
        IOWrite.createIO();
        ClassBuilder.buildClass("Mapper.java","Reducer.java");
        socketServerMap = SocketServerFactory.getSocketServer(numOfMap,numOfReduce,1);
        Split();
    }

}
