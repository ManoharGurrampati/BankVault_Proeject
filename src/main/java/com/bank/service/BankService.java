package com.bank.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.bank.dao.AccountDAO;
import com.bank.dao.TransactionDAO;
import com.bank.exception.AccountNotFoundException;
import com.bank.exception.InsufficientBalanceException;
import com.bank.exception.InvalidPinException;
import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.util.DBConnection;
import com.bank.util.PasswordUtil;

public class BankService {
	
	private AccountDAO accountDAO = new AccountDAO();
	private TransactionDAO transactionDAO = new TransactionDAO();
	
	public long openAccount(String name, String email,String phone,String accountType,BigDecimal initialDeposit,String pin) throws SQLException{
		 if (name == null || name.isBlank()) {
		        throw new IllegalArgumentException("Account holder name cannot be empty");
		    }
		    if (email == null || email.isBlank()) {
		        throw new IllegalArgumentException("Email cannot be empty");
		    }
		    if (initialDeposit == null || initialDeposit.compareTo(BigDecimal.ZERO) < 0) {
		        throw new IllegalArgumentException("Initial deposit cannot be negative");
		    }

		    Account existing = accountDAO.getAccountByEmail(email);
		    if (existing != null) {
		        throw new IllegalArgumentException("An account with this email already exists");
		    }

		    String pinHash = PasswordUtil.hashPin(pin);

		    Account newAccount = new Account();
		    newAccount.setAccountHolderName(name);
		    newAccount.setEmail(email);
		    newAccount.setPhone(phone);
		    newAccount.setAccountType(accountType);
		    newAccount.setBalance(initialDeposit);
		    newAccount.setPinHash(pinHash);
		    newAccount.setAccountStatus("ACTIVE");

		    return accountDAO.createAccount(newAccount);
	}
	
	public void deposit(long accountNumber, BigDecimal amount) throws SQLException, AccountNotFoundException {

	    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
	        throw new IllegalArgumentException("Deposit amount must be positive");
	    }

	    Account account = accountDAO.getAccountByNumber(accountNumber);
	    if (account == null) {
	        throw new AccountNotFoundException("No account found with number: " + accountNumber);
	    }

	    if (!"ACTIVE".equals(account.getAccountStatus())) {
	        throw new IllegalStateException("Cannot deposit into a non-active account");
	    }

	    BigDecimal newBalance = account.getBalance().add(amount);
	    accountDAO.updateBalance(accountNumber, newBalance);

	    Transaction transaction = new Transaction();
	    transaction.setAccountNumber(accountNumber);
	    transaction.setTransactionType("DEPOSIT");
	    transaction.setAmount(amount);
	    transaction.setBalanceAfter(newBalance);
	    transaction.setRelatedAccount(null);

	    transactionDAO.insertTransaction(transaction);
	}
	
	public void withdraw(long accountNumber, BigDecimal amount, String pin)
	        throws SQLException, AccountNotFoundException, InsufficientBalanceException, InvalidPinException {

	    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
	        throw new IllegalArgumentException("Withdrawal amount must be positive");
	    }

	    Account account = accountDAO.getAccountByNumber(accountNumber);
	    if (account == null) {
	        throw new AccountNotFoundException("No account found with number: " + accountNumber);
	    }

	    if (!"ACTIVE".equals(account.getAccountStatus())) {
	        throw new IllegalStateException("Cannot withdraw from a non-active account");
	    }

	    if (!PasswordUtil.verifyPin(pin, account.getPinHash())) {
	        throw new InvalidPinException("Incorrect PIN");
	    }

	    BigDecimal minimumBalance = new BigDecimal("500.00");
	    BigDecimal balanceAfterWithdrawal = account.getBalance().subtract(amount);

	    if (balanceAfterWithdrawal.compareTo(minimumBalance) < 0) {
	        throw new InsufficientBalanceException("Withdrawal would drop balance below minimum required balance of " + minimumBalance);
	    }

	    accountDAO.updateBalance(accountNumber, balanceAfterWithdrawal);

	    Transaction transaction = new Transaction();
	    transaction.setAccountNumber(accountNumber);
	    transaction.setTransactionType("WITHDRAW");
	    transaction.setAmount(amount);
	    transaction.setBalanceAfter(balanceAfterWithdrawal);
	    transaction.setRelatedAccount(null);

	    transactionDAO.insertTransaction(transaction);
	}
	
	public void transfer(long fromAccount, long toAccount, BigDecimal amount, String pin)
	        throws SQLException, AccountNotFoundException, InsufficientBalanceException, InvalidPinException {

	    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
	        throw new IllegalArgumentException("Transfer amount must be positive");
	    }

	    if (fromAccount == toAccount) {
	        throw new IllegalArgumentException("Cannot transfer to the same account");
	    }

	    try (Connection con = DBConnection.getConnection()) {
	        con.setAutoCommit(false);

	        try {
	            Account source = accountDAO.getAccountByNumber(fromAccount);
	            if (source == null) {
	                throw new AccountNotFoundException("Source account not found: " + fromAccount);
	            }

	            Account destination = accountDAO.getAccountByNumber(toAccount);
	            if (destination == null) {
	                throw new AccountNotFoundException("Destination account not found: " + toAccount);
	            }

	            if (!PasswordUtil.verifyPin(pin, source.getPinHash())) {
	                throw new InvalidPinException("Incorrect PIN");
	            }

	            BigDecimal minimumBalance = new BigDecimal("500.00");
	            BigDecimal sourceBalanceAfter = source.getBalance().subtract(amount);

	            if (sourceBalanceAfter.compareTo(minimumBalance) < 0) {
	                throw new InsufficientBalanceException("Transfer would drop source balance below minimum required balance");
	            }

	            BigDecimal destinationBalanceAfter = destination.getBalance().add(amount);

	            accountDAO.updateBalance(con, fromAccount, sourceBalanceAfter);
	            accountDAO.updateBalance(con, toAccount, destinationBalanceAfter);

	            Transaction debit = new Transaction();
	            debit.setAccountNumber(fromAccount);
	            debit.setTransactionType("TRANSFER_OUT");
	            debit.setAmount(amount);
	            debit.setBalanceAfter(sourceBalanceAfter);
	            debit.setRelatedAccount(toAccount);
	            transactionDAO.insertTransaction(con, debit);

	            Transaction credit = new Transaction();
	            credit.setAccountNumber(toAccount);
	            credit.setTransactionType("TRANSFER_IN");
	            credit.setAmount(amount);
	            credit.setBalanceAfter(destinationBalanceAfter);
	            credit.setRelatedAccount(fromAccount);
	            transactionDAO.insertTransaction(con, credit);

	            con.commit();

	        } catch (Exception e) {
	            con.rollback();
	            throw e;
	        }
	    }
	}
	
	public BigDecimal checkBalance(long accountNumber, String pin)
	        throws SQLException, AccountNotFoundException, InvalidPinException {

	    Account account = accountDAO.getAccountByNumber(accountNumber);
	    if (account == null) {
	        throw new AccountNotFoundException("No account found with number: " + accountNumber);
	    }

	    if (!PasswordUtil.verifyPin(pin, account.getPinHash())) {
	        throw new InvalidPinException("Incorrect PIN");
	    }

	    return account.getBalance();
	}
	
	public void closeAccount(long accountNumber, String pin)
	        throws SQLException, AccountNotFoundException, InvalidPinException {

	    Account account = accountDAO.getAccountByNumber(accountNumber);
	    if (account == null) {
	        throw new AccountNotFoundException("No account found with number: " + accountNumber);
	    }

	    if (!PasswordUtil.verifyPin(pin, account.getPinHash())) {
	        throw new InvalidPinException("Incorrect PIN");
	    }

	    BigDecimal remainingBalance = account.getBalance();

	    if (remainingBalance.compareTo(BigDecimal.ZERO) > 0) {
	        Transaction finalWithdrawal = new Transaction();
	        finalWithdrawal.setAccountNumber(accountNumber);
	        finalWithdrawal.setTransactionType("WITHDRAW");
	        finalWithdrawal.setAmount(remainingBalance);
	        finalWithdrawal.setBalanceAfter(BigDecimal.ZERO);
	        finalWithdrawal.setRelatedAccount(null);

	        transactionDAO.insertTransaction(finalWithdrawal);
	        accountDAO.updateBalance(accountNumber, BigDecimal.ZERO);
	    }

	    accountDAO.updateStatus(accountNumber, "CLOSED");
	}
	
	public List<Transaction> viewTransactionHistory(long accountNumber) throws SQLException {
	    return transactionDAO.getTransactionsByAccount(accountNumber);
	}
	
}
