import java.util.Hashtable;

public class MyMessage {
    MessageType messageType;
    Integer senderID;
    String key;
    Integer value;
    Hashtable h;

    public MyMessage(MessageType messageType, int senderID , String key, Integer value, Hashtable h){
        this.messageType = messageType;
        this.senderID = senderID;
        this.key = key;
        this.value = value;
        this.h = h;
    }

    public MyMessage(){

        this.value = -20;
        this.key = "ERROR";
        this.messageType = MessageType.ERROR;
    }

    public MyMessage(byte[] buffer){
        this.messageType = buffer.

    }
}
