package com.example.transactions.service;

import com.example.transactions.dto.TransactionRequest;
import com.example.transactions.model.Transaction;
import com.example.transactions.model.TransactionStatus;
import com.example.transactions.repository.TransactionCsvRepository;
import com.example.transactions.util.TransactionInputSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Service
public class TransactionService {
    private final TransactionCsvRepository repository;
    private final Random random;

    @Autowired
    public TransactionService(TransactionCsvRepository repository) {
        this(repository, new Random());
    }

    public TransactionService(TransactionCsvRepository repository, Random random) {
        this.repository = repository;
        this.random = random;
    }

    public List<Transaction> getAllTransactions() throws IOException{
        List<Transaction> all = repository.findAll();
        all.sort(Comparator.comparing(Transaction::getTransactionDate).reversed());
        return all;
    }

    private Transaction createTransaction(TransactionRequest request) throws IOException {
        String accountNumber = TransactionInputSanitizer.trim(request.getAccountNumber());
        String accountHolderName = TransactionInputSanitizer.trim(request.getAccountHolderName());
        Transaction transaction = new Transaction(request.getTransactionDate(),accountNumber, accountHolderName, request.getAmount(), assignRandomStatus()
        );
        return repository.save(transaction);
    }

    private TransactionStatus assignRandomStatus() {
        TransactionStatus[] statuses = TransactionStatus.values();
        return statuses[random.nextInt(statuses.length)];
    }
}
