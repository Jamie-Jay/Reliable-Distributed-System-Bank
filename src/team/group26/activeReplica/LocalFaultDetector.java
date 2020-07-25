package team.group26.activeReplica;

import team.group26.activeReplica.handler.LFDHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class LocalFaultDetector {
    private String serverHost;
    private String globalDetectorHost;
    private int serverPort;
    private int globalDetectorPort;
    private String sid;
    private int interval;
    private int timeout;
    private String lid;

    public LocalFaultDetector(String serverHost, int serverPort, String sid, int interval, int timeout) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.interval = interval;
        this.timeout = timeout;
        this.sid =sid;
    }

    public LocalFaultDetector(String serverHost, String globalDetectorHost, String sid, int serverPort,
                                    int globalDetectorPort, int interval, int timeout, String lid) {
        this.serverHost = serverHost;
        this.globalDetectorHost = globalDetectorHost;
        this.serverPort = serverPort;
        this.globalDetectorPort = globalDetectorPort;
        this.sid = sid;
        this.interval = interval;
        this.timeout = timeout;
        this.lid = lid;
    }


    public void runService() throws IOException {
        Socket socket = null;
        Socket gfdSocket = null;

        while (true) {
            // Try to build the socket until the connection is built
            while (socket == null) {
                try {
                    // socket to server
                    socket = new Socket(serverHost, serverPort);
                    socket.setSoTimeout(timeout);
                    // socket to GFD
                    gfdSocket = new Socket(globalDetectorHost, globalDetectorPort);
                } catch (IOException e) {
                    // Do nothing
                }
            }
            System.out.println("The LFD for [Server " + sid + "] is running");
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            PrintWriter gfdOut = new PrintWriter(gfdSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // out.println("LFD");
            Timer timer = new Timer();
            // Reset synchronize argument
            LFDHandler.isRun = 1;
            LFDHandler task = new LFDHandler(out, in, sid, gfdOut, lid);
            timer.schedule(task, 0, interval);
            // Manually synchronization
            while (LFDHandler.isRun == 1) ;
            socket = null;
        }

    }

    public static void main(String[] args) throws IOException {
        int serverPort = Integer.parseInt(args[0]);
        String serverHost = args[1];
        int interval = Integer.parseInt(args[2]);
        int timeout = Integer.parseInt(args[3]);
        String sid = args[4];
        String lid = args[5];
        String gfdHost = args[7];
        int gfdPort = Integer.parseInt(args[6]);
        (new LocalFaultDetector(serverHost, gfdHost, sid, serverPort, gfdPort, interval, timeout, lid)).runService();
    }

}

