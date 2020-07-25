package team.group26.passiveReplica;

import team.group26.passiveReplica.Bank;
import team.group26.passiveReplica.handler.MainHandler;
import team.group26.passiveReplica.handler.PassiveBankUpHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class PrimaryServer {
    private int port;
    private String sid;
    private List<Socket> clients = new CopyOnWriteArrayList<>();

    // backup info
    private String backUpHost;
    private int backupPort1;
    private String backup1Sid;
    private int backupPort2;
    private String backup2Sid;
    private int interval;
    private int timeout;

    // state data
    private boolean isPassive;
    private boolean syncBackup;
    protected final Bank bank=Bank.getInstance();

    public PrimaryServer(int port, String sid, boolean isPassive) {
        this.port = port;
        this.sid = sid;
        this.isPassive = isPassive;
        this.syncBackup = false;
    }

    public PrimaryServer(int port, String sid, String backUpHost, int backupPort1, String backup1Sid, int backupPort2, String backup2Sid, int interval, int timeout) {
        this.port = port;
        this.sid = sid;
        this.backUpHost = backUpHost;
        this.backupPort1 = backupPort1;
        this.backup1Sid = backup1Sid;
        this.backupPort2 = backupPort2;
        this.backup2Sid = backup2Sid;
        this.interval = interval;
        this.timeout = timeout;
        this.isPassive = false;
        this.syncBackup = true;
    }

    public PrimaryServer(int port) {
        this.port = port;
    }

    // Entry of the primary server to run service
    public void runService() {
        try {
            ServerSocket serverSocket = new ServerSocket(this.port);
            System.out.println("The server is running on port " + this.port);

            if (syncBackup) {
                if (backupPort1 != 0) {
                    PassiveBankUpHandler PassiveBankUpHandler1 = new PassiveBankUpHandler(backup1Sid, backUpHost, backupPort1, interval, timeout, bank);
                    PassiveBankUpHandler1.start();
                }
                if (backupPort2 != 0) {
                    PassiveBankUpHandler PassiveBankUpHandler2 = new PassiveBankUpHandler(backup2Sid, backUpHost, backupPort2, interval, timeout, bank);
                    PassiveBankUpHandler2.start();
                }
            }

            while(true){
                Socket clientSocket = serverSocket.accept();
                MainHandler mainHandler = new MainHandler(clientSocket, sid, bank, isPassive);
                mainHandler.start();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        if (args.length > 8){ // have passive backup replica
            String hostName = args[2];
            int backupPort1 = Integer.parseInt(args[3]);
            String backup1ServerName = args[4];
            int backupPort2 = Integer.parseInt(args[5]);
            String backup2ServerName = args[6];
            int interval = Integer.parseInt(args[7]);
            int timeout = Integer.parseInt(args[8]);
            (new PrimaryServer(port, args[1], hostName, backupPort1, backup1ServerName, backupPort2, backup2ServerName, interval, timeout)).runService();
        } else {
            boolean isPassive = (args.length > 2) && ("PASSIVE".equalsIgnoreCase(args[2]));
            (new PrimaryServer(port, args[1], isPassive)).runService();
        }
    }
}


