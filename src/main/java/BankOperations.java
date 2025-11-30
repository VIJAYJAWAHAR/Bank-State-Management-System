import java.sql.*;
import java.util.Scanner;

public class BankOperations {

    private Scanner scanner = new Scanner(System.in);

    public void createAccount() {
        System.out.print("Enter Account Holder Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Initial Deposit Amount: ");
        double balance = scanner.nextDouble();
        scanner.nextLine();

        String sql = "INSERT INTO accounts(name, balance) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, name);
            pst.setDouble(2, balance);
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                int accNo = rs.getInt(1);
                System.out.println("✔ Account Created Successfully! Your Account No: " + accNo);

                // Log initial deposit
                insertTransaction(accNo, "DEPOSIT", balance);
            }

        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public void deposit() {
        System.out.print("Enter Account Number: ");
        int accNo = scanner.nextInt();

        System.out.print("Enter Amount to Deposit: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        String sql = "UPDATE accounts SET balance = balance + ? WHERE acc_no = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setDouble(1, amount);
            pst.setInt(2, accNo);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                insertTransaction(accNo, "DEPOSIT", amount);
                System.out.println("✔ Deposit Successful!");
            } else {
                System.out.println("❌ Invalid Account Number");
            }

        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public void withdraw() {
        System.out.print("Enter Account Number: ");
        int accNo = scanner.nextInt();

        System.out.print("Enter Amount to Withdraw: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        String checkSql = "SELECT balance FROM accounts WHERE acc_no=?";
        String updateSql = "UPDATE accounts SET balance = balance - ? WHERE acc_no = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement checkPst = con.prepareStatement(checkSql)) {

            checkPst.setInt(1, accNo);
            ResultSet rs = checkPst.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Invalid Account Number");
                return;
            }

            double currentBalance = rs.getDouble("balance");
            if (amount > currentBalance) {
                System.out.println("❌ Insufficient Balance!");
                return;
            }

            try (PreparedStatement updatePst = con.prepareStatement(updateSql)) {
                updatePst.setDouble(1, amount);
                updatePst.setInt(2, accNo);
                updatePst.executeUpdate();

                insertTransaction(accNo, "WITHDRAWAL", amount);
                System.out.println("✔ Withdrawal Successful!");
            }

        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public void viewAccount() {
        System.out.print("Enter Account Number: ");
        int accNo = scanner.nextInt();
        scanner.nextLine();

        String sql = "SELECT * FROM accounts WHERE acc_no = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, accNo);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                System.out.println("\n----- Account Details -----");
                System.out.println("Account No   : " + rs.getInt("acc_no"));
                System.out.println("Holder Name  : " + rs.getString("name"));
                System.out.println("Balance      : " + rs.getDouble("balance"));
                System.out.println("---------------------------\n");
            } else {
                System.out.println("❌ Account Not Found");
            }

        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public void viewTransactions() {
        System.out.print("Enter Account Number: ");
        int accNo = scanner.nextInt();
        scanner.nextLine();

        String sql = "SELECT * FROM transactions WHERE acc_no = ? ORDER BY date DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, accNo);
            ResultSet rs = pst.executeQuery();

            System.out.println("\n----- Transaction History -----");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("ID: " + rs.getInt("trans_id") +
                        " | Type: " + rs.getString("type") +
                        " | Amount: " + rs.getDouble("amount") +
                        " | Date: " + rs.getTimestamp("date"));
            }
            if (!found) System.out.println("No transactions available.");
            System.out.println("-------------------------------\n");

        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    // Insert record into transactions table
    private void insertTransaction(int accNo, String type, double amount) {
        String sql = "INSERT INTO transactions(acc_no, type, amount) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, accNo);
            pst.setString(2, type);
            pst.setDouble(3, amount);
            pst.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("Transaction log failed: " + ex.getMessage());
        }
    }
}
