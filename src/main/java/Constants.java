import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final int SERVER_TCP_PORT = 6000;
    public static final int SERVER_UDP_PORT_CLIENT_ONE = 11111;
    public static final int SERVER_UDP_PORT_CLIENT_TWO = 11112;
    public static final String[][] LOCAL_DIRS = {
                                                {// user facing dirs
                                                        "C:\\advse\\s1\\",
                                                        "C:\\advse\\c1\\",
                                                        "C:\\advse\\c2\\",
                                                },
                                                { // backup dirs. Needed to compare files with user facing dirs for changes
                                                        "C:\\advse\\backup\\s1\\",
                                                        "C:\\advse\\backup\\c1\\",
                                                        "C:\\advse\\backup\\c2\\",
                                                }
                                            };

    public static final int blockSize = 2_000_000;
    public static final int UDP_PACK_SIZE = 2000060;
    public static final String CRLF = "\r\n";
//
public static final String CLIENT_FILE_ROOT ="C:\\advse\\c1\\";
    public static final String CLIENT_FILE_ROOT2 ="C:\\advse\\c2\\";

    public static final String SERVER_FILE_ROOT = "C:\\advse\\s1\\";


    public static final int MONITOR_BUFFER_SIZE=6;
    public static final int CLIENT_UDP_PORT = 15550;
    public static final int SERVER_UDP_PORT = 16667;


    public static final int MAX_DATAGRAM_SIZE = 65500;


}
