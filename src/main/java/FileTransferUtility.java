import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class FileTransferUtility {

    public static void sendfile(String dir,String fileName ) {
        try {
            File file=new File(dir);
            //if (!file.exists()) return;

            InetAddress serverIp=InetAddress.getByName("localhost");
            Socket tcpSocket = new Socket(serverIp, Constants.SERVER_TCP_PORT);

            // get the port number from the server that will receive data through UDP datagrams
            String action="1\nSEND REQUEST";
            int serverPort=getPortFromServer(tcpSocket,action,fileName,Constants.CLIENT_UDP_PORT);
            if (serverPort==0) {return;}


            // start sending the file
            PacketBoundedBufferMonitor bufferMonitor=new PacketBoundedBufferMonitor(Constants.MONITOR_BUFFER_SIZE);
            InetAddress senderIp=InetAddress.getByName("localhost");

            PacketSender packetSender=new PacketSender(bufferMonitor,senderIp,Constants.CLIENT_UDP_PORT,serverIp,serverPort);
            packetSender.start();

            FileReader fileReader=new FileReader(bufferMonitor,fileName,dir);
            fileReader.start();

            try {
                packetSender.join();
                fileReader.join();
            }
            catch (InterruptedException e) {}

        }catch(Exception e) {e.printStackTrace();}

    }



    public static int getPortFromServer(Socket tcpSocket,String action,String fileName,int udpPort) {
        int serverPort=0;
        try {
            Scanner inputSocket =  new Scanner(tcpSocket.getInputStream());
            PrintWriter outputSocket = new PrintWriter(tcpSocket.getOutputStream(), true);

            // send the HTTP packet
            System.out.println("action :"+action);
            String request=action+" # "+fileName+" # "+udpPort;
            outputSocket.println(request+Constants.CRLF+"STOP");
            System.out.println(Constants.CRLF+">> Request:"+request);

            // receive the response
            String line=inputSocket.nextLine();
            System.out.println("lineinputSocket :"+line);
            // get the port number of the server that will receive data for the file
            while(!line.equals("STOP")) {
                if (line.isEmpty()) {line=inputSocket.nextLine();continue;}
                if(line.startsWith("SEND REQUEST")){ //OG action var
                    // get the new port that is assigned by the server to receive data
                    System.out.println(">> Response:"+line+Constants.CRLF);
                    String [] items=line.split(":");
                    serverPort=Integer.parseInt(items[items.length-1]);
                    break;
                }
                line=inputSocket.nextLine();
                //new added lines
                inputSocket.close();
                outputSocket.close();
                //
            }
//            inputSocket.close();
//            outputSocket.close();
        }catch(Exception e) {e.printStackTrace();}
        return serverPort;
    }


    public static void receivefile(String fileName ) {
        try {

            ServerSocket serverSocket=null;
            try {
                serverSocket = new ServerSocket(Constants.SERVER_TCP_PORT);
            } catch (IOException ioEx) {
                System.out.println("\n>> Unable to set up port!");
                System.exit(1);
            }

            System.out.println("\r\n>> Ready to accept requests");
            // handle multiple client connections
            do {
                try {
                    // Wait for clients...
                    Socket client = serverSocket.accept();
                    System.out.println("\n>> New request is accepted."+Constants.CRLF);

                    Scanner inputSocket = new Scanner(client.getInputStream());
                    PrintWriter outputSocket = new PrintWriter(client.getOutputStream(), true);

                    // get action type from the received data
                    String line=inputSocket.nextLine();
                    String actionType = "";
                    int clientUDPPort=0;
                    while(!line.equals("STOP")) {
                        if (line.isEmpty()) {line=inputSocket.nextLine();continue;}
                        if(line.startsWith("SEND REQUEST")){
                            System.out.println(">> Request: "+line+Constants.CRLF);
                            actionType="SEND REQUEST";
                            clientUDPPort=Integer.parseInt(line.split("#")[2].strip());
                            break;
                        }
                        line=inputSocket.nextLine();
                    }

                    if (actionType.equals("SEND REQUEST")) {
                        receiveHandle(client,outputSocket,clientUDPPort);
                    }


                }catch(IOException io) {
                    System.out.println(">> Fail to listen to requests!");
                    System.exit(1);
                }

            } while (true);// end of while loop


        }catch(Exception e) {
            e.printStackTrace();
        }
    }


    public static void receiveHandle(Socket socket,PrintWriter outputSocket,int senderPort) {
        try {
            System.out.println("INSODE receiveHandle");
            // create the response with the port number which will receive data from clients through UDP
            String response="SEND REQUEST OK: receive data with the port:"+Constants.SERVER_UDP_PORT;
            System.out.println(">> Response: "+response+Constants.CRLF);

            // send the response
            outputSocket.println(response+Constants.CRLF+"STOP");
            outputSocket.close();


            PacketBoundedBufferMonitor bm=new PacketBoundedBufferMonitor(Constants.MONITOR_BUFFER_SIZE);
            InetAddress senderIp=socket.getInetAddress();// get the IP of the sender
            InetAddress receiverIp=InetAddress.getByName("localhost");

            receiveFile(bm,receiverIp,Constants.SERVER_UDP_PORT,senderIp, senderPort);// receive the file

        }catch(Exception e) {e.printStackTrace();}

    }

    public static void receiveFile(PacketBoundedBufferMonitor bm, InetAddress receiverIp,int receiverPort,InetAddress senderIp,int senderPort) {

        PacketReceiver packetReceiver=new PacketReceiver(bm,receiverIp,receiverPort,senderIp,senderPort);
        packetReceiver.start();

        FileWriter fileWriter=new FileWriter(bm);
        fileWriter.start();
        try {
            packetReceiver.join();
            fileWriter.join();
        }
        catch (InterruptedException e) {e.printStackTrace();}

    }
}
