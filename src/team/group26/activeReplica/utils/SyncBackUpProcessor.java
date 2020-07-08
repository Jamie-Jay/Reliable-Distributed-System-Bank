package team.group26.activeReplica.utils;

import team.group26.activeReplica.Bank;

import java.io.PrintWriter;
import java.util.TimerTask;

public class SyncBackUpProcessor extends TimerTask {
    private PrintWriter out;
    Bank bank;

    public SyncBackUpProcessor(Bank bank) {
        this.bank = bank;
    }

    public SyncBackUpProcessor(PrintWriter out, Bank bank) {
        this.out = out;
        this.bank = bank;
    }

    @Override
    public void run(){
        //System.out.println("Current Bal = " + bank.getBalance());
        out.println(bank.getBalance());
    }
}
