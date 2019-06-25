package System.Sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.*;

public class SocketServerFactory {
    private static ServerSocket serverSocket;
    private static int PORT = 9000;
    private static int numOfMaps,numOfReduce;
    private static SocketServer socketServerMappers,socketServerReducer;

    public static SocketServer getSocketServer(int numOfM,int numOfR, int type){
        numOfReduce = numOfR;
        numOfMaps = numOfM;
        if(type == 1){
            try {
                if(serverSocket == null)serverSocket = new ServerSocket(PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            socketServerMappers = new SocketServerMap(numOfMaps,serverSocket);
            return socketServerMappers;
        }
        else if(type == 2) {
            socketServerReducer = new SocketServerReduce(numOfReduce,serverSocket);
            return socketServerReducer;
        }
        else throw new IllegalArgumentException("Invalid Socket Server Type");
    }
    public static SocketServer getSocketServerReducer(){return socketServerReducer;}
    public static SocketServer getSocketServerMappers(){return socketServerMappers;}

    public static int listenToMapStart(){
        ExecutorService service = Executors.newSingleThreadExecutor();
        try{
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    while(SocketThread.getDONE() < numOfMaps){
                        try{
                            Thread.sleep(100);
                        }
                        catch (InterruptedException ex){
                            ex.printStackTrace();
                        }
                    }
                }
            };
            Future<?> f = service.submit(r);
            f.get(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException ex){
            System.out.println("ERROR IN SLEEP");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            System.out.println("Error in starting the system , you need to restart");
            System.exit(0);
            e.printStackTrace();
        }

        System.out.println("----------------------------------------------------");
        System.out.println(".:: All Mapping Process Started ::. ");
        System.out.println("----------------------------------------------------");
        return 1;
    }

    public static int listenToMapDone(){
        ExecutorService service = Executors.newSingleThreadExecutor();
            try{
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        //int listen = 0;
                        while(SocketThread.getDONE() > 0){
                            try{
                                Thread.sleep(100);
                            }
                            catch (InterruptedException ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                };
                Future<?> f = service.submit(r);
                f.get(5, TimeUnit.SECONDS);
            }
            catch (InterruptedException ex){
                System.out.println("ERROR IN SLEEP");
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                System.out.println("Error in starting the system , you need to restart");
                System.exit(0);
                e.printStackTrace();
            }

        System.out.println("----------------------------------------------------");
        System.out.println(".:: Done Mapping Completely  , Start Reducing Process :.");
        System.out.println("----------------------------------------------------");
        return 1;
    }

    public static int listenToReduceStart(){
        ExecutorService service = Executors.newSingleThreadExecutor();
        try{
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    //int listen = 0;
                    while(SocketThread.getDONE() < numOfReduce){
                        try{
                            Thread.sleep(100);
                        }
                        catch (InterruptedException ex){
                            ex.printStackTrace();
                        }
                    }
                }
            };
            Future<?> f = service.submit(r);
            f.get(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException ex){
            System.out.println("ERROR IN SLEEP");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            System.out.println("Error in starting the reducers , restart the system");
            System.exit(0);
            e.printStackTrace();
        }

        System.out.println("---------------------------------------");
        System.out.println(" .:: All Reducers Process Started ::. ");
        System.out.println("---------------------------------------");
        return 1;
    }

    public static int listenToReducerDone(){
        ExecutorService service = Executors.newSingleThreadExecutor();
        try{
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    //int listen = 0;
                    while(SocketThread.getDONE() > 0){
                        try{
                            Thread.sleep(100);
                        }
                        catch (InterruptedException ex){
                            ex.printStackTrace();
                        }
                    }
                }
            };
            Future<?> f = service.submit(r);
            f.get(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException ex){
            System.out.println("ERROR IN SLEEP");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            System.out.println("Error in starting the system , you need to restart");
            System.exit(0);
            e.printStackTrace();
        }

        System.out.println("----------------------------------------------------");
        System.out.println(".:: Done Reducing Completely  , Start Writing Now :.");
        System.out.println("----------------------------------------------------");
        return 1;
    }


    public static int getMappersStatus(){
        return socketServerMappers.listen();
    }
    public static int getReducersStatus(){
        return socketServerReducer.listen();
    }



}
