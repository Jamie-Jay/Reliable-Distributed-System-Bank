package team.group26.globalFaultDetector;

import team.group26.globalFaultDetector.handler.GFDHandler;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GFD {
    private int port;
    private List<String> membership = new CopyOnWriteArrayList<>();

    public GFD(int port) {
        this.port = port;
    }

    public void runService() {
        try {
            ServerSocket serverSocket = new ServerSocket(this.port);
            System.out.println("The GFD is running on port " + this.port);
            while(true) {
                Socket clientSocket = serverSocket.accept();
                GFDHandler GFDHandler = new GFDHandler(clientSocket, membership);
                GFDHandler.start();
            }
        }catch (Exception e){
            // e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        (new GFD(port)).runService();
    }
}
