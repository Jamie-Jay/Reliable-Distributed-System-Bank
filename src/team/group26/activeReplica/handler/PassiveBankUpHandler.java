package team.group26.activeReplica.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import team.group26.activeReplica.Bank;
import team.group26.activeReplica.utils.SyncBackUpProcessor;

public class PassiveBankUpHandler extends Thread {
    // backup info
    private String backUpHost;
    private int backupPort;
    //private int backupPort2;
    private int interval;
    private int timeout;

    Bank bank;

    public PassiveBankUpHandler(/*int port, String sid, */String backUpHost, int backupPort1, int interval, int timeout, Bank bank) {
        //this.port = port;
        //this.sid = sid;
        this.backUpHost = backUpHost;
        this.backupPort = backupPort1;
        //this.backupPort2 = backupPort2;
        this.interval = interval;
        this.timeout = timeout;
        this.bank = bank;
    }

    public void run() {
        Socket socket = null;
        try {
            socket = new Socket(backUpHost, backupPort);
            socket.setSoTimeout(timeout);
            //System.out.println("The Backup:" + backupPort + " for [Server " + sid + "] is running");
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Timer timer = new Timer();
            timer.schedule(new SyncBackUpProcessor(out, bank),
                    0,
                    interval);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
