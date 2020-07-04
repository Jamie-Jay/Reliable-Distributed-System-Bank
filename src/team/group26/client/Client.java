package team.group26.client;

import team.group26.client.handler.RequestHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Client {
    private String hostName;
    private int port;
    private String serverName;
    private String hostName2;
    private int port2;
    private String serverName2;
    private int baseRid;

    private String cid;
    private List<Socket> servers = new CopyOnWriteArrayList<>();

    public Client(String hostName, int port, String cid, int baseRid) {
        this.hostName = hostName;
        this.port = port;
        this.cid = cid;
        this.baseRid = baseRid;
    }
    public Client(String hostName, int port, String serverName, String hostName2, int port2, String serverName2, String cid, int baseRid) {
        this.hostName = hostName;
        this.port = port;
        this.serverName = serverName;
        this.hostName2 = hostName2;
        this.port2 = port2;
        this.serverName2 = serverName2;
        this.cid = cid;
        this.baseRid = baseRid;
    }

    public void runService() {
        try {
            Socket clientSocket = new Socket(hostName, port);
            Socket clientSocket2 = new Socket(hostName2, port2);
            System.out.println("The client "+cid+" is running");
            (new RequestHandler(clientSocket, serverName, clientSocket2, serverName2, cid, baseRid)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String hostName = args[0];
        int port = Integer.parseInt(args[1]);
        String serverName = args[2];

        String hostName2 = args[3];
        int port2 = Integer.parseInt(args[4]);
        String serverName2 = args[5];
        //get the client ID from Command line
        String cid = args[6];
        int base_rid = Integer.parseInt(args[7]);
        (new Client(hostName, port, serverName, hostName2, port2, serverName2, cid, base_rid)).runService();
    }
}

