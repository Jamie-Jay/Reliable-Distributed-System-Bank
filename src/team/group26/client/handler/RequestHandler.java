package team.group26.client.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestHandler extends Thread{
    private PrintWriter out;
    private BufferedReader in;
    private Map<Integer, Boolean> duplicateManager;
    private String request;
    private Socket socket;
    private String cid;
    private int socketId;
    private int port;

    public RequestHandler(PrintWriter out, BufferedReader in, Map<Integer, Boolean> duplicateManager, String request) {
        this.out = out;
        this.in = in;
        this.duplicateManager = duplicateManager;
        this.request = request;
    }

    public RequestHandler(int socketId, int port, String cid, Map<Integer, Boolean> duplicateManager,
                          String request) throws IOException {
        this.cid = cid;
        this.socketId = socketId;
        this.port = port;
        if (MainHandler.socket[socketId] != null) {
            this.out = new PrintWriter(MainHandler.socket[socketId].getOutputStream(),true);
            this.in = new BufferedReader(new InputStreamReader(MainHandler.socket[socketId].getInputStream()));
        }

        this.duplicateManager = duplicateManager;
        this.request = request;
    }

    @Override
    public void run() {
        // Return is the socket is disconnected
        if (MainHandler.socket[socketId] == null) {
            System.out.println("Port "+port+" is in closed");
            return;
        }
        String fromServer;
        String log;
        String sid;
        int requestId;

        try {
            out.println(request);
            System.out.println("client: " + request);
            fromServer = in.readLine();
            String[] requests = fromServer.split("\\s+");
            sid = requests[0];
            requestId = Integer.parseInt(requests[2]);
            log = String.format("[Server %s] %s", sid, fromServer);
            if(duplicateManager.remove(requestId) == null) {
                log = String.format("msg_num %d: Duplicate response received from replica %s", requestId, sid);
            }
            System.out.println(log);
        } catch (IOException e) {
            MainHandler.socket[socketId] = null;
            while (MainHandler.socket[socketId] == null) {
                try {
                    MainHandler.socket[socketId] = new Socket("localhost", port);
                } catch (IOException ex) {
                    // Do nothing
                }
            }
            try {
                this.out = new PrintWriter(MainHandler.socket[socketId].getOutputStream(),true);
                out.println(cid);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // System.out.println("Connection reset "+port );
        }
    }
}
