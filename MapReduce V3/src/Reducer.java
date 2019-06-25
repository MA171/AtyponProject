import System.Components.KeyVal;
import System.Components.Writer;
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


public class Reducer extends Application {

    private static TreeMap<String,Integer> wordCount = new TreeMap<>();
    private static ObjectOutputStream output;
    private static ObjectInputStream input;
    private static Socket socket;
    private static Queue<KeyVal<String,Integer>> dataQ = new LinkedList<>();
    private static boolean notDoneReading = true;
    private static int processID;
    private static IOReport report;
    private static long startTime,finishTime,startMemory,finishMemory;

    // close = "-1" , EndReading = "-2" , doneReducin = "-4";


    public static void run(){
        while(!dataQ.isEmpty()){
            KeyVal<String,Integer> line = dataQ.poll();
                if(!wordCount.containsKey(line.getKey()))
                    wordCount.put(line.getKey(),line.getValue());
                else
                    wordCount.put(line.getKey(),wordCount.get(line.getKey())+line.getValue());
        }
        return;
    }

    public static void printResult(){
        wordCount.forEach((s, integer) -> System.out.println(s + " -> "+ integer)
        );
    }
    public static void getWritingCommand(){
        report.addMessage("Waiting for Writing command " + processID);
        report.flush();
        try {
            Object line = null;
            while(true){
                line = input.readObject();
                if(line instanceof ProtocolMsg){
                    ProtocolMsg msg = (ProtocolMsg)line;
                    if(msg.getMessage().equalsIgnoreCase(Commands.getDoneReducing())){
                        report.addMessage("Writing command Received" + processID);
                        report.flush();
                        Writer writer = new Writer(output,wordCount);
                        writer.sendResult();
                        Close();
                    }

                }
            }
        }catch (IOException ex){
            report.addMessage("Error in reading Shuffle command : " + ex.fillInStackTrace());
            report.flush();
        }
        catch (Exception ex){
            report.addMessage("Class not Found in mapper : " + ex.fillInStackTrace());
            report.flush();
        }

    }
    private static void Reduce(){
        report.addMessage("Start Reducing " + processID);
        report.flush();
        while(notDoneReading || !dataQ.isEmpty()){
            if(!dataQ.isEmpty()){
                run();
            }
        }
        report.addMessage("Stop Reducing " + processID);
    }
    private static void Read(){
        report.addMessage("Start reading from Mappers " + processID );
        report.flush();
        try{
            while(true){
                Object read = input.readObject();
                if(read instanceof KeyVal){
                    KeyVal<String,Integer> line = (KeyVal<String,Integer>)read;
                    dataQ.add(line);
                }
                else if(read instanceof ProtocolMsg){
                    ProtocolMsg line = (ProtocolMsg) read;
                    if(line.getMessage().equalsIgnoreCase(Commands.getEndReading()))break;
                    if(line.getMessage().equalsIgnoreCase(Commands.getClose())){
                        socket.close();
                        return;
                    }
                }

            }
            report.addMessage("Done Reading " + processID);
            report.flush();
            notDoneReading = false;
            if(!dataQ.isEmpty())run();
            report.addMessage("Reducer " + processID + " Done Working");
            Thread.sleep(1000);
            output.writeObject(new ProtocolMsg(Commands.getDoneReducing()));
            getWritingCommand();
        }
        catch (Exception ex){
            report.addMessage("Error in receiving data in reducer " + processID + " : " + ex);
            report.flush();
        }
    }
    private static void Close(){
        try{
            report.addMessage("Reducer " + processID + " will close now");
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
        report = new IOReport("Reducer"+processID);

        try{
            socket = new Socket("localhost",9000);
            OutputStream outputStream = socket.getOutputStream();
            output = new ObjectOutputStream(outputStream);
            InputStream inputStream = socket.getInputStream();
            input = new ObjectInputStream(inputStream);
            report.addMessage("Start Reducing Process");
            report.flush();
            Thread.sleep(500);

            new Thread(() -> Reduce()).start();

            new Thread(() -> Read()).start();
            //startMapping.start();

        }
        catch (Exception ex){
            report.addMessage(ex.getMessage());
            report.flush();

        }

    }






    static TextArea txt;
    public void start(Stage primaryStage) throws Exception {
        txt = new TextArea();
        txt.appendText("Start Process - \n");
        Scene s = new Scene(txt);
        primaryStage.setScene(s);
        primaryStage.setTitle("Reducer");
        primaryStage.show();
    }
}
