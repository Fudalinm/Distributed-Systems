import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
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
        this();
        MyMessage m;
        try{
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer));
            m = (MyMessage) in.readObject();
            in.close();
            this.messageType = m.messageType;
            this.h = m.h;
            this.key = m.key;
            this.senderID = m.senderID;
            this.value = m.value;

        }catch (Exception e){
            System.out.println("Error while obtaining MyMessage");
        }
    }
}
