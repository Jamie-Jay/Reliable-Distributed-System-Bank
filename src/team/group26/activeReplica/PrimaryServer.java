package team.group26.activeReplica;

import team.group26.activeReplica.handler.MainHandler;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class PrimaryServer {
    private int port;
    private String sid;
    private List<Socket> clients = new CopyOnWriteArrayList<>();

    public PrimaryServer(int port, String sid) {
        this.port = port;
        this.sid = sid;
    }

    public PrimaryServer(int port) {
        this.port = port;
    }

    // Entry of the primary server to run service
    public void runService() {
        try {
            ServerSocket serverSocket = new ServerSocket(this.port);
            System.out.println("The server is running on port " + this.port);
            while(true){
                Socket clientSocket = serverSocket.accept();
                MainHandler mainHandler = new MainHandler(clientSocket, sid);
                mainHandler.start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        (new PrimaryServer(port, args[1])).runService();
    }
}


