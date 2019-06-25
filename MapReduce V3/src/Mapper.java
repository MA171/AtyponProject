import System.Components.DataMsg;
import System.Components.Shuffler;
import System.General.Commands;
import System.General.ProtocolMsg;
import System.IO.IOReport;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Mapper extends Application {

    private static TreeMap<String,Integer> wordCount = new TreeMap<>();
    private static ObjectOutputStream output;
    private static ObjectInputStream input;
    private static Socket socket;
    private static long startTime,finishTime,startMemory,finishMemory;
    private static Queue<String> dataQ = new LinkedList<>();
    private static boolean notDoneReading = true;
    private static int processID;
    private static IOReport report;

    // close = "-1",EndReading = "-2",startReducing = "-3"



    private static void run(){
        Pattern pattern = Pattern.compile("[a-zA-Z]+");
        Matcher matcher;
        while(!dataQ.isEmpty()){
            String line = dataQ.poll();
            matcher = pattern.matcher(line);
            while(matcher.find()){
                String word = matcher.group();
                if(!wordCount.containsKey(word))
                    wordCount.put(word,1);
                else
                    wordCount.put(word,wordCount.get(word)+1);
            }
        }

        return;
    }

    private static void printResult(){
        wordCount.forEach((s, integer) -> System.out.println(s + " -> "+ integer));
    }
    private static void getShuffleCommand(){
        report.addMessage("Waiting for shuffle command " + processID);
            try {
                Object line = null;
                while(true){
                    line = input.readObject();
                    if(line instanceof ProtocolMsg){
                        ProtocolMsg msg = (ProtocolMsg)line;
                        if(msg.getMessage().equalsIgnoreCase(Commands.getStartReducing())){
                            Shuffler sh = new Shuffler(output,wordCount);
                            sh.sendResult();
                            Close();
                        }
                    }
                }
            }catch (IOException ex){
                report.addMessage("Error in reading Shuffle command : " + ex.fillInStackTrace());
            }
            catch (Exception ex){
                report.addMessage("Class not Found in mapper : " + ex.fillInStackTrace());
            }

    }
    private static void Map(){
        report.addMessage("Start Mapping " + processID);
        while(notDoneReading || !dataQ.isEmpty()){
            if(!dataQ.isEmpty()){
                run();
            }
        }
        report.addMessage("Stop Mapping " + processID);
        //printResult();

    }
    private static void Read(){
        Object line = null;
        try{
            while(true){
                try{
                    line = input.readObject();
                }
                catch (ClassNotFoundException ex){
                    report.addMessage(ex.getMessage());
                    report.flush();
                }

                if(line instanceof ProtocolMsg){
                    ProtocolMsg msg = (ProtocolMsg)line;
                    if(msg.getMessage().equalsIgnoreCase(Commands.getEndReading()))break;
                }
                else if(line instanceof DataMsg) {
                    DataMsg ad = (DataMsg)line;
                    dataQ.add(ad.getData());
                }
            }
            report.addMessage("Done Reading " + processID);
            notDoneReading = false;
            if(!dataQ.isEmpty())run();
            Thread.sleep(1000);
            output.writeObject(new ProtocolMsg(Commands.getStartReducing()));
            getShuffleCommand();
        }
        catch (IOException ex){
            report.addMessage("Error in receiving data from socket in mapers : " + ex);
        }
        catch (InterruptedException ex){
            report.addMessage("Error in receiving data from socket in mapers : " + ex);
        }
    }
    private static void Close(){
        try{
            report.addMessage("Mapper " + processID + " will close now , Bye ");
            output.writeObject(new ProtocolMsg(Commands.getClose()));
            socket.close();
            finishTime = System.currentTimeMillis();
            finishMemory = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
            double duration = (finishTime-startTime)/1000;
            double memory = (finishMemory-startMemory)/(1024L*1024L);
            report.addMessage("Time Taken is : " + duration +"s");
            report.addMessage("Memory Taken is : " + memory + "MB");
            report.flush();
            System.exit(0);
        }
        catch (IOException ex){
            report.addMessage("Error in reading Shuffle command : " + ex.fillInStackTrace());
            report.flush();
        }
    }

    public static void main(String[] args){

        processID = Integer.parseInt(args[0]);
        report = new IOReport("Mapper"+processID);
        try{
            socket = new Socket("localhost",9000);
            OutputStream outputStream = socket.getOutputStream();
            output = new ObjectOutputStream(outputStream);
            InputStream inputStream = socket.getInputStream();
            input = new ObjectInputStream(inputStream);

            startTime = System.currentTimeMillis();
            startMemory = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

            new Thread(() -> Map()).start();
            new Thread(() -> Read()).start();

        }
        catch (Exception ex){
            report.addMessage(ex.getMessage());
        }

    }

    static TextArea txt;
    public void start(Stage primaryStage) throws Exception {
        txt = new TextArea();
        txt.appendText("Start Process - \n");
        Scene s = new Scene(txt);
        primaryStage.setScene(s);
        primaryStage.setTitle("Mapper");
        primaryStage.show();
    }
}
