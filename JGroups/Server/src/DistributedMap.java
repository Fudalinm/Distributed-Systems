import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK2;
import org.jgroups.protocols.pbcast.STABLE;
import org.jgroups.stack.ProtocolStack;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import org.json.*;

public class DistributedMap implements SimpleStringMap {

    private static final String DEFAULT_MULTICAST_ADDRESS = "230.100.200.210";
    private static final String CHANNEL_NAME = "fudalinm_Cluster";
    private static boolean isFirst = true;

    private Map<String,Integer> hashTable = new HashMap<>();
    private ClientCommunicator clientCommunicator;
    private JChannel jChannel;


    //TODO: Tkink it is done but better mark :)
    public DistributedMap() throws Exception{
        initChannel();
        if(!isFirst){
            sendInitializationRequest();
        }
        clientCommunicator = new ClientCommunicator();
    }

    //TODO: is it all? o.0
    /**Actully don't know what this can do more ? o.0 */
    public boolean containsKey(String key){
        return this.hashTable.containsKey(key);
    }

    //TODO: is it all? o.0
    /**Actully don't know what this can do more ? o.0 */
    public Integer get(String key){
        if(this.hashTable.containsKey(key)){
            return this.hashTable.get(key);
        }else{
            System.out.println("No such key in map ERROR");
            return -1;
        }
    }

    /**Think it is done */
    public void put(String key, Integer value){
        this.hashTable.put(key,value);

        /**Sending messages to others in cluster about putting to map*/
        JSONObject j = new JSONObject();
        j.put(JSONKeys.MESSAGE_TYPE.getMessageType(), ServerTypeMessages.PUT_REQUEST);
        j.put(JSONKeys.KEY.getMessageType(),key);
        j.put(JSONKeys.VALUE.getMessageType(),value);
        sendClusterMessage(j);
    }

    /**Think it is done */
    public Integer remove(String key){
        int x = this.hashTable.remove(key);

        /**Sending messages to others in cluster about removing from map*/
        JSONObject j = new JSONObject();
        j.put(JSONKeys.MESSAGE_TYPE.getMessageType(), ServerTypeMessages.REMOVE_REQUEST);
        j.put(JSONKeys.KEY.getMessageType(),key);
        sendClusterMessage(j);
        return x;
    }

    /**Think it is done */
    private void initChannel() throws Exception{
        //stack.addProtocol(new UDP().setValue("mcast_group_addr", InetAddress.getByName(multicastAddr)))
        System.setProperty("java.net.preferIPv4Stack","true");

        jChannel = new JChannel(false);
        ProtocolStack stack=new ProtocolStack();
        jChannel.setProtocolStack(stack);
        stack.addProtocol(new UDP().setValue("mcast_group_addr", InetAddress.getByName(DEFAULT_MULTICAST_ADDRESS)))
                .addProtocol(new PING())
                .addProtocol(new MERGE3())
                .addProtocol(new FD_SOCK())
                .addProtocol(new FD_ALL()
                        .setValue("timeout", 12000)
                        .setValue("interval", 3000))
                .addProtocol(new VERIFY_SUSPECT())
                .addProtocol(new BARRIER())
                .addProtocol(new NAKACK2())
                .addProtocol(new UNICAST3())
                .addProtocol(new STABLE())
                .addProtocol(new GMS())
                .addProtocol(new UFC())
                .addProtocol(new MFC())
                .addProtocol(new FRAG2());
        stack.init();

        jChannel.setReceiver(new ReceiverAdapter(){
            @Override
            public void viewAccepted(View view){
                //System.out.println("** view: " + view);
                super.viewAccepted(view);
                System.out.println(view.toString());
            }
            public void receive (Message msg){
                JSONObject m = new JSONObject(new String(msg.getBuffer()));
                System.out.println(m.toString());
                processClusterMessages(m);
            }
        });

        jChannel.connect(CHANNEL_NAME,null,10000);

    }

    /**Think it is done */
    public void initHashTableFromResponse(JSONObject j){
        this.hashTable = (HashMap) j.getJSONObject(JSONKeys.HASH_TABLE.getMessageType()).toMap();
    }

    /**Think it is done */
    private void processClusterMessages(JSONObject j){
        ServerTypeMessages mt = (ServerTypeMessages) j.get(JSONKeys.MESSAGE_TYPE.getMessageType());
         switch (mt){
             case INITIALIZATION_RESPONSE:
                 initHashTableFromResponse(j);
                 break;
             case INITIALIZATION_REQUEST:
                 sendInitializationResponse();
                 break;
             case REMOVE_REQUEST:
                 this.hashTable.remove(j.get(JSONKeys.KEY.getMessageType()));
                 break;
             case PUT_REQUEST:
                String key = (String) j.get(JSONKeys.KEY.getMessageType());
                Integer value = (Integer) j.get(JSONKeys.VALUE.getMessageType());
                this.hashTable.put(key,value);
                break;
         }
    }

    /**Think it is done*/
    private void sendInitializationRequest(){
        JSONObject ob = new JSONObject();
        ob.put(JSONKeys.MESSAGE_TYPE.getMessageType(), ServerTypeMessages.INITIALIZATION_REQUEST);
        try{sendClusterMessage(ob);}catch (Exception e){System.out.println("Error while sending init request");}
    }

    /**Think it is done */
    private void sendInitializationResponse(){
        JSONObject ob = new JSONObject();
        ob.put(JSONKeys.MESSAGE_TYPE.getMessageType(), ServerTypeMessages.INITIALIZATION_RESPONSE);
        ob.put(JSONKeys.HASH_TABLE.getMessageType(),new JSONObject(this.hashTable));
        try{sendClusterMessage(ob);}catch (Exception e){System.out.println("Error while sending init response");}
    }

    /**Think it is done */
    private void sendClusterMessage( JSONObject j) {
        Message m = new Message();
        m.setBuffer(j.toString().getBytes());
        try{
            jChannel.send(m);
        }catch (Exception e){
            System.out.println("Error while sending to other in cluster");
        }

    }

}

