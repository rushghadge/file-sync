    import java.io.File;
    import java.io.FileInputStream;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.net.*;
    import java.nio.ByteBuffer;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.util.Arrays;

    public class FileComparison {


        public static int compareFileByByte(Path File_One, Path File_Two) throws Exception {
            int idx = 0;int res_idx=0;
            int j=0;
            byte[] resChunk=new byte[2000000];
            byte[][] a= new byte[2000][2000];
            try {
                long fileOne_size = Files.size(File_One);
                long fileTwo_size = Files.size(File_Two);
                if (fileOne_size < Constants.blockSize || fileTwo_size < Constants.blockSize) {
                    return idx;
                }
                // Compare byte-by-byte
                try (FileInputStream first = new FileInputStream(String.valueOf(File_One));
                     FileInputStream second = new FileInputStream(String.valueOf(File_Two)))
                {
                    byte[] chunk1, chunk2  ;

                    do {
                        chunk1 = first.readNBytes(Constants.blockSize);
                        ByteBuffer buffer = ByteBuffer.wrap(resChunk);
                        buffer.position(0);
                        chunk2 = second.readNBytes(Constants.blockSize);
                        System.out.println(chunk1.length +" "+ chunk2.length +" "+ idx);
                        if(!Arrays.equals(chunk1, chunk2)){
                            System.out.println("xgange  "+idx);
                            res_idx= idx;
                        }
                        System.out.println("ridx "+res_idx+"idx "+idx);
                        if(res_idx!=0 && chunk1.length>0){
                            System.out.println("ifffffffffff    ridx "+res_idx+"idx "+idx);
                            resChunk=chunk1;
                            a[j]=resChunk;
                            buffer.put(chunk1);
                            System.out.println("a[j]  "+a[j][0]+" j:"+j);
                            j++; // res will get j chunks

                        }

                        idx++;

                    }while (chunk1.length>0 && chunk2.length>0);
                    System.out.println("ridx "+res_idx);
                    for(int i=0;i<16;i++){
                        for(int k=0;k<16;k++) {
                        }
                    }
                    System.out.println("j "+j);
                    //newly

                    System.out.println(idx);
////////send j,a , idx-1   over udp

//sendFile(a,idx-1,j);
                   // receiveFile();
                    modifiedFileWrite(File_Two,idx-1,resChunk,j,a );
                }catch (Exception e){
                    e.printStackTrace();
                    return 0;
                }
                return -1;
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }


        }
public static void sendFile(byte[][] a,int len, int blocksChanged) throws IOException {
    DatagramSocket dsoc=new DatagramSocket(2001);

    byte b[]=new byte[1024];
    b=TwoDtoOneD.bidiToMono(a);


    dsoc.send(new DatagramPacket(a[1],len, InetAddress.getLocalHost(),2001));
    dsoc.close();

    System.out.println("sent");

}



        public static void receiveFile() throws IOException {


            DatagramSocket socket = new DatagramSocket(new InetSocketAddress(2001));
            byte[] message = new byte[20000];
            DatagramPacket packet = new DatagramPacket(message, message.length);
            socket.receive(packet);
            System.out.println("REceived");
            System.out.println(new String(packet.getData(), packet.getOffset(), packet.getLength()));
        }


        public static void modifiedFileWrite (Path ServerFile,int length, byte[] resChunk, int changedBlocks,byte[][] a) throws Exception {
            int idx = 0;    byte[][] serverArr= new byte[50000][50000];
        int loopserver=0;
            System.out.println("res6 " + resChunk[6]);
            System.out.println(resChunk[7]);

    int diff = length-changedBlocks;

            byte[] serverFilechunk1;

            System.out.println("FILE EDITED!!");

            try (

                    FileInputStream second = new FileInputStream(String.valueOf(ServerFile))
            ) {
                byte[] chunk1;

                do {
                    chunk1 = second.readNBytes(Constants.blockSize);
                    System.out.println(chunk1.length +" ");
                    serverArr[loopserver]=chunk1;
                    System.out.println("serverArr[loopserver] "+serverArr[loopserver] +"loopserver :"+loopserver);
                    loopserver++;


                    System.out.println("loopserver " + loopserver);


                } while (chunk1.length > 0 );
                int x=0;
                for(int i = diff;i<length;i++){
                        serverArr[i] = a[x];
                        x++;
                }
//to right
                FileOutputStream serverFile = new FileOutputStream(String.valueOf(ServerFile));
                for(int i = 0;i<length;i++){
                    serverFile.write(serverArr[i]);
                    System.out.println(" WRITTEN TO FILE HAHA " );
                }


            }
        }



//
//        public static void modifiedFileWrite (Path ServerFile,int length, byte[] resChunk, int changedBlocks,byte[][] a) throws Exception {
//            int idx = 0;    byte[][] serverArr= new byte[50000][50000];
//            int loopserver=0;
//            System.out.println("res6 " + resChunk[6]);
//            System.out.println(resChunk[7]);
//
//            int diff = length-changedBlocks;
//
//            byte[] serverFilechunk1;
//
//            System.out.println("FILE EDITED!!");
//
//            try (
//
//                    FileInputStream second = new FileInputStream(String.valueOf(ServerFile))
//            ) {
//                byte[] chunk1;
//
//                do {
//                    chunk1 = second.readNBytes(Constants.blockSize);
//                    System.out.println(chunk1.length +" ");
//                    serverArr[loopserver]=chunk1;
//                    System.out.println("serverArr[loopserver] "+serverArr[loopserver] +"loopserver :"+loopserver);
//                    loopserver++;
//
//
//                    System.out.println("loopserver " + loopserver);
//
//
//                } while (chunk1.length > 0 );
//                int x=0;
//                for(int i = diff;i<length;i++){
//                    serverArr[i] = a[x];
//                    x++;
//                }
////to right
//                FileOutputStream serverFile = new FileOutputStream(String.valueOf(ServerFile));
//                for(int i = 0;i<length;i++){
//                    serverFile.write(serverArr[i]);
//                    System.out.println(" WRITTEN TO FILE HAHA " );
//                }
//
//
//            }
//        }



        public static void main(String[] args) throws SocketException {
    //        File File_One = new File("G:\\My Drive\\UTA\\Sem5\\ASE\\file-transfer\\clientFileHolder\\small.txt"); // Path to file one
    //        File File_Two = new File("G:\\My Drive\\UTA\\Sem5\\ASE\\file-transfer\\clientFileHolder\\small_copy.txt"); // Path to file two

          //  System.out.println(compareFileByByte(File_One.toPath(), File_Two.toPath()));

        }
    }
