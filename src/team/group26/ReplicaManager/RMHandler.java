package team.group26.ReplicaManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RMHandler extends Thread {
    Socket clientSocket;

    public RMHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

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
            }
        }
        printMembership();
        return true;
    }

    @Override
    public void run() {
        try {
            // PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            while((inputLine = in.readLine()) != null) {
                String[] request = inputLine.split("\\s+");
                if(request.length == 0) {
                    continue;
                }
                if (handleMembership(request)) {
                    continue;
                }
            }

        } catch (IOException e) {
            // do nothing
        }

    }
}
