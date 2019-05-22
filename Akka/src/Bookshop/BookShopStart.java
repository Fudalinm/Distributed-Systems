package Bookshop;

import Client.ClientActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

public class BookShopStart {
    public static void main(String[] args){
        // config
        File configFile = new File("remote_app2.conf");
        Config config = ConfigFactory.parseFile(configFile);


        // create actor system & actors
        final ActorSystem system = ActorSystem.create("bookshop", config);
        final ActorRef local = system.actorOf(Props.create(Bookshop.class), "local");

    }


}
