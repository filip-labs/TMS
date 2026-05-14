package com.example.transactions.service;

import com.example.transactions.dto.TransactionRequest;
import com.example.transactions.model.Transaction;
import com.example.transactions.model.TransactionStatus;
import com.example.transactions.repository.TransactionCsvRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionServiceTest {

    @TempDir
    Path tempDir;

    private TransactionCsvRepository repository;

    @BeforeEach
    void setUp() throws IOException {
        repository = new TransactionCsvRepository(tempDir.resolve("transactions.csv").toString());
        repository.init();
    }

    @Test
    void getAllTransactionsReturnsAllRowsFromCsv() throws IOException {
        TransactionService service = new TransactionService(repository, new Random(42));
        List<Transaction> all = service.getAllTransactions();
        assertThat(all).isNotEmpty();
    }

    @Test
    void getAllTransactionsAreSortedByDateDescending() throws IOException {
        TransactionService service = new TransactionService(repository, new Random(42));
        List<Transaction> all = service.getAllTransactions();
        for (int i = 1; i < all.size(); i++) {
            assertThat(all.get(i - 1).getTransactionDate())
                .isAfterOrEqualTo(all.get(i).getTransactionDate());
        }
    }

    @Test
    void createTransactionPersistsAndAssignsAValidStatus() throws IOException {
        TransactionService service = new TransactionService(repository, new Random(0));
        TransactionRequest request = new TransactionRequest();
        request.setTransactionDate(LocalDate.of(2025, 5, 1));
        request.setAccountNumber("9999-8888-7777");
        request.setAccountHolderName("Service Test");
        request.setAmount(new BigDecimal("123.45"));

        Transaction created = service.createTransaction(request);

        assertThat(created.getStatus()).isIn((Object[]) TransactionStatus.values());
        assertThat(created.getAccountHolderName()).isEqualTo("Service Test");
        assertThat(created.getAmount()).isEqualByComparingTo("123.45");

        List<Transaction> all = service.getAllTransactions();
        assertThat(all.getFirst().getAccountHolderName()).isEqualTo("Service Test");
    }

    @Test
    void createTransactionTrimsStringValuesBeforeSaving() throws IOException {
        TransactionService service = new TransactionService(repository, new Random(0));
        TransactionRequest request = new TransactionRequest();
        request.setTransactionDate(LocalDate.of(2025, 5, 1));
        request.setAccountNumber("  ACCT_123-456  ");
        request.setAccountHolderName("  Service Test  ");
        request.setAmount(new BigDecimal("55.10"));

        Transaction created = service.createTransaction(request);

        assertThat(created.getAccountNumber()).isEqualTo("ACCT_123-456");
        assertThat(created.getAccountHolderName()).isEqualTo("Service Test");

        List<Transaction> all = service.getAllTransactions();
        assertThat(all.getFirst().getAccountNumber()).isEqualTo("ACCT_123-456");
        assertThat(all.getFirst().getAccountHolderName()).isEqualTo("Service Test");
    }

    @Test
    void randomStatusAssignmentCoversAllValuesOverManyCalls() throws IOException {
        TransactionService service = new TransactionService(repository, new Random(1));
        Set<TransactionStatus> seen = new HashSet<>();

        for (int i = 0; i < 200 && seen.size() < TransactionStatus.values().length; i++) {
            TransactionRequest request = new TransactionRequest();
            request.setTransactionDate(LocalDate.of(2025, 5, 1));
            request.setAccountNumber("0000-0000-0000");
            request.setAccountHolderName("Iter Sample");
            request.setAmount(new BigDecimal("1.00"));
            seen.add(service.createTransaction(request).getStatus());
        }

        assertThat(seen).isEqualTo(EnumSet.allOf(TransactionStatus.class));
    }
}