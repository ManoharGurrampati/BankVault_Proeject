package com.bank.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.bank.model.Account;
import com.bank.util.DBConnection;

import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

	public long createAccount(Account account) throws SQLException {
		final String command = "Insert into accounts(account_holder_name, email, phone, account_type, balance, pin_hash, account_status) values (?,?,?,?,?,?,?)";
		try (Connection con = DBConnection.getConnection();
				PreparedStatement stmt = con.prepareStatement(command, Statement.RETURN_GENERATED_KEYS)) {

			stmt.setString(1, account.getAccountHolderName());
			stmt.setString(2, account.getEmail());
			stmt.setString(3, account.getPhone());
			stmt.setString(4, account.getAccountType());
			stmt.setBigDecimal(5, account.getBalance());
			stmt.setString(6, account.getPinHash());
			stmt.setString(7, account.getAccountStatus());

			stmt.executeUpdate();

			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getLong(1);
				} else {
					throw new SQLException("Account creation failed, no ID obtained.");
				}
			}
		}
	}

	public Account getAccountByNumber(long accountNumber) throws SQLException {
		final String command = "Select * from accounts where account_number = ?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement stmt = con.prepareStatement(command)) {
			stmt.setLong(1, accountNumber);

			try (ResultSet result = stmt.executeQuery()) {
				if (result.next()) {
					Account account = new Account();
					account.setAccountNumber(result.getLong("account_number"));
					account.setAccountHolderName(result.getString("account_holder_name"));
					account.setEmail(result.getString("email"));
					account.setPhone(result.getString("phone"));
					account.setAccountType(result.getString("account_type"));
					account.setBalance(result.getBigDecimal("balance"));
					account.setPinHash(result.getString("pin_hash"));
					account.setAccountStatus(result.getString("account_status"));
					account.setCreatedAt(result.getTimestamp("created_at"));

					return account;
				} else {
					return null;
				}
			}
		}
	}

	public Account getAccountByEmail(String email) throws SQLException {

		final String command = "Select * from accounts where email = ?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement stmt = con.prepareStatement(command)) {
			stmt.setString(1, email);

			try (ResultSet result = stmt.executeQuery()) {
				if (result.next()) {
					Account account = new Account();
					account.setAccountNumber(result.getLong("account_number"));
					account.setAccountHolderName(result.getString("account_holder_name"));
					account.setEmail(result.getString("email"));
					account.setPhone(result.getString("phone"));
					account.setAccountType(result.getString("account_type"));
					account.setBalance(result.getBigDecimal("balance"));
					account.setPinHash(result.getString("pin_hash"));
					account.setAccountStatus(result.getString("account_status"));
					account.setCreatedAt(result.getTimestamp("created_at"));

					return account;
				} else {
					return null;
				}
			}
		}
	}

	public boolean updateBalance(long accountNumber, BigDecimal newBalance) throws SQLException {

		final String command = "update accounts set balance = ? where account_number = ?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement stmt = con.prepareStatement(command)) {

			stmt.setBigDecimal(1, newBalance);
			stmt.setLong(2, accountNumber);

			int rowsAffected = stmt.executeUpdate();

			return rowsAffected == 1;
		}
	}
	
	public boolean updateBalance(Connection con, long accountNumber, BigDecimal newBalance) throws SQLException {
	    final String command = "update accounts set balance = ? where account_number = ?";

	    try (PreparedStatement stmt = con.prepareStatement(command)) {
	        stmt.setBigDecimal(1, newBalance);
	        stmt.setLong(2, accountNumber);
	        return stmt.executeUpdate() == 1;
	    }
	}

	public boolean updateStatus(long accountNumber, String status) throws SQLException {

		final String command = "update accounts set account_status = ? where account_number = ?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement stmt = con.prepareStatement(command)) {

			stmt.setString(1, status);
			stmt.setLong(2, accountNumber);

			int rowsAffected = stmt.executeUpdate();

			return rowsAffected == 1;
		}
	}

	public List<Account> getAllAccounts() throws SQLException {

		final String command = "Select * from accounts";

		List<Account> accounts = new ArrayList<>();

		try (Connection con = DBConnection.getConnection();
				PreparedStatement stmt = con.prepareStatement(command);
				ResultSet result = stmt.executeQuery();) {
			while (result.next()) {
				Account account = new Account();
				account.setAccountNumber(result.getLong("account_number"));
				account.setAccountHolderName(result.getString("account_holder_name"));
				account.setEmail(result.getString("email"));
				account.setPhone(result.getString("phone"));
				account.setAccountType(result.getString("account_type"));
				account.setBalance(result.getBigDecimal("balance"));
				account.setPinHash(result.getString("pin_hash"));
				account.setAccountStatus(result.getString("account_status"));
				account.setCreatedAt(result.getTimestamp("created_at"));
				accounts.add(account);
			}
		}
		return accounts;
	}

	public boolean accountExists(long accountNumber) throws SQLException {

		final String command = "select 1 from accounts where account_number = ?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement stmt = con.prepareStatement(command)) {
			stmt.setLong(1, accountNumber);

			try (ResultSet result = stmt.executeQuery()) {
				return result.next();
			}
		}
	}
}
