import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        BankOperations bank = new BankOperations();
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n===== Bank Menu =====");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. View Account Details");
            System.out.println("5. View Transaction History");
            System.out.println("6. Exit");
            System.out.print("Select Choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> bank.createAccount();
                case 2 -> bank.deposit();
                case 3 -> bank.withdraw();
                case 4 -> bank.viewAccount();
                case 5 -> bank.viewTransactions();
                case 6 -> System.out.println("✔ Thank you for banking with us!");
                default -> System.out.println("❌ Invalid Choice! Try Again.");
            }
        } while (choice != 6);
    }
}
