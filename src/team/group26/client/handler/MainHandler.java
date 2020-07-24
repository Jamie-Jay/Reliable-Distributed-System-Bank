package team.group26.client.handler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainHandler extends Thread {
    public static Socket[] socket = new Socket[3];
    private String inputLine, outputLine;
    private String cid;
    private int baseRid;
    private Map<Integer, Boolean> duplicateManager = new ConcurrentHashMap<>();

    public MainHandler(String cid, int baseRid) {
        this.cid = cid;
        this.baseRid = baseRid;
    }

    @Override
    public void run() {
        try {
            String fromServer;
            String userInput;
            PrintWriter out1 = new PrintWriter(socket[0].getOutputStream(),true);
            BufferedReader in1 = new BufferedReader(new InputStreamReader(socket[0].getInputStream()));
            PrintWriter out2 = new PrintWriter(socket[1].getOutputStream(),true);
            BufferedReader in2 = new BufferedReader(new InputStreamReader(socket[1].getInputStream()));
            PrintWriter out3 = new PrintWriter(socket[2].getOutputStream(),true);
            BufferedReader in3 = new BufferedReader(new InputStreamReader(socket[2].getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Hello, please apply the request <cmd> <amount>");

            // Send initialization client id to servers
            out1.println(cid);
            out2.println(cid);
            out3.println(cid);


            while ((userInput = stdIn.readLine()) != null) {
                /*System.out.println("client: " + userInput);
                (new RequestHandler(out1, in1, duplicateManager,
                        userInput)).start();*/
                duplicateManager.put(baseRid, true);
                if (MainHandler.socket[0] != null) {
                    (new RequestHandler(0, 8080, "c1", duplicateManager,
                            String.format("s1 %s %d %s", cid, baseRid, userInput))).start();
                }
                if (MainHandler.socket[1] != null) {
                    (new RequestHandler(1, 8081, "c2", duplicateManager,
                            String.format("s2 %s %d %s", cid, baseRid, userInput))).start();
                }
                if (MainHandler.socket[2] != null) {
                    (new RequestHandler(2, 8082, "c3", duplicateManager,
                            String.format("s3 %s %d %s", cid, baseRid, userInput))).start();
                }
                baseRid += 1;
            }
            socket[0].close();
            socket[1].close();
            socket[2].close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}