package System.Sockets;

import System.Components.DataMsg;
import System.General.ClassBuilder;
import System.General.ProtocolMsg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SocketServerMap implements SocketServer {

    private static SocketThread socketThread[];
    private static ServerSocket serverSocket = null;
    private int numOfMaps;

    public SocketServerMap(int numOfMaps, ServerSocket server) {
        socketThread = new SocketThread[numOfMaps];
        this.numOfMaps = numOfMaps;
        serverSocket = server;
        new Thread(() ->createNewConnections(numOfMaps)).start();
        listenNewConnection();
    }

    public void listenNewConnection(){
        int i = 0;
        Socket socket = null;
        while (i < numOfMaps) {
            try {
                socket = serverSocket.accept();
                if(socket != null) System.out.println("New Mapper Number : " + i);
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new thread for a client
            socketThread[i] = new SocketThread(socket);
            socketThread[i].start();
            i++;
        }
    }

    public void createNewConnections(int numOfConnections){
        try{
            ProcessBuilder p;
            for(int i=0;i<numOfConnections;i++){
                System.out.println("Creating mapper number " + (i+1));
                Process process = ClassBuilder.runClass("Mapper " + (i+1));
                watch(process);
                Thread.sleep(250);
            }
            //ClassBuilder.removeClass("Mapper");
        }
        catch (InterruptedException ex){
            System.out.println("Error in sleeping");
        }
    }

    private void watch(final Process process) {

        new Thread(() -> {
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            try {
                while ((line = input.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    public int listen() {
        for(int i=0;i<numOfMaps;i++){
            if(socketThread[i].isAlive())return 0;
        }
        //System.out.println("-: All System.Sockets Dead now :- ");
        return 1;
    }

    public void announcement(ProtocolMsg message) {
        for(int i=0;i<numOfMaps;i++){
            socketThread[i].sendObj(message);
        }
    }
    public void sendObj(Object message){
            for(int i=0;i<numOfMaps;i++){
                socketThread[i].sendObj(message);
            }
    }
    public void sendObj(int idx, Object message) {
        socketThread[idx].sendObj(message);
    }

}
