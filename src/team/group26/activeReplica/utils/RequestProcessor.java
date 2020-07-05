package team.group26.activeReplica.utils;

import team.group26.activeReplica.Bank;

public class RequestProcessor {
    // Request Type
    private static final String DEPOSIT = "DEP";
    private static final String WITHDRAW = "WTD";
    private static final String PING = "PING";
    private static final String WELCOME = "Hello, please apply the request <cmd> <amount>";

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
        if(request.length != 5) {
            response = String.format("%s Undefined Request", cid);
            return response;
        }

        if(request[3].equals(DEPOSIT)) {
            try {
                bank.depositMoney(cid, Integer.parseInt(request[4]));
                response = String.format("%s %s SUCCESS: Remain %d in account.", cid, request[2], bank.getBalance());
            } catch (NumberFormatException e) {
                response = String.format("%s %s Undefined Request.", cid, request[2]);
            }
        } else if(request[3].equals(WITHDRAW)) {
            try {
                if(bank.withdrawMoney(cid, Integer.parseInt(request[4]))) {
                    response = String.format("%s %s SUCCESS: Remain %d in account.", cid, request[2], bank.getBalance());
                } else {
                    response = String.format("%s %s FAIL: Balance %d is not enough. Your heart should be in work.", cid,
                            request[2], bank.getBalance());
                }
            } catch (NumberFormatException e) {
                response = String.format("%s %s Amount of money must be an integer.", cid, request[2]);
            }
        } else {
            response = String.format("%s %s Undefined Request", cid, request[2]);
        }
        return response;
    }
}
