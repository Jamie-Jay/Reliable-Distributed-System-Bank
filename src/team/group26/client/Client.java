package team.group26.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        //get the client ID from Command line
        String cID = args[2];

        try {
            Socket clientSocket = new Socket(hostName, portNumber);
            System.out.println("The client "+cID+" is running");

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            //pass the client ID to server
            out.println(cID);
            String fromServer;
            String userInput;

            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                if (fromServer.equals("exit"))
                    break;

                userInput = stdIn.readLine();
                if (userInput != null) {
                    System.out.println("client: " + userInput);
                    out.println(userInput);
                }
            }
            clientSocket.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}

