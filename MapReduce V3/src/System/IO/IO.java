package System.IO;

import java.io.BufferedWriter;

public abstract class IO {

    private BufferedWriter out;
    private boolean writingStatus = false;

    public abstract void startWriting();
    public abstract void writeData();
    public abstract boolean getWritingStatus();


}
