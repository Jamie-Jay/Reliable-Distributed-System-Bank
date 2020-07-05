package team.group26.activeReplica.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TimerTask;

public class LFDHandler extends TimerTask {
    private PrintWriter out, gfdOut;
    private BufferedReader in;
    private String sid;
    private String lid;
    private static int heartbeatCount = 0;
    public static volatile int isRun = 1;


    public LFDHandler(PrintWriter out, BufferedReader in, String sid, PrintWriter gfdOut, String lid) {
        this.out = out;
        this.in = in;
        this.sid = sid;
        this.gfdOut = gfdOut;
        this.lid = lid;
    }

    @Override
    public void run(){
        String[] fromServer;
        long startTime;
        long endTime;
        try {
            out.println("PING");
            heartbeatCount += 1;
            // System.out.println(in.readLine());
            fromServer = (in.readLine()).split("\\s+");
            if((fromServer[1]).equals("PONG")){
                // add membership message to server
                if (heartbeatCount == 1) {
                    System.out.println(String.format("%s: %s add %s", lid, lid, sid));
                    gfdOut.println(String.format("%s add %s", lid, sid));
                }
                System.out.println("[Server "+ sid +"] is alive.");
                if(heartbeatCount % 5 == 0) {
                    System.out.println("heartbeat " + heartbeatCount + " times.");
                }
            }
        } catch (IOException e) {
            System.out.println("[Server "+ sid +"] dies.");
            System.out.println("heartbeat " + heartbeatCount + " times.");
            // delete membership message to server
            System.out.println(String.format("%s: %s delete %s", lid, lid, sid));
            gfdOut.println(String.format("%s delete %s", lid, sid));
            heartbeatCount = 0;
            isRun = -1;
            this.cancel();
        }
    }
}
