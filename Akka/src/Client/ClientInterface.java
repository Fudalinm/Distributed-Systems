package Client;


import Messages.ClientMessage;
import Messages.ClientMessageType;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class ClientInterface {

    public static void main(String[] args) throws Exception {
        // config
        File configFile = new File("remote_app.conf");
        Config config = ConfigFactory.parseFile(configFile);

        // create actor system & actors
        final ActorSystem system = ActorSystem.create("local_system", config);
        final ActorRef local = system.actorOf(Props.create(ClientActor.class), "local");



        System.out.println("Started. Commands: 'search' , 'stream', 'order'  , 'q'" +
                "\n command +' '+ 'book_title' ");

        // read line & send to actor
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = br.readLine();
            if (line.equals("q")) {
                break;
            }
            String[] split = line.split(" ");
            String command = split[0];
            String bookTitle = split[1];

            if(command.equals("search")){
                ClientMessage cm= new ClientMessage(ClientMessageType.SEARCH_REQUEST,bookTitle);
                local.tell(cm,null);
            } else if (command.equals("stream")){
                ClientMessage cm= new ClientMessage(ClientMessageType.STREAM_REQUEST,bookTitle);
                local.tell(cm,null);
            }else if (command.equals("order")) {
                ClientMessage cm= new ClientMessage(ClientMessageType.ORDER_REQUEST,bookTitle);
                local.tell(cm,null);
            } else {
                System.out.println("Unknow command: '" + command + "'");
            }
        }

        system.terminate();
    }
}
