package System.General;

import java.beans.IntrospectionException;
import java.io.IOException;

public class ClassBuilder {
    private static ProcessBuilder p;

    public static void buildClass(String... names){
        try{
            String command = "";
            for(int i=0;i< names.length-1;i++){
                command += "javac " + names[i] + " && ";
            }
            command += "javac " + names[names.length-1];
            p = new ProcessBuilder("cmd.exe","/c","cd src && " + command);
            CheckUtils.watchProcess(p.start());
            Thread.sleep(1000);
        }
        catch (IOException ex){
            System.out.println("Exception in ClassBuilder ");
            ex.printStackTrace();
        }
        catch (InterruptedException ex){
            System.out.println("Exception in ClassBuilder");
            ex.printStackTrace();
        }

    }

    public static Process runClass(String name){
        try{
            p = new ProcessBuilder("cmd.exe","/c","cd src && java " + name);
            return p.start();
        }
        catch (IOException ex){
            System.out.println("Exception in ClassBuilder ");
            ex.printStackTrace();
        }
        return null;
    }

    public static void removeClass(String name){
        try{
            p = new ProcessBuilder("cmd.exe","/c","cd src && del " + name + ".class");
            p.start();
        }
        catch (IOException ex){
            System.out.println("Exception in removeClass : ");
            ex.printStackTrace();
        }

    }


}
