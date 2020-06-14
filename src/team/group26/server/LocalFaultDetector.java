package team.group26.server;

import team.group26.server.handler.LFDHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;

public class LocalFaultDetector {
    private String serverHost;
    private String globalDetectorHost;
    private int serverPort;
    private int globalDetectorPort;
    private String sid;
    private int interval;
    private int timeout;

    public LocalFaultDetector(String serverHost, int serverPort, String sid, int interval, int timeout) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.interval = interval;
        this.timeout = timeout;
        this.sid =sid;
    }

    public LocalFaultDetector(String serverHost, String globalDetectorHost, String sid, int serverPort,
                                    int globalDetectorPort, int interval, int timeout) {
        this.serverHost = serverHost;
        this.globalDetectorHost = globalDetectorHost;
        this.serverPort = serverPort;
        this.globalDetectorPort = globalDetectorPort;
        this.sid = sid;
        this.interval = interval;
        this.timeout = timeout;
    }


    public void runService() throws IOException {
        Socket socket = new Socket(serverHost, serverPort);
        socket.setSoTimeout(timeout);
        System.out.println("The LFD for [Server " + sid + "] is running");
        PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out.println("LFD");
        Timer timer = new Timer();
        timer.schedule(new LFDHandler(out, in, sid),0, interval);
    }

    public static void main(String[] args) throws IOException {
        int serverPort = Integer.parseInt(args[0]);
        String serverHost = args[1];
        int interval = Integer.parseInt(args[2]);
        int timeout = Integer.parseInt(args[3]);
        String sid = args[4];
        (new LocalFaultDetector(serverHost, serverPort, sid, interval, timeout)).runService();
    }
}
