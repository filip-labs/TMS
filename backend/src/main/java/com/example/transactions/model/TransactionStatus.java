package com.example.transactions.model;

public enum TransactionStatus {
    PENDING("Pending"),
    SETTLED("Settled"),
    FAILED("Failed");

    private final String displayName;

    TransactionStatus(String displayName) {
        this.displayName = displayName;
    }

    public static TransactionStatus fromDisplayName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Transaction status must not be blank!");
        }

        String normalizedValue = value.trim();

        for (TransactionStatus status : values()) {
            if (status.displayName.equalsIgnoreCase(normalizedValue)
                || status.name().equalsIgnoreCase(normalizedValue)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Unknown transaction status: " + value);
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}