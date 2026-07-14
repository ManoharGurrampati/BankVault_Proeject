package com.bank;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

import com.bank.model.Transaction;
import com.bank.service.BankService;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BankService bankService = new BankService();

        boolean running = true;

        while (running) {
            printMenu();
            int choice;

            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.\n");
                continue;
            }

            try {
                switch (choice) {
                    case 1:
                        openAccount(scanner, bankService);
                        break;
                    case 2:
                        deposit(scanner, bankService);
                        break;
                    case 3:
                        withdraw(scanner, bankService);
                        break;
                    case 4:
                        transfer(scanner, bankService);
                        break;
                    case 5:
                        checkBalance(scanner, bankService);
                        break;
                    case 6:
                        viewHistory(scanner, bankService);
                        break;
                    case 7:
                        closeAccount(scanner, bankService);
                        break;
                    case 8:
                        running = false;
                        System.out.println("Thank you for using the Bank Management System. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option. Please choose between 1-8.\n");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage() + "\n");
            }
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("===== Bank Management System =====");
        System.out.println("1. Open Account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Transfer Funds");
        System.out.println("5. Check Balance");
        System.out.println("6. View Transaction History");
        System.out.println("7. Close Account");
        System.out.println("8. Exit");
        System.out.print("Choose an option: ");
    }

    private static void openAccount(Scanner scanner, BankService bankService) throws Exception {
        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        System.out.print("Account type (SAVINGS/CURRENT): ");
        String type = scanner.nextLine().toUpperCase();

        System.out.print("Initial deposit: ");
        BigDecimal deposit = new BigDecimal(scanner.nextLine().trim());

        System.out.print("Set a 4-digit PIN: ");
        String pin = scanner.nextLine();

        long accountNumber = bankService.openAccount(name, email, phone, type, deposit, pin);
        System.out.println("Account created successfully! Your account number is: " + accountNumber + "\n");
    }

    private static void deposit(Scanner scanner, BankService bankService) throws Exception {
        System.out.print("Account number: ");
        long accountNumber = Long.parseLong(scanner.nextLine().trim());

        System.out.print("Amount to deposit: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine().trim());

        bankService.deposit(accountNumber, amount);
        System.out.println("Deposit successful!\n");
    }

    private static void withdraw(Scanner scanner, BankService bankService) throws Exception {
        System.out.print("Account number: ");
        long accountNumber = Long.parseLong(scanner.nextLine().trim());

        System.out.print("Amount to withdraw: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine().trim());

        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine();

        bankService.withdraw(accountNumber, amount, pin);
        System.out.println("Withdrawal successful!\n");
    }

    private static void transfer(Scanner scanner, BankService bankService) throws Exception {
        System.out.print("From account number: ");
        long fromAccount = Long.parseLong(scanner.nextLine().trim());

        System.out.print("To account number: ");
        long toAccount = Long.parseLong(scanner.nextLine().trim());

        System.out.print("Amount to transfer: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine().trim());

        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine();

        bankService.transfer(fromAccount, toAccount, amount, pin);
        System.out.println("Transfer successful!\n");
    }

    private static void checkBalance(Scanner scanner, BankService bankService) throws Exception {
        System.out.print("Account number: ");
        long accountNumber = Long.parseLong(scanner.nextLine().trim());

        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine();

        BigDecimal balance = bankService.checkBalance(accountNumber, pin);
        System.out.println("Current balance: " + balance + "\n");
    }

    private static void viewHistory(Scanner scanner, BankService bankService) throws Exception {
        System.out.print("Account number: ");
        long accountNumber = Long.parseLong(scanner.nextLine().trim());

        List<Transaction> transactions = bankService.viewTransactionHistory(accountNumber);

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.\n");
            return;
        }

        System.out.println("---- Transaction History ----");
        for (Transaction t : transactions) {
            System.out.println(t.getTransactionId() + " | " + t.getTransactionType() + " | "
                    + t.getAmount() + " | Balance after: " + t.getBalanceAfter()
                    + " | " + t.getTimeStamp());
        }
        System.out.println();
    }

    private static void closeAccount(Scanner scanner, BankService bankService) throws Exception {
        System.out.print("Account number: ");
        long accountNumber = Long.parseLong(scanner.nextLine().trim());

        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine();

        bankService.closeAccount(accountNumber, pin);
        System.out.println("Account closed successfully.\n");
    }
}