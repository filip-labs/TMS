package com.example.transactions.util;

public final class TransactionInputSanitizer {

    private TransactionInputSanitizer() {
    }

    public static String trim(String value) {
        return value == null ? null : value.strip();
    }
}
