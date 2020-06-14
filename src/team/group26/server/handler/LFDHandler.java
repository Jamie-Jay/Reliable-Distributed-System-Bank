package team.group26.server.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

public class LFDHandler extends TimerTask {
    private PrintWriter out;
    private BufferedReader in;
    private String sid;

    public LFDHandler(PrintWriter out, BufferedReader in, String sid) {
        this.out = out;
        this.in = in;
        this.sid = sid;
    }

    @Override
    public void run(){
        String fromServer;
        long startTime;
        long endTime;
        try {
            out.println("PING");
            if((fromServer = in.readLine()).equals("PONG")){
                System.out.println("[Server "+ sid +"] is alive.");
            }
        } catch (IOException e) {
            System.out.println("[Server "+ sid +"] dies.");
            this.cancel();
        }
    }
}
