package com.example.transactions.dto;

import com.example.transactions.model.Transaction;
import com.example.transactions.util.TransactionInputSanitizer;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate transactionDate,
    String accountNumber,
    String accountHolderName,
    BigDecimal amount,
    String status
) {
    public static TransactionResponse from(Transaction t) {
        return new TransactionResponse(
            t.getTransactionDate(),
            TransactionInputSanitizer.escapeHtml(t.getAccountNumber()),
            TransactionInputSanitizer.escapeHtml(t.getAccountHolderName()),
            t.getAmount(),
            t.getStatus() == null ? null : t.getStatus().getDisplayName()
        );
    }
}
