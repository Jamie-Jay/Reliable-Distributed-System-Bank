package team.group26.client;

import team.group26.client.handler.MainHandler;

import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Client {
    private String hostName;
    private int port1, port2, port3;
    private int baseRid;
    private String cid;
    private List<Socket> servers = new CopyOnWriteArrayList<>();

    public Client(String hostName, int port1, int port2, int port3, String cid, int baseRid) {
        this.hostName = hostName;
        this.port1 = port1;
        this.port2 = port2;
        this.port3 = port3;
        this.cid = cid;
        this.baseRid = baseRid;
    }

    public void runService() {
        try {
            Socket clientSocket1 = new Socket(hostName, port1);
            Socket clientSocket2 = new Socket(hostName, port2);
            Socket clientSocket3 = new Socket(hostName, port3);
            System.out.println("The client "+cid+" is running");
            (new MainHandler(clientSocket1, clientSocket2, clientSocket3,
                    cid, baseRid)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String hostName = args[0];
        int port1 = Integer.parseInt(args[1]);
        int port2 = Integer.parseInt(args[2]);
        int port3 = Integer.parseInt(args[3]);
        //get the client ID from Command line
        String cid = args[4];
        int base_rid = Integer.parseInt(args[5]);
        (new Client(hostName, port1, port2, port3, cid, base_rid)).runService();
    }
}

