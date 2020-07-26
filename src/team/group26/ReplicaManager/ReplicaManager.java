package team.group26.ReplicaManager;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class ReplicaManager {
    public static BlockingDeque<String> msgQueue = new LinkedBlockingDeque<>();
    public static CopyOnWriteArrayList<String> membership = new CopyOnWriteArrayList<>();
    // public static AtomicInteger globalSyncId = new AtomicInteger(0);
    private int port;

    public ReplicaManager(int port) {
        this.port = port;
    }



    public void runService() {
        try {
            ServerSocket serverSocket = new ServerSocket(this.port);
            System.out.println("The RM is running on port " + this.port);
            while(true){
                Socket clientSocket = serverSocket.accept();
                RMHandler RMHandler = new RMHandler(clientSocket);
                RMHandler.start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        (new ReplicaManager(port)).runService();
    }
}
