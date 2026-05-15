package com.example.transactions.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionRequest {
    private static final String ACCOUNT_NUMBER_PATTERN = "^[\\p{L}\\p{N} _-]+$";

    @NotNull(message = "TransactionDate is required!")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate transactionDate;

    @NotBlank(message = "AccountNumber is required!")
    @Size(max = 30,
          message = "AccountNumber must be at most 30 characters!")
    @Pattern(
        regexp = ACCOUNT_NUMBER_PATTERN,
        message = "AccountNumber may contain only letters, numbers, spaces underscores!"
    )
    private String accountNumber;

    @NotBlank(message = "AccountHolderName is required!")
    @Size(max = 100,
          message = "AccountHolderName must be at most 100 characters!")
    private String accountHolderName;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.00",
                inclusive = false,
                message = "amount must be greater than zero")
    private BigDecimal amount;

    public TransactionRequest() {
        // Required by Jackson to instantiate the request DTO.
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

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
