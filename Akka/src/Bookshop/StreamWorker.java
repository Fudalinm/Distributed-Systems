package Bookshop;
import Messages.MessageToService;
import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import scala.concurrent.duration.FiniteDuration;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class StreamWorker extends AbstractActor {
    BufferedReader br;
    private class BookIterable implements Iterable<String> {
        String line = null;

        public BookIterable() {
            try {

                line = br.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public Iterator<String> iterator() { return new BookIterator();}

        private class BookIterator implements Iterator<String>{
            @Override
            public boolean hasNext() { return line != null; }

            @Override
            public String next() {
                String current_line = line;
                try{
                    line = br.readLine();
                } catch (Exception e){
                    e.printStackTrace();
                }
                return current_line;
            }
        }
    }


    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    FileReader fileReader;
    String bookPath;
    public StreamWorker(String bookPart){
        this.bookPath = bookPart;
    }

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(MessageToService.class, m -> {
                 this.fileReader = new FileReader(this.bookPath+ "/" + m.bookTitle);
                 this.br = new BufferedReader(fileReader);
                 stream(m.actorRef);
                })
                .matchAny(o -> log.info("Bookshop: received unknown message"))
                .build();
    }

    private void stream(ActorRef target){
        Materializer materializer = ActorMaterializer.create(context().system());
        Source <String, NotUsed> source = Source.from(new BookIterable());
        source.throttle(1,new FiniteDuration(1000, TimeUnit.MILLISECONDS),1, ThrottleMode.shaping())
                .to(Sink.actorRef(target,""))
                .run(materializer);
    }



    @Override
    public void preStart(){
        log.info("Stream worker start");
    }
}
