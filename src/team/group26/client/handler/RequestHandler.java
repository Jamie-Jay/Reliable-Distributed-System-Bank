package team.group26.client.handler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RequestHandler extends Thread {
    private Socket socket;
    private String inputLine, outputLine;
    private String cid;

    public RequestHandler(Socket socket, String cid) {
        this.socket = socket;
        this.cid = cid;
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            //pass the client ID to server
            out.println(cid);
            String fromServer;
            String userInput;

            while ((fromServer = in.readLine()) != null) {
                System.out.println("[Server] " + fromServer);
                if (fromServer.equals("exit"))
                    break;

                userInput = stdIn.readLine();
                if (userInput != null) {
                    System.out.println("client: " + userInput);
                    out.println(userInput);
                }
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}