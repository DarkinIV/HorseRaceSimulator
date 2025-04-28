import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private double balance;
    private int totalBets;
    private int wonBets;

    public User(String username) {
        this.username = username;
        this.balance = 500.0; // Starting balance
        this.totalBets = 0;
        this.wonBets = 0;
    }

    public String getUsername() {
        return username;
    }

    public double getBalance() {
        return balance;
    }

    public void addBalance(double amount) {
        this.balance += amount;
    }

    public boolean placeBet(double amount) {
        if (amount <= 0 || amount > balance) {
            return false;
        }
        balance -= amount;
        totalBets++;
        return true;
    }

    public void winBet(double amount) {
        balance += amount;
        wonBets++;
    }

    public int getTotalBets() {
        return totalBets;
    }

    public int getWonBets() {
        return wonBets;
    }

    public double getWinRate() {
        return totalBets == 0 ? 0 : (double) wonBets / totalBets;
    }
}