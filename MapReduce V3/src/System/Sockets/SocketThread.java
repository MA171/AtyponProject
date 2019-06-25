package System.Sockets;

import System.Components.ArrayListSocket;
import System.Components.ShuffleManager;
import System.General.Commands;
import System.General.ProtocolMsg;
import System.IO.IOWrite;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class SocketThread extends Thread implements Serializable{
    protected Socket socket;
    private InputStream inp = null;
    private OutputStream outp = null;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;
    private String name;
    private static int NUM = 0;
    private static String workginNow = "Map";

    public SocketThread(Socket clientSocket) {
        this.socket = clientSocket;
        name = NUM++ + "";
    }

    public void run() {
        try {
            inp = socket.getInputStream();
            in = new ObjectInputStream(inp);
            outp = socket.getOutputStream();
            out = new ObjectOutputStream(outp);
        } catch (IOException e) {
            System.out.println("Error in Input or Output Stream in Socket Thread");
            return;
        }
        Object line;
        while (true) {
            try {
                line = in.readObject();
                if(line instanceof ProtocolMsg){
                    ProtocolMsg s = (ProtocolMsg)line;
                    if(s.getMessage().equalsIgnoreCase(Commands.getStartReducing())){
                        System.out.println("Start Reducing signal received");
                        NUM--;
                    }
                    if(s.getMessage().equalsIgnoreCase(Commands.getDoneReducing())){
                        System.out.println("Done Reducing Signal Received");
                        NUM--;
                    }
                    if (s.getMessage().equalsIgnoreCase(Commands.getClose())) {
                        socket.close();
                        if(NUM == 0)workginNow = "Reduce";
                        System.out.println(workginNow + " Process - " + name + " - Finished and stopped");
                        return;
                    }
                }
                else if(line instanceof ArrayListSocket){
                    ArrayListSocket val = (ArrayListSocket)line;
                    if(val.getId() == 2){
                        //System.out.println("From Reducer : " + val);
                        IOWrite.getInstance().addData(val.getAr());
                    }
                    else{
                        //System.out.println("From Mapper : " + val);
                        ShuffleManager.getInstance().addItem(val.getAr());
                    }
                }
            }
            catch (SocketException ex){
                ex.printStackTrace();
                return;
            }
            catch (IOException e) {
                e.printStackTrace();
                return;
            }
            catch (ClassNotFoundException e) {
                System.out.println("Class Not found in socketTread : ");
                e.printStackTrace();
                return;
            }
        }
    }

    public void sendMessage(String message){
        try{
            out.writeUTF(message);
            out.flush();
        }
        catch (IOException ex){
            System.out.println("Error in sending message to socket : " + ex);
        }

    }

    public void sendObj(Object message) {
        try{
            out.writeObject(message);
        }catch (IOException ex){
            System.out.println("Error in writing object : ");ex.printStackTrace();
        }
    }


    public static int getDONE(){return NUM;}

}