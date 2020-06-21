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
    private int baseRid;
    private String cid;
    private List<Socket> servers = new CopyOnWriteArrayList<>();

    public Client(String hostName, int port, String cid, int baseRid) {
        this.hostName = hostName;
        this.port = port;
        this.cid = cid;
        this.baseRid = baseRid;
    }

    public void runService() {
        try {
            Socket clientSocket = new Socket(hostName, port);
            System.out.println("The client "+cid+" is running");
            (new RequestHandler(clientSocket, cid, baseRid)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String hostName = args[0];
        int port = Integer.parseInt(args[1]);
        //get the client ID from Command line
        String cid = args[2];
        int base_rid = Integer.parseInt(args[3]);
        (new Client(hostName, port, cid, base_rid)).runService();
    }
}
