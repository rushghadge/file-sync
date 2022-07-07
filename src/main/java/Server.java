import org.apache.log4j.Logger;
import utilities.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Server implements Runnable{
    Socket client;
    Map<String, Integer> clientPortMap = new HashMap();
    public static final Logger logger = Log.getLogger("Server");
    public static String requestType;
    public static List<Buffer> clinetBuffer = new ArrayList<>();
    public static int clientNo;
    public static String localDir;
    public static String backupDir;



    public Server(Socket client){
        this.client = client;
    }

    @Override
    public void run(){
        logger.info("new request from "+ this.client.getInetAddress() + "/" + this.client.getPort() + " has been accepted");
        // initiate the Scanner and PrintWriter objects
        Scanner receiver = null;
        PrintWriter sender = null;
        try {
            receiver = new Scanner(this.client.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            sender = new PrintWriter(this.client.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // First thing to do is get the client Number
        String line = receiver.nextLine();
        clientNo = Integer.parseInt(line);
        clientPortMap.put(line, this.client.getPort());
        logger.info("Welcome Client"+line);
        line = receiver.nextLine();
        requestType = line;
      if(requestType.contains("#")){
          requestType=  requestType.split("#",2)[0].trim();
         //
      }
        System.out.println("requestType "+requestType);
      //  line = receiver.nextLine();
//        while(!line.equals("STOP")){
//            System.out.println("in elif STOP ");
//            line = receiver.nextLine();
//        }

        if(requestType.equals("POLLING")){
            // Look into client(id) buffer
            logger.info("in elif POLLING ");

            assert sender != null;
            sender.println(clinetBuffer.get(clientNo).checkUpdate());
        }else if(requestType.equals("STARTING")){
            logger.info("in elif STARTING ");

            clinetBuffer.set(clientNo, new Buffer(clientNo));
        }else if(requestType.equals("SEND REQUEST")){
            logger.info("in elif REQUEST ");
            FileTransferUtility.receiveHandle(this.client,sender,Constants.CLIENT_UDP_PORT);
        }
////////recieve file for udp
//        System.out.println("file comp in transfer udp");
//        try {
//            FileComparison.receiveFile();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }


        ///////////////
        logger.info("All requests from "+ this.client.getInetAddress() + "/" + this.client.getPort()+" have been completed");
    }

    public static void main(String[] args){
        ServerSocket socket=null;
        Socket client=null;
        clinetBuffer.add(new Buffer(0));
        clinetBuffer.add(new Buffer(1));
        clinetBuffer.add(new Buffer(2));
        clinetBuffer.add(new Buffer(3));
        // localDir
        localDir = Constants.LOCAL_DIRS[0][0];
        backupDir = Constants.LOCAL_DIRS[1][0];
        // First cleanup the clients' local and backup dirs.
        Helper.deleteAllFiles(logger, localDir, backupDir);

        try{
            socket = new ServerSocket(Constants.SERVER_TCP_PORT);
        }catch(IOException exc){
            logger.error("Unable to setup port and start the server...");
            System.exit(1);
        }
        logger.info("Server is listening to port " + Constants.SERVER_TCP_PORT);
        // Create thread pool
        Thread[] clientThreads = new Thread [10];
        int idx, i;
        while (true){
            idx = -1;
            // Check if any thread slot is empty
            for(i=0; i<10; i++){
                if (clientThreads[i]==null || !clientThreads[i].isAlive()) {
                    idx = i;
                    break;
                }
            }
            // If no thread slot is empty then wait for 10 seconds and continue
            if(idx==-1){
                try{
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            // When there is at least one empty thread slot.
            // Go ahead and Accept client request
            try {
                client = socket.accept();
            } catch (IOException exc){
                logger.error("Failed to listen to requests!");
                System.exit(1);
            }
            clientThreads[idx] = new Thread(new Server(client));
            clientThreads[idx].start();
        }
    }
}

