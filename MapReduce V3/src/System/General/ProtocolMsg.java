package System.General;

import java.io.Serializable;

public class ProtocolMsg implements Serializable {

    private String message;

    public ProtocolMsg(String msg){
        this.message = msg;
    }

    public String getMessage() {
        return message;
    }
}
