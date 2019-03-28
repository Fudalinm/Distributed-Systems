import Enums.ClientTypeMessages;
import Enums.JSONKeys;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client {
    private String name;
    private int serverPort;
    private DatagramSocket serverSocket;
    private InetAddress ip;
    private JSONObject msgToSend;

    public Client(String name,int port,String ip){
        this.name = name;
        this.serverPort = port;
        try{
            this.serverSocket = new DatagramSocket();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Client " + this.name + " : Error while creating socket");
        }
        try{
            this.ip = InetAddress.getByName(ip);
        }catch (Exception e){
            System.out.println("Client " + this.name + " : Error while getting ip address");
        }
    }

    private void sendToServer(JSONObject j){
        byte[] msg = j.toString().getBytes();
        DatagramPacket packet = new DatagramPacket(msg,msg.length,this.ip,this.serverPort);
        try {
            this.serverSocket.send(packet);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Client " + this.name + " :" + "Error while sending packet to server");
        }

    }

    private void receiveFromServer(){
        byte[] buffer = new byte[2048];
        DatagramPacket receivePacket = new DatagramPacket(buffer,buffer.length);
        try{
            this.serverSocket.receive(receivePacket);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error while receiving message");
        }
        System.out.println(new String(buffer,0,receivePacket.getLength()));
    }

    private JSONObject takeParameters(){
        System.out.println("Client " + this.name + " :" +  "Select operation type R-remove P-put V-View G-get");
        String type = new Scanner(System.in).nextLine();
        String key;
        Integer value;
        JSONObject j = new JSONObject();
        if(type.equals("R") || type.equals("G") || type.equals("V") || type.equals("P") ){
            if(type.equals("R") || type.equals("G") || type.equals("P")){
                System.out.println("Client " + this.name + " :" + "Give Key parameter");
                key = new Scanner(System.in).nextLine();
            }else{key = "";}
            if(type.equals("P")){
                System.out.println("Client " + this.name + " :" + "Give Key parameter");
                value = new Scanner(System.in).nextInt();
            }else{value = -100;}

            switch (type){
                case "R":
                    j.put(JSONKeys.MESSAGE_TYPE.getMessageType(),ClientTypeMessages.REMOVE);
                    j.put(JSONKeys.KEY.getMessageType(),key);
                    break;
                case "P":
                    j.put(JSONKeys.MESSAGE_TYPE.getMessageType(),ClientTypeMessages.PUT);
                    j.put(JSONKeys.KEY.getMessageType(),key);
                    j.put(JSONKeys.VALUE.getMessageType(),value);
                    break;
                case "V":
                    j.put(JSONKeys.MESSAGE_TYPE.getMessageType(),ClientTypeMessages.VIEW);
                    break;
                case "G":
                    j.put(JSONKeys.MESSAGE_TYPE.getMessageType(),ClientTypeMessages.GET);
                    j.put(JSONKeys.KEY.getMessageType(),key);
                    break;
            }
        }else {
            System.out.println("Client " + this.name + " :" + "invalid message type");
        }
        return j;
    }


    public void run(){
        while(true){
            this.msgToSend = takeParameters();
            //jesli rozny od pustego
            if(!this.msgToSend.toString().equals("{}")){
                System.out.println("Client " + this.name + " : message to send\n" + this.msgToSend.toString());
                sendToServer(this.msgToSend);
                receiveFromServer();
            }
        }

    }
}
