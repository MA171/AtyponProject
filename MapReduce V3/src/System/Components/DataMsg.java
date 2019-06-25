package System.Components;

import java.io.Serializable;

public class DataMsg implements Serializable {
    private String data;

    public DataMsg(String message){
        data = message;
    }

    public String getData() {
        return data;
    }
}
