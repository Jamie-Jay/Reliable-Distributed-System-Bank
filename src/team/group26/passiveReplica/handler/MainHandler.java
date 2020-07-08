package team.group26.passiveReplica.handler;

import team.group26.passiveReplica.Bank;
import team.group26.passiveReplica.utils.RequestProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainHandler extends Thread
{
    Socket clientSocket;
    String inputLine,outputLine;
    String sid;

    // state data
    boolean isPassive;
    Bank bank;

    public MainHandler(Socket clientSocket, String sid, Bank bank, boolean isPassive){
        this.clientSocket = clientSocket;
        this.sid = sid;
        this.bank = bank;
        this.isPassive = isPassive;
    }

    public void run() {
        try{
            PrintWriter out =
                    new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            //get the client ID
            String cid = in.readLine();
            if(!cid.equals("LFD")){
                System.out.println("["+ cid +"] is connected.");
            }
            RequestProcessor pi = new RequestProcessor(cid, bank);
            while((inputLine = in.readLine()) != null) {
                if (isPassive)
                {
                    // do not work for client request
                    outputLine = pi.SyncInput(inputLine);
                } else {
                    outputLine = pi.processInput(inputLine);
                }

                if (outputLine != null) {
                    out.println(sid + " " + outputLine);
                    if(!cid.equals("LFD")) {
                        System.out.println("Request from [Client " + cid + "] " + inputLine);
                        System.out.println("Response to [Client " + cid + "] " + sid + " " + outputLine);
                    } else {
                        // System.out.println("[LFD] PING");
                    }
                    if (outputLine.equals("exit"))
                        break;
                }
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
