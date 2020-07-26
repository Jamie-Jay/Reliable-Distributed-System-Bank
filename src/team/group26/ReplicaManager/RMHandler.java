package team.group26.ReplicaManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class RMHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter outClient;
    private BufferedReader inClient;

    public RMHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.outClient = new PrintWriter(clientSocket.getOutputStream(),true);
        this.inClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
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

    /*public synchronized void messageProxy(String payload) {
        for (String member : ReplicaManager.membership) {
            String message = String.format("%s %s", member, payload);
            int serverPort = getServerPort(member);
            try {
                Socket socket = new Socket("localhost", serverPort);
                (new RequestHandler(socket, message)).start();
            } catch (IOException e) {
                // do nothing is member differs
            }
        }
    }*/



    /* RM_MSG format: NEW_MEMBER <sid> <port>*/
    public void sendCheckpointMsg(String s1, String s2) throws IOException {
        int primaryServer = getServerPort(s1);
        int newServer = getServerPort(s2);
        Socket socket = new Socket("localhost", primaryServer);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        // out.println("RM");
        out.println(String.format("NEW_MEMBER %s %d", s2, newServer));
        out.close();
        socket.close();
    }

    synchronized private void printMembership() {
        String res = String.format("%d members: ", ReplicaManager.membership.size());
        for (String member : ReplicaManager.membership) {
            res += (member + " ");
        }
        System.out.println(res);
    }

    public boolean handleMembership(String[] request) throws IOException {
        if (!request[0].equals("MEMBER")) {
            return false;
        }
        String op = request[1];
        String sid = request[2];
        String firstSid = null;
        if(!ReplicaManager.membership.isEmpty()) {
            firstSid = ReplicaManager.membership.get(0);
        }
        if(("DELETE").equals(op)) {
            for(String item : ReplicaManager.membership) {
                if (item.equals(sid)) {
                    ReplicaManager.membership.remove(item);
                    break;
                }
            }
        }
        if(("ADD").equals(op)) {
            ReplicaManager.membership.add(sid);
            if (firstSid != null) {
                sendCheckpointMsg(firstSid, sid);
            } else {
                sendCheckpointMsg(sid, sid);
            }
        }
        printMembership();
        return true;
    }

    // Atomically send membership message
    public synchronized boolean handleMemberRequest (String[] request) throws IOException {
        if (!request[0].equals("MEM_REQ")) {
            return false;
        }
        String msg = "";
        for (String member : ReplicaManager.membership) {
            msg += (member + " ");
        }
        outClient.println(msg);
        // If performance matters, delete this line
        inClient.readLine();

        // Thread.sleep(500);
        return true;
    }

    @Override
    public void run() {
        try {
            // PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = this.inClient;
            String inputLine;
            while((inputLine = in.readLine()) != null) {
                String[] request = inputLine.split("\\s+");
                if(request.length == 0) {
                    continue;
                }
                if (handleMembership(request)) {
                    continue;
                }
                if (handleMemberRequest(request)) {
                    continue;
                }
            }

        } catch (IOException e) {
            // do nothing
        }

    }
}
