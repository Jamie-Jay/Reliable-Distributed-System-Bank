package team.group26.activeReplica.handler;

import team.group26.activeReplica.utils.RequestProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainHandler extends Thread
{
    public static int rid = 0;
    Socket clientSocket;
    String inputLine,outputLine;
    String sid;
    boolean isReady;

    public MainHandler(Socket clientSocket, String sid){
        this.clientSocket = clientSocket;
        this.sid = sid;
    }

    public MainHandler(Socket clientSocket, String sid, boolean isReady){
        this.clientSocket = clientSocket;
        this.sid = sid;
        this.isReady = isReady;
    }

    private void sendCheckpointToBackup(String hostName, int port, String sid) throws IOException {
        Socket secondaryChanel = new Socket(hostName, port);
        PrintWriter out = new PrintWriter(secondaryChanel.getOutputStream(),true);
        String checkpointMsg = String.format("CHECKPOINT %d %d", RequestProcessor.bank.getBalance(), rid);
        System.out.println("Send Checkpoint to new replica " + sid);
        out.println("CHECKPOINT");
        out.println(checkpointMsg);
        out.close();
        secondaryChanel.close();
    }

    /* RM_MSG format: NEW_MEMBER <sid> <port>*/
    private boolean handleRM(String input) throws IOException {
        String[] request = input.split("\\s+");
        if (request.length != 3 || !request[0].equals("NEW_MEMBER")) {
            return false;
        }
        int port = Integer.parseInt(request[2]);
        sendCheckpointToBackup("localhost", port, request[1]);
        return true;
    }

    /* RM_MSG format: CHECKPOINT <balance> <last_rid>*/
    private boolean handleCheckpoint(String input) throws IOException {
        String[] request = input.split("\\s+");
        if (request.length != 3 || !request[0].equals("CHECKPOINT")) {
            return false;
        }
        System.out.println("[ckt] " + input);
        return true;
    }

    public void run() {
        try{
            PrintWriter out =
                    new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            //get the client ID
            String cid = in.readLine();
            final boolean isClient = !cid.equals("LFD") && !cid.equals("CHECKPOINT") && !cid.equals("RM");
            if(isClient){
                System.out.println("[client "+ cid +"] is connected.");
            }
            RequestProcessor pi = new RequestProcessor(cid);
            while((inputLine = in.readLine()) != null) {

                // Handle potential RM_MSG
                if (handleRM(inputLine)) {
                    continue;
                }

                // Handle potential CHECKPOINT_MSG
                if (handleCheckpoint((inputLine))) {
                    continue;
                }

                // Mimic internet latency
                Thread.sleep(300);

                outputLine = pi.processInput(inputLine);
                out.println(sid + " " + outputLine);
                if(isClient) {
                    System.out.println("Request from [Client " + cid + "] " + inputLine);
                    System.out.println("Response to [Client " + cid + "] " + sid + " " + outputLine);
                } else {
                    // System.out.println("[LFD] PING");
                }
                // System.out.println("Least recent rid " + MainHandler.rid);
                if (outputLine.equals("exit"))
                    break;
            }
        }
        catch(IOException | InterruptedException e){
            try {
                clientSocket.close();
            } catch (IOException ex) {

            }
        }
    }
}
