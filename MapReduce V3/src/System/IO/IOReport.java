package System.IO;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class IOReport {

    private BufferedWriter out;
    public IOReport(String name){
        try{
            out = new BufferedWriter(new FileWriter("Status/"+name+".txt"));
        }
        catch (IOException ex){
            System.out.println("Exception in IOReport");
            ex.printStackTrace();
        }
    }

    public void addMessage(String msg){
        try{
            out.write(msg + "\n");
        }
        catch (IOException ex){
            System.out.println("EEEE");
            ex.printStackTrace();
        }

    }

    public void flush(){
        try{
            out.flush();
        }
        catch (IOException ex){
            System.out.println("EEEE");
            ex.printStackTrace();
        }
    }

}
