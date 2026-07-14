package com.bank.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Account {
	private long accountNumber;
	private String accountHolderName;
	private String email;
	private String phone;
	private String accountType;
	private BigDecimal balance;
	private String pinHash;
	private String accountStatus;
	private Timestamp createdAt;
	
	public Account() {
		
	}
	
	public Account(String accountHolderName, String email, String phone,  String accountType, BigDecimal balance, String pinHash, String accountStatus) {
		this.accountHolderName =accountHolderName;
		this.email = email;
		this.phone = phone;
		this.accountType= accountType;
		this.balance = balance;
		this.pinHash= pinHash;
		this.accountStatus = accountStatus;
	}
	
	public long getAccountNumber() {
		return accountNumber;
	}
	public String getAccountHolderName() {
		return accountHolderName;
	}
	public String getEmail() {
		return email;
	}
	public String getPhone() {
		return phone;
	}
	public String getAccountType() {
		return accountType;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public String getPinHash() {
		return pinHash;
	}
	public String getAccountStatus() {
		return accountStatus;
	}
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	
	public void setAccountNumber(long accountNumber) {
		this.accountNumber = accountNumber;
	}
	public void setAccountHolderName(String accountHolderName) {
		this.accountHolderName = accountHolderName;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public void setAccountType(String account_Type) {
		this.accountType = account_Type;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public void setPinHash(String pinHash) {
		this.pinHash = pinHash;
	}
	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
}
