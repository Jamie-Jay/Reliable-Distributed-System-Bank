package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {

    public static void main(String[] args) {

        int portNumber = Integer.parseInt(args[0]);
        try {

            ServerSocket serverSocket = new ServerSocket( portNumber );
            System.out.println("The server is running on port " + args[0]);

            while(true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("A client is connected");

                ServerThread st = new ServerThread(clientSocket);
                st.start();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}

class ServerThread extends Thread
{
    Socket clientSocket;
    String inputLine,outputLine;
    public ServerThread(Socket clientSocket){
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
            ProcessInput pi = new ProcessInput(cID);
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

class ProcessInput {
    //different input stage
    private static final int WAITING = 0;
    private static final int SERVICE = 1;
    private static final int WITHDRAW = 2;
    private static final int DEPOSIT = 3;
    private static final int CONTINUE= 4;
    private String cID;
    //one thread and one server.ProcessInput object for one client, initial the client ID with client input
    public ProcessInput(String cID){
        this.cID = cID;
    }

    private int state = WAITING;
    final Bank bank=Bank.getInstance();

    private String[] output = { "Please select the service you like by inputting the number: 1. Withdraw 2. Deposit ",
            "Please enter the amount of money you want to withdraw:",
            "Please enter the amount of money you want to deposit:" };

    public synchronized String processInput(String theInput) {

        String theOutput = null;

        if (state == WAITING) {
            theOutput = output[0];
            state = SERVICE;
        } else if (state == SERVICE) {
            if (theInput.equals("1")) {
                theOutput = output[1];
                state = WITHDRAW;
            } else if(theInput.equals("2")){
                theOutput = output[2];
                state = DEPOSIT;
            } else {
                theOutput = "You're supposed to enter\"1\" or \"2\" " +
                        "Try again. ";
            }
        } else if (state == WITHDRAW) {
            int n = Integer.parseInt(theInput);
            if (n > bank.getBalance()) {
                theOutput =  "You don't have enough balance, the balance is " + bank.getBalance()+" want continue service? [y/n]";
                state = CONTINUE;
            }else if (n > 0 && n <= bank.getBalance()){
                bank.withdrawMoney(cID,n);
                theOutput =  "Successfully withdraw, the balance is " + bank.getBalance()+" want continue service? [y/n]";
                state = CONTINUE;
            } else {
                theOutput = "You're supposed to input a integer number which not less than 1";
                state = SERVICE;
            }
        } else if (state == DEPOSIT) {
            int n = Integer.parseInt(theInput);
            bank.depositMoney(cID,n);
            theOutput =  "Successfully deposit, the balance is " + bank.getBalance() +" want continue service? [y/n]";
            state = CONTINUE;
        }else if (state == CONTINUE){
            if (theInput.equals("y")){
                theOutput = "1. Withdraw 2. Deposit";
                state = SERVICE;
            }else{
                theOutput = "close";
                state = WAITING;
            }
        }
        return theOutput;
    }
}
