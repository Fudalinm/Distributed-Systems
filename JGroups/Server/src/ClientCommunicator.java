import Enums.ClientTypeMessages;
import Enums.JSONKeys;
import org.json.JSONObject;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
/**Consider respond message to client */

/**implements runable */
public class ClientCommunicator implements Runnable{
    //client communicator can pass messages to Map
    private int id;
    private int clientPort;
    private DistributedMap distributedMap;
    private DatagramSocket clientSocket;


    public ClientCommunicator(int id,int port,DistributedMap map){
        this.id = id;
        this.distributedMap = map;
        this.clientPort = port;
        init();
        //trzeba zrobic funkcje ktora bedzie sie runowac na tym obiekcie
    }


    private void init(){
        try{
            this.clientSocket = new DatagramSocket(this.clientPort);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("** ID: " + this.id + " ** " +"Error while creating client socket");
        }
    }

    /**Assuming that client message is passed as JSONObject */
    private JSONObject receiveMessage(){
        byte[] buffer = new byte[2048];
        DatagramPacket receivePacket = new DatagramPacket(buffer,buffer.length);
        try{
            this.clientSocket.receive(receivePacket);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("** ID: " + this.id + " ** " +"Error while receiving client packet");
        }
        String jsonString = new String(buffer,0,receivePacket.getLength());
        return new JSONObject(jsonString);
    }

    private void processMessage(JSONObject j){
        /**to check */
        Enums.ClientTypeMessages ctm = Enums.ClientTypeMessages.ClientTypeMessagesFromString((String)j.get(JSONKeys.MESSAGE_TYPE.getMessageType()));
        String key;
        Integer val;
        String response;
        switch (ctm){
            /**Maybe case init or something*/
            case VIEW:
                response = "** ID: " + this.id + " ** " +"Map looks like this: \n"+this.distributedMap.showMap();
                System.out.println(response);
                break;
            case REMOVE:
                key = (String) j.get(JSONKeys.KEY.getMessageType());
                val = this.distributedMap.remove(key);
                response = "** ID: " + this.id + " ** " +"You removed element:\n     key: " + key + "     value: " + val;
                System.out.println(response);
                break;
            case GET:
                key = (String) j.get(JSONKeys.KEY.getMessageType());
                val = this.distributedMap.get(key);
                response = "** ID: " + this.id + " ** " +"You wanted to get element:\n     key: " + key + "     value: " + val;
                System.out.println(response);
                break;
            case PUT:
                key = (String) j.get(JSONKeys.KEY.getMessageType());
                val = (Integer) j.get(JSONKeys.VALUE.getMessageType());
                this.distributedMap.put(key,val);
                response = "** ID: " + this.id + " ** " +"You wanted to put element:\n     key: " + key + "     value: " + val;
                System.out.println(response);
                break;
            default:
                response = "ERROR";
                break;
        }
        sendResponse(response);
    }

    //TODO: implement responses :)))
    public void sendResponse(String response){

    }

    @Override
    public void run(){
        while (true){
            JSONObject j = receiveMessage();
            processMessage(j);
        }
    }
}