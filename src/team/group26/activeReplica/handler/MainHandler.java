package team.group26.activeReplica.handler;

import team.group26.activeReplica.Bank;
import team.group26.activeReplica.utils.RequestProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class MainHandler extends Thread
{
    public static int rid = 0;
    public static boolean isReady = true;
    public static BlockingQueue<String> msgQueue = new LinkedBlockingQueue<>();
    Socket clientSocket;
    String inputLine,outputLine;
    String sid;


    public MainHandler(Socket clientSocket, String sid){
        this.clientSocket = clientSocket;
        this.sid = sid;
    }

    private void sendCheckpointToBackup(String hostName, int port, String sid) throws IOException {
        Socket secondaryChanel = new Socket(hostName, port);
        PrintWriter out = new PrintWriter(secondaryChanel.getOutputStream(),true);
        String checkpointMsg = String.format("CHECKPOINT %d %d", RequestProcessor.bank.getBalance(), rid);
        System.out.println("Send Checkpoint to new replica " + sid);
        // out.println("CHECKPOINT");
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
    private boolean handleCheckpoint(String input) throws IOException, InterruptedException {
        // mimic the recovery time

        String[] request = input.split("\\s+");
        if (request.length != 3 || !request[0].equals("CHECKPOINT")) {
            return false;
        }

        Thread.sleep(10000);


        System.out.println("[ckt] " + input);
        String lastRid = request[2];
        Boolean notHandle = false;
        (Bank.getInstance()).setBalance(Integer.parseInt(request[1]));
        RequestProcessor pi = new RequestProcessor();
        // Recover unhandled log
        for (String msg : msgQueue) {
            if ((msg.split("\\s+")[2]).equals(lastRid)) {
                notHandle = true;
            }
        }

        while (!msgQueue.isEmpty()) {
            String inputLine = msgQueue.take();
            if (!notHandle) {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                String cid = (inputLine.split("\\s+"))[1];
                outputLine = pi.processInput(inputLine);
                out.println(sid + " " + outputLine);
                System.out.println("(recovery from log) Request from [Client " + cid + "] " + inputLine);
                System.out.println("(recovery from log) Response to [Client " + cid + "] " + sid + " " + outputLine);
            }
            if ((inputLine.split("\\s+")[2]).equals(lastRid)) {
                notHandle = false;
            }
        }
        isReady = true;
        return true;
    }

    public void run() {
        try{
            PrintWriter out =
                    new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            //get the client ID
            //String cid = in.readLine();
            //final boolean isClient = !cid.equals("LFD") && !cid.equals("CHECKPOINT") && !cid.equals("RM");
            RequestProcessor pi = new RequestProcessor();
            while((inputLine = in.readLine()) != null) {
                // Handle potential RM_MSG
                if (handleRM(inputLine)) {
                    continue;
                }

                // Handle potential CHECKPOINT_MSG
                if (handleCheckpoint((inputLine))) {
                    continue;
                }

                if (inputLine.equals("PING")) {
                    out.println(sid + " PONG");
                    continue;
                }
                // Mimic internet latency
                Thread.sleep(200);

                if (!isReady) {
                    msgQueue.put(inputLine);
                    System.out.println("[Into log] " + inputLine);
                    continue;
                }

                String[] requests = inputLine.split("\\s+");
                String cid = requests[1];


                outputLine = pi.processInput(inputLine);
                out.println(sid + " " + outputLine);
                System.out.println("Request from [Client " + cid + "] " + inputLine);
                System.out.println("Response to [Client " + cid + "] " + sid + " " + outputLine);
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
