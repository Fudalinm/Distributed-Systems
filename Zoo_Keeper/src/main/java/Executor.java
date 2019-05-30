import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import java.util.List;
import java.util.Scanner;

public class Executor implements Watcher , Runnable, AsyncCallback.StatCallback {
    /**We are only interested in node '/z' */
    String znode = "/z";
    ZooKeeper zooKeeper;
    /** Arguments of programm*/
    String hostPort;
    String[] pathToExec;
    Process program = null;
    Children2Callback children2Callback;
    public boolean fShowChildTree = false;

    public Executor(String hostPort,String[] pathToExec) throws Exception{
        this.hostPort = hostPort;
        this.pathToExec = pathToExec;
        this.zooKeeper = new ZooKeeper(hostPort, 3000, this);
        //needed to follow children
        this.children2Callback = new Children2Callback() {
            @Override
            public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
                if(children == null){return;}
                if(fShowChildTree){
                    System.out.format("Children(%d) of our node:\n",children.size());
                    for (String x : children){
                        System.out.format("     - %s\n",x);
                    }
                    fShowChildTree = false;
                }else {
                    System.out.format("Current number of children = %d\n",children.size());
                }
            }
        };
    }

    /** Callback to handle information from zookeeper server we are connected to*/
    public void process(WatchedEvent event) {
        System.out.println("process");
        String path = event.getPath(); //To determine if it is our node
        if(event.getType() == Event.EventType.None){
            //we know that stat of connection has changed
            //if(event.getState() == Event.KeeperState.SyncConnected)
                //no need to do anything
            if(event.getState() == Event.KeeperState.Expired || event.getState() == Event.KeeperState.AuthFailed || event.getState() == Event.KeeperState.Disconnected){
                System.out.format("Exiting program due to %s",event.getState());
                this.quit();
            }
        }else if (path != null && path.equals(this.znode)){
            if(event.getType() == Event.EventType.NodeCreated){
                System.out.println("Node created, what actully happend??");
            }
            if(event.getType() == Event.EventType.NodeDeleted){
                System.out.println("Node deleted. It means node deleted");
            }
            if(event.getType() == Event.EventType.NodeDataChanged){
                System.out.println("Node data changed, no need to do anything");
            }
        }
        //After processing we always need to call exist one more time AND call for children
        this.zooKeeper.exists(znode,true,this,null);
        this.zooKeeper.getChildren(this.znode,true,this.children2Callback,this.children2Callback);
    }

    /** This method is invoked after completing exists on ZooKeeper server */
//    rc - The return code or the result of the call.
//    path - The path that we passed to asynchronous calls.
//    ctx - Whatever context object that we passed to asynchronous calls.
//    stat - Stat object of the node on given path.
    public void processResult(int rc, String path, Object ctx, Stat stat){
        KeeperException.Code kec =  KeeperException.Code.get(rc);
        switch (kec){
            case OK :
                this.startProgram();
                break;
            case NONODE:
                this.stopProgram();
                break;
            case SESSIONEXPIRED:
            case NOAUTH:
                System.out.println("The node is dead!");
                this.quit();
                return;
             default:
                 System.out.println("Unknown error, retrying to check existance");
                 this.zooKeeper.exists(this.znode,true,this,null);
        }
    }

    void stopProgram(){
        /**Need to check if it started */
        System.out.println("Handling no node\n");
        if(this.program ==null) return;
        this.program.destroy();
        this.program = null;
    }
    void startProgram(){
        /**Need to run program :) */
        System.out.println("Handling is node\n");
        if(this.program != null) return;
        try{
            this.program = Runtime.getRuntime().exec(this.pathToExec);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void quit() {
        System.out.println("I'm quiting");
        stopProgram();
        try {
            this.zooKeeper.close();
        }catch (Exception e){
            System.out.println("failed to close zooKeeper");
        }
        System.exit(0);
    }

    public void run(){
        /** Check if node exist */
        /** This method will invoke  processResult(..) after completing on server*/
        this.zooKeeper.register(this);
        this.zooKeeper.exists(znode,true,this,null);
    }

    public static void main (String[] args) throws Exception{
        String[] pathToExec = new String[args.length-1];
        System.arraycopy(args,1,pathToExec,0,pathToExec.length);
        Executor e = new Executor(args[0],pathToExec);
        e.run();
        while (true){
            System.out.println("Type Y to see child tree | Q to quit\n");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if(line.equals("Y")){
                e.fShowChildTree = true;
                e.zooKeeper.getChildren(e.znode,true,e.children2Callback,e.children2Callback);
            }else if(line.equals("Q")) {
                e.quit();
                System.exit(1);
            }else {
                System.out.format("Unrecognized message");
            }
        }
    }
}