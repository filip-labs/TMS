package com.example.transactions.controller;

import com.example.transactions.dto.TransactionRequest;
import com.example.transactions.dto.TransactionResponse;
import com.example.transactions.model.Transaction;
import com.example.transactions.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAll() throws IOException {
        List<TransactionResponse> body = service.getAllTransactions().stream()
            .map(TransactionResponse::from)
            .toList();
        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(
        @Valid
        @RequestBody
        TransactionRequest request) throws IOException {
        Transaction created = service.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(TransactionResponse.from(created));
    }
}
