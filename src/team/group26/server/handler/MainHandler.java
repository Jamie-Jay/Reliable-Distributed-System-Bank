package team.group26.server.handler;

import team.group26.server.utils.RequestProcessor;

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
    public MainHandler(Socket clientSocket, String sid){
        this.clientSocket = clientSocket;
        this.sid = sid;
    }

    public void run() {
        try{
            PrintWriter out =
                    new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            //get the client ID
            String cid = in.readLine();
            System.out.println("[client "+ cid +"] is connected.");
            RequestProcessor pi = new RequestProcessor(cid);
            outputLine = pi.processInput(null);
            out.println(outputLine);
            while((inputLine = in.readLine()) != null) {
                System.out.println("Reply from [Client " + cid + "] " + inputLine);
                outputLine = pi.processInput(inputLine);
                out.println("[Serve " + sid + "] " + outputLine);
                System.out.println("Response to [Client " + cid + "] " + outputLine);
                if (outputLine.equals("exit"))
                    break;
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
