package Bookshop;

import Messages.MessageToService;
import Messages.ResponseFromDatabaseWorker;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

public class DatabaseWorker extends AbstractActor {
    public String databasePath;
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private FileReader fileReader;


    public DatabaseWorker(String databasePath){
        this.databasePath = databasePath;
    }

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(MessageToService.class, m -> {
                    log.info("DatabaseWorker" + this.databasePath  + " : received request for title: " + m.bookTitle);
                    //zakladamy ze pola sa rozdzielone w bazie nastepujaco: tytul;cena
                    String line = "";
                    BufferedReader br = new BufferedReader(fileReader);
                   while ( (line = br.readLine()) != null){
                       String[] split = line.split(";");
                       String bookTitle = split[0];
                       Integer price = new Integer(split[1]);
                       if (m.bookTitle.equals(bookTitle)){
                           log.info("DatabaseWorker" + this.databasePath  + " : found a book with title: " + m.bookTitle + " and price ; " +  price);
                            getSender().tell( new ResponseFromDatabaseWorker(m,price) ,getSelf());
                           return;
                       }
                   }
                   //nothing found we can send unsuccessful result
                    log.info("DatabaseWorker" + this.databasePath  + " : couldnt find a book with title: " + m.bookTitle + "responding to parent" );
                    getSender().tell( new ResponseFromDatabaseWorker(m,-1)  ,getSelf());
                })
                .matchAny(o -> log.info("DatabaseWorker" + this.databasePath  + " : received unknown message"))
                .build();
    }




    @Override
    public void preStart() throws Exception{
        System.out.println(this.databasePath);
        this.fileReader = new FileReader(this.databasePath);
    }

}
