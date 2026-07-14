package com.bank.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transaction {
	private long transactionId;
	private long accountNumber;
	private String transactionType;
	private BigDecimal amount;
	private BigDecimal balanceAfter;
	private Long relatedAccount;
	private Timestamp timeStamp;

	public Transaction() {

	}

	public Transaction(long accountNumber, String transactionType, BigDecimal amount, BigDecimal balanceAfter,
			Long relatedAccount) {
		this.accountNumber = accountNumber;
		this.transactionType = transactionType;
		this.amount = amount;
		this.balanceAfter = balanceAfter;
		this.relatedAccount = relatedAccount;
	}

	public long getTransactionId() {
		return transactionId;
	}

	public long getAccountNumber() {
		return accountNumber;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public Long getRelatedAccount() {
		return relatedAccount;
	}

	public Timestamp getTimeStamp() {
		return timeStamp;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

	public void setAccountNumber(long accountNumber) {
		this.accountNumber = accountNumber;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setRelatedAccount(Long relatedAccount) {
		this.relatedAccount = relatedAccount;
	}

	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}

	public BigDecimal getBalanceAfter() {
		return balanceAfter;
	}

	public void setBalanceAfter(BigDecimal balanceAfter) {
		this.balanceAfter = balanceAfter;
	}

}
