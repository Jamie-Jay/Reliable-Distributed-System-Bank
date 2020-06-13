public class Bank {

    private int balance=0;

    private static Bank bank;

    //make the constructor private
    private Bank(){ }

    public static synchronized Bank getInstance() {
        if (bank == null) {
            bank = new Bank();
        }
        return bank;
    }

    public synchronized void depositMoney(String cID,int money) {
        balance+=money;
        System.out.println("The client "+cID +" deposits"+money);
    }

    public synchronized void withdrawMoney(String cID, int money)
    {
        if(balance-money<0)
        {
            System.out.println("Insufficient balance");
            return;
        }else {
            balance-=money;
            System.out.println("The client"+ cID +" withdraw"+money);
        }

    }

    public int getBalance()
    {
        System.out.println("The balance is "+balance);
        return balance;
    }

}
