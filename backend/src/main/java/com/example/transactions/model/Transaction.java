package com.example.transactions.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Transaction {
    private LocalDate transactionDate;
    private String accountNumber;
    private String accountHolderName;
    private BigDecimal amount;
    private TransactionStatus status;

    public Transaction() {}

    public Transaction(LocalDate transactionDate, String accountNumber, String accountHolderName, BigDecimal amount,
        TransactionStatus status) {
        this.transactionDate = transactionDate;
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.amount = amount;
        this.status = status;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Transaction that = (Transaction) o;
        return Objects.equals(transactionDate, that.transactionDate) && Objects.equals(accountNumber,
            that.accountNumber) && Objects.equals(accountHolderName, that.accountHolderName)
            && Objects.equals(amount, that.amount) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionDate, accountNumber, accountHolderName, amount, status);
    }
}
