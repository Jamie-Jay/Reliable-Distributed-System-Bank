package team.group26.client;

import team.group26.client.handler.RequestHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Client {
    private String hostName;
    private int port1, port2, port3;
    private int baseRid;
    private String cid;
    private Map<Integer, Boolean> duplicateManager = new ConcurrentHashMap<>();

    public Client(String hostName, int port1, int port2, int port3, String cid, int baseRid) {
        this.hostName = hostName;
        this.port1 = port1;
        this.port2 = port2;
        this.port3 = port3;
        this.cid = cid;
        this.baseRid = baseRid;
    }

    // hard code sid and port
    public int getServerPort(String sid) {
        if (sid.equals("s1")) {
            return 8080;
        } else if (sid.equals("s2")) {
            return 8081;
        } else if (sid.equals("s3")) {
            return 8082;
        }
        return -1;
    }

    public void runService() {
        try {
            System.out.println("The client "+cid+" is running");
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            // hard code address of RM
            Socket socketRM = new Socket("localhost", 4040);

            // IO Stream of RM
            PrintWriter outRM = new PrintWriter(socketRM.getOutputStream(),true);
            BufferedReader inRM = new BufferedReader(new InputStreamReader(socketRM.getInputStream()));
            System.out.println("Hello, Welcome! Please use the command <cmd> <amount>");
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                // Get membership list
                outRM.println("MEM_REQ");
                String[] membersList = (inRM.readLine()).split("\\s+");

                duplicateManager.put(baseRid, true);
                // System.out.println(membersList);
                for (String member : membersList) {
                    String request = String.format("%s %s %d %s", member, cid, baseRid, userInput);
                    Socket clientSocket = new Socket("localhost", getServerPort(member));
                    (new RequestHandler(clientSocket, duplicateManager, request)).start();
                }
                baseRid += 1;
            }
        } catch (Exception e) {
            //e.printStackTrace();
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