package team.group26.client.handler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RequestHandler extends Thread {
    private Socket socket;
    private String sid;
    private Socket socket2;
    private String sid2;
    private String inputLine, outputLine;
    private String cid;
    private int baseRid;
    private Set<Integer> toProcessReuqestIds;

    public RequestHandler(Socket socket, String cid, int baseRid) {
        this.socket = socket;
        this.cid = cid;
        this.baseRid = baseRid;
    }

    public RequestHandler(Socket socket, String sid, Socket socket2, String sid2, String cid, int baseRid) {
        this.socket = socket;
        this.sid = sid;
        this.socket2 = socket2;
        this.sid2 = sid2;
        this.cid = cid;
        this.baseRid = baseRid;
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out2 = new PrintWriter(socket2.getOutputStream(),true);
            BufferedReader in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            //pass the client ID to server
            out.println(cid);
            out2.println(cid);
            String fromServer;
            String userInput;
            toProcessReuqestIds = new HashSet<>();

            while ((fromServer = in.readLine()) != null || (fromServer = in2.readLine()) != null ) {
                String[] request = fromServer.split("\\s+"); // sid rid content
                if (request.length > 2)
                {
                    for (Integer ele : toProcessReuqestIds) {
                        if (ele.equals(Integer.valueOf(request[1])))
                        {
                            System.out.println("[Server] " + fromServer);
                            toProcessReuqestIds.remove(request[1]);
                            break;
                        }
                    }
                }
                if (fromServer.equals("exit"))
                    break;

                userInput = stdIn.readLine();

                if (userInput != null) {
                    System.out.println("client: " + userInput);
                    out.println("" + cid + " " + baseRid + " " + userInput);
                    out2.println("" + cid + " " + baseRid + " " + userInput);
                    toProcessReuqestIds.add(baseRid);
                    System.out.println(toProcessReuqestIds);
                    baseRid += 1;
                }
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}