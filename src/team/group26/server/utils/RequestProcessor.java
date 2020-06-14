package team.group26.server.utils;

import team.group26.server.Bank;

public class RequestProcessor {
    // Request Type
    private static final String DEPOSIT = "DEP";
    private static final String WITHDRAW = "WTD";
    private static final String PING = "PING";
    private static final String WELCOME = "Hello, please apply the request <cid> <cmd> <amount>";

    private String cid;
    // One thread and one server.ProcessInput object for one client, initial the client ID with client input
    public RequestProcessor(String cid){
        this.cid = cid;
    }

    final Bank bank=Bank.getInstance();

    public synchronized String processInput(String theInput) {
        if(theInput == null){
            return WELCOME;
        }
        String[] request = theInput.split("\\s+");
        String response = null;
        if(request[0].equals(PING)) {
            response = "PONG";
            return response;
        }
        // Wrong Format
        if(request.length != 3) {
            response = "Undefined Request.";
            return response;
        }

        if(request[1].equals(DEPOSIT)) {
            try {
                bank.depositMoney(cid, Integer.parseInt(request[2]));
                response = "SUCCESS: Remain " + bank.getBalance() + " in account.";
            } catch (NumberFormatException e) {
                response = "Amount of money must be an integer.";
            }
        } else if(request[1].equals(WITHDRAW)) {
            try {
                if(bank.withdrawMoney(cid, Integer.parseInt(request[2]))) {
                    response = "SUCCESS: Remain" + bank.getBalance() + "in account.";
                } else {
                    response = "FAIL: Balance is not enough. Your heart should be in work.";
                }
            } catch (NumberFormatException e) {
                response = "Amount of money must be an integer.";
            }
        } else {
            response = "Undefined Request.";
        }
        return response;
    }
}
