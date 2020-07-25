package team.group26.globalFaultDetector.handler;

import team.group26.activeReplica.utils.RequestProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class GFDHandler extends Thread{
    Socket clientSocket;
    String inputLine,outputLine;
    List<String> membership;
    String sid;

    public GFDHandler(Socket clientSocket, List<String> membership){
        this.clientSocket = clientSocket;
        this.membership = membership;
    }

    synchronized private void printMembership() {
        String res = String.format("GFD: %d members: ", membership.size());
        for (String member : membership) {
            res += (member + " ");
        }
        System.out.println(res);
    }

    public void updateRM(String msg) throws IOException {
        // hard-code the address of RM
        Socket socket = new Socket("localhost", 4040);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println(msg);
        out.close();
        socket.close();
    }

    @Override
    public void run() {
        try{
            PrintWriter out =
                    new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String[] requests;
            String lid, op, sid;

            while((inputLine = in.readLine()) != null) {
                requests = inputLine.split("\\s+");
                lid = requests[0];
                op = requests[1];
                sid = requests[2];
                // delete a server from membership
                if(("DELETE").equals(op)) {
                    for(String item : membership) {
                        if (item.equals(sid)) {
                            membership.remove(item);
                            break;
                        }
                    }
                }
                if(("ADD").equals(op)) {
                    membership.add(sid);
                }

                updateRM(String.format("MEMBER %s %s", op, sid));

                printMembership();
            }
        }
        catch(IOException e){
            try {
                clientSocket.close();
            } catch (IOException ex) {

            }
        }
    }
}
