package team.group26.passiveReplica.utils;

import team.group26.passiveReplica.Bank;

import java.io.PrintWriter;
import java.util.TimerTask;

public class SyncBackUpProcessor extends TimerTask {
    private PrintWriter out;
    private String sid;
    Bank bank;

    public SyncBackUpProcessor(Bank bank) {
        this.bank = bank;
    }

    public SyncBackUpProcessor(PrintWriter out, Bank bank, String sid) {
        this.out = out;
        this.bank = bank;
        this.sid = sid;
    }

    @Override
    public void run(){
        System.out.println("[Checkpoint] send state to " + sid);
        out.println("checkpoint "+bank.getBalance());
    }
}
