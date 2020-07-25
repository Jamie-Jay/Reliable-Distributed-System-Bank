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


    public RequestHandler(Socket socket, Map<Integer, Boolean> duplicateManager,
                          String request) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(),true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.duplicateManager = duplicateManager;
        this.request = request;
    }

    @Override
    public void run() {
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
            socket.close();
        } catch (IOException e) {
            // System.out.println("Connection reset "+port );
        }
    }
}
