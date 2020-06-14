package team.group26.server;

public class Bank {

    private int balance=0;

    private static Bank bank;

    //make the constructor private
    private Bank(){ }

    public static Bank getInstance() {
        if (bank == null) {
            synchronized (Bank.class) {
                if (bank == null) {
                    bank = new Bank();
                }
            }
        }
        return bank;
    }

    public synchronized void depositMoney(String cID,int money) {
        balance+=money;
    }

    public synchronized boolean withdrawMoney(String cID, int money)
    {
        if(balance-money<0)
        {
            return false;
        }else {
            balance-=money;
            return true;
        }

    }

    public int getBalance()
    {
        return balance;
    }

}
