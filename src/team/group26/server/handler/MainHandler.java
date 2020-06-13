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
    public MainHandler(Socket clientSocket){
        this.clientSocket= clientSocket;
    }

    public void run() {
        try(
                PrintWriter out =
                        new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
        ){
            //get the client ID
            String cID = in.readLine();
            RequestProcessor pi = new RequestProcessor(cID);
            outputLine = pi.processInput(null);
            out.println(outputLine);
            while((inputLine = in.readLine()) != null) {
                outputLine = pi.processInput(inputLine);
                out.println(outputLine);
                if (outputLine.equals("exit"))
                    break;
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
