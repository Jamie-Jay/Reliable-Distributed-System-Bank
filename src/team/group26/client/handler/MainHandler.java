package team.group26.client.handler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainHandler extends Thread {
    private Socket socket1;
    private Socket socket2;
    private Socket socket3;
    private String inputLine, outputLine;
    private String cid;
    private int baseRid;
    private Map<Integer, Boolean> duplicateManager = new ConcurrentHashMap<>();

    public MainHandler(Socket socket1, Socket socket2, Socket socket3, String cid, int baseRid) {
        this.socket1 = socket1;
        this.socket2 = socket2;
        this.socket3 = socket3;
        this.cid = cid;
        this.baseRid = baseRid;
    }

    @Override
    public void run() {
        try {
            String fromServer;
            String userInput;
            PrintWriter out1 = new PrintWriter(socket1.getOutputStream(),true);
            BufferedReader in1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
            PrintWriter out2 = null;
            BufferedReader in2 = null;
            PrintWriter out3 = null;
            BufferedReader in3 = null;
            if (socket2 != null) {
                out2 = new PrintWriter(socket2.getOutputStream(), true);
                in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
            }
            if (socket3 != null) {
                out3 = new PrintWriter(socket3.getOutputStream(),true);
                in3 = new BufferedReader(new InputStreamReader(socket3.getInputStream()));
            }

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Hello, please apply the request <cmd> <amount>");

            // Send initialization client id to servers
            out1.println(cid);
            if (out2 != null)
                out2.println(cid);
            if (out3 != null)
                out3.println(cid);

            while ((userInput = stdIn.readLine()) != null) {
                System.out.println("client: " + userInput);
                duplicateManager.put(baseRid, true);
                (new RequestHandler(out1, in1, duplicateManager,
                        String.format("s1 %s %d %s", cid, baseRid, userInput))).start();
                if (out2 != null && in2 != null)
                    (new RequestHandler(out2, in2, duplicateManager,
                        String.format("s2 %s %d %s", cid, baseRid, userInput))).start();
                if (out3 != null && in3 != null)
                    (new RequestHandler(out3, in3, duplicateManager,
                        String.format("s3 %s %d %s", cid, baseRid, userInput))).start();
                baseRid += 1;
            }
            socket1.close();
            if (socket2 != null)
                socket2.close();
            if (socket3 != null)
                socket3.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}