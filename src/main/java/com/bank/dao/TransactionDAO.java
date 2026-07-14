package com.bank.dao;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.bank.model.Transaction;
import com.bank.util.DBConnection;
public class TransactionDAO {
	public long insertTransaction(Transaction transaction) throws SQLException {
		final String command = "Insert into transactions(account_number, transaction_type, amount, balance_after, related_account) values (?,?,?,?,?)";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement stmt = con.prepareStatement(command, Statement.RETURN_GENERATED_KEYS)) {

	        stmt.setLong(1, transaction.getAccountNumber());
	        stmt.setString(2, transaction.getTransactionType());
	        stmt.setBigDecimal(3, transaction.getAmount());
	        stmt.setBigDecimal(4, transaction.getBalanceAfter());
	        stmt.setObject(5, transaction.getRelatedAccount());

	        stmt.executeUpdate();

	        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                return generatedKeys.getLong(1);
	            } else {
	                throw new SQLException("Transaction insert failed, no ID obtained.");
	            }
	        }
	    }
    }
	
	public long insertTransaction(Connection con, Transaction transaction) throws SQLException {
	    final String command = "Insert into transactions(account_number, transaction_type, amount, balance_after, related_account) values (?,?,?,?,?)";

	    try (PreparedStatement stmt = con.prepareStatement(command, Statement.RETURN_GENERATED_KEYS)) {
	        stmt.setLong(1, transaction.getAccountNumber());
	        stmt.setString(2, transaction.getTransactionType());
	        stmt.setBigDecimal(3, transaction.getAmount());
	        stmt.setBigDecimal(4, transaction.getBalanceAfter());
	        stmt.setObject(5, transaction.getRelatedAccount());

	        stmt.executeUpdate();

	        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                return generatedKeys.getLong(1);
	            } else {
	                throw new SQLException("Transaction insert failed, no ID obtained.");
	            }
	        }
	    }
	}

    public List<Transaction> getTransactionsByAccount(long accountNumber) throws SQLException {
        final String command = "Select * from transactions where account_number = ?";

        List<Transaction> transactions = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(command)) {

            stmt.setLong(1, accountNumber);

            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    Transaction transaction = new Transaction();
                    
                    transaction.setTransactionId(result.getLong("transaction_id"));
                    transaction.setAccountNumber(result.getLong("account_number"));
                    transaction.setTransactionType(result.getString("transaction_type"));
                    transaction.setAmount(result.getBigDecimal("amount"));
                    transaction.setBalanceAfter(result.getBigDecimal("balance_after"));
                    transaction.setRelatedAccount((Long) result.getObject("related_account"));
                    transaction.setTimeStamp(result.getTimestamp("time_stamp"));

                    transactions.add(transaction);
                }
            }
        }

        return transactions;
    }

    public Transaction getTransactionById(long transactionId) throws SQLException {
    	final String command = "Select * from transactions where transaction_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(command)) {

            stmt.setLong(1, transactionId);

            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setTransactionId(result.getLong("transaction_id"));
                    transaction.setAccountNumber(result.getLong("account_number"));
                    transaction.setTransactionType(result.getString("transaction_type"));
                    transaction.setAmount(result.getBigDecimal("amount"));
                    transaction.setBalanceAfter(result.getBigDecimal("balance_after"));
                    transaction.setRelatedAccount((Long) result.getObject("related_account"));
                    transaction.setTimeStamp(result.getTimestamp("time_stamp"));

                    return transaction;
                } else {
                    return null;
                }
            }
        }
    }
}
