package System.Sockets;

import System.General.ProtocolMsg;

import java.net.*;
import java.util.ArrayList;

public interface SocketServer  {


    void listenNewConnection();
    void createNewConnections(int numOfConnections);
    int listen();
    void announcement(ProtocolMsg message);
    void sendObj(Object message);
    void sendObj(int idx,Object message);

    /*
    public void listenNewConnection(){
        int i = 0;
        Socket socket = null;
        while (i < numOfMaps) {
            try {
                socket = serverSocket.accept();
                if(socket != null) System.out.println("New Connection Number : " + i);
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new thread for a client
            socketThread[i] = new System.Sockets.SocketThread(socket);
            socketThread[i].start();
            i++;
        }
    }

    public void send(int idx, String line){
        socketThread[idx].sendMessage(line);
    }


    private void createNewMappers(int numOfConnections){
        try{
            ProcessBuilder p;
            String createConnectionCommand = "cd src && java Mapper "+numOfConnections+" ";
            p = new ProcessBuilder("cmd.exe","/c","cd src && javac Mapper.java");
            p.start();
            Thread.sleep(500);
            for(int i=0;i<numOfConnections;i++){
                p = new ProcessBuilder("cmd.exe","/c",createConnectionCommand + i);
                watch(p.start());
            }
        }
        catch (IOException ex){
            System.out.println("Error in Creating the Mapper process : " + ex);
        }
        catch (InterruptedException ex){
            System.out.println("Error in sleeping");
        }
    }
    private void createNewReducers(int numOfConnections){
        try{
            ProcessBuilder p;
            String createConnectionCommand = "cd src && java Reducer "+numOfConnections+" ";
            p = new ProcessBuilder("cmd.exe","/c","cd src && javac Reducer.java");
            p.start();
            Thread.sleep(500);
            for(int i=0;i<numOfConnections;i++){
                p = new ProcessBuilder("cmd.exe","/c",createConnectionCommand + i);
                watch(p.start());
            }
        }
        catch (IOException ex){
            System.out.println("Error in Creating the Mapper process : " + ex);
        }
        catch (InterruptedException ex){
            System.out.println("Error in sleeping");
        }
    }

    private static void watch(final Process process) {

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
*/
}