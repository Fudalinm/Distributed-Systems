//implementacja rozwiązania powinna cechować się dostępnością i tolerowaniem partycjonowania.
//musi odpowiadac na kazda prosbe nie zawsze aktualnymi wiadomosciami
//musi dzialac nawet jak czesc systemu ulegnie awarii


import java.awt.*;
import java.util.*;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK2;
import org.jgroups.protocols.pbcast.STABLE;
import org.jgroups.stack.ProtocolStack;

public class DistributedMap implements SimpleStringMap {
    //static variables
    private static final int initialSize = 100;
    private static String channelName = "Abrakadabra";

    //private variables
    private boolean isFirstMap;
    private boolean isInitialized;
    private Integer Id;
    private Hashtable<String,Integer> hashTable;
    private JChannel jChannel;


    public DistributedMap(int id,boolean isFirst){
        this.Id = new Integer(id);
        this.isFirstMap = isFirst;
        this.isInitialized = false;
        this.hashTable = new Hashtable<String, Integer>(initialSize);

        try {
            init();
        }catch (Exception e){
            System.out.println("Error in constructor");
        }

        jChannel.setReceiver(new ReceiverAdapter(){
            @Override
            public void viewAccepted(View view){
                super.viewAccepted(view);
                System.out.println(view.toString());
            }
            public void receive (Message msg){
                MyMessage m = new MyMessage(msg.getBuffer());
                processMessageFromJGroups(m);
            }
        });

        if(this.isFirstMap){
            this.isInitialized = true;
        }else{
            sendMessage(MessageType.INITIALIZATION_REQUEST,-1,"Init",null);
        }
    }


    public boolean containsKey(String key){
        return hashTable.containsKey(key);
    }

    public Integer get(String key){
        if(hashTable.containsKey(key)){
            return hashTable.get(key);
        }else{
            System.out.println("There is no value with such key in table");
            return 0;
        }
    }

    public void put(String key, Integer value){
        if(hashTable.containsKey(key)){
            System.out.println("You cannot put this to table the key is already in hashTable");
            return;
        }
        hashTable.put(key,value);
        //TODO: need to put to other tables with that group id


    }

    public Integer remove(String key){
        if(!hashTable.containsKey(key)){
            System.out.println("There is no such key in hashTable");
            return -1;
        }
        int x =hashTable.remove(key);
        //TODO: need to tell other tables to remove it from table


        return x;
    }

    private void processMessageFromJGroups(MyMessage message){
        switch (message.messageType){
            case INITIALIZATION_REQUEST:
                System.out.println("JGroups: Handling init request");
                if(this.isInitialized){
                    System.out.println("Sending init_response");
                    sendMessage(MessageType.INITIALIZATION_RESPONSE,-1,"NONE",this.hashTable);
                }else{
                    System.out.println("Cannot send init_response");
                }
                break;
            case INITIALIZATION_RESPONSE:
                System.out.println("JGroups: Handling init response");
                if(this.isInitialized){
                    System.out.println("I am initialized: ignore");
                }else{
                    System.out.println("Initialization complete");
                    this.isInitialized = true;
                    this.hashTable = message.h;
                }
                break;
            case GET: System.out.println("JGroups: Handling get"); break; //TODO: No need to do anything?
            case PUT: System.out.println("JGroups: Handling put"); this.hashTable.put(message.key,message.value); break;
            case REMOVE: System.out.println("JGroups: Handling remove"); this.hashTable.remove(message.key); break;
            case ERROR: System.out.println("JGroups: Error message has arrived"); break;
        }
    }

    private int sendMessage(MessageType mt,Integer value,String key,Hashtable h){
        MyMessage m = new MyMessage(mt,this.Id,key,value,h);
        Message mToSend = new Message(null,null,m);
        try{
            jChannel.send(mToSend);
        }catch (Exception e){
            System.out.println("Error while sending");
            return -1;
        }
        return 1;
    }

    private void init() throws Exception{

        System.setProperty("java.net.preferIPv4Stack","true");

        jChannel = new JChannel(false);
        ProtocolStack stack=new ProtocolStack();
        jChannel.setProtocolStack(stack);
        stack.addProtocol(new UDP())
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

        jChannel.connect(channelName);
    }


}