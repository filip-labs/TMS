package com.example.transactions.repository;

import com.example.transactions.model.Transaction;
import com.example.transactions.model.TransactionStatus;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TransactionCsvRepository {

    public static final String CSV_HEADER = "Transaction Date,Account Number,Account Holder Name,Amount,Status";
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final int EXPECTED_COLUMN_COUNT = 5;

    private final Path csvPath;

    public TransactionCsvRepository(
        @Value("${app.csv.path:./data/transactions.csv}")
        String csvPath) {
        this.csvPath = Paths.get(csvPath).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() throws IOException {
        ensureFileExists();
    }

    public synchronized List<Transaction> findAll() throws IOException {
        ensureFileExists();

        List<Transaction> transactions = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            String header = reader.readLine();

            if (header == null) {
                return transactions;
            }

            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                try {
                    Transaction transaction = parseLine(line);

                    if (transaction != null) {
                        transactions.add(transaction);
                    }
                } catch (RuntimeException ignored) {
                    // Skip malformed rows so one bad CSV row does not break the whole API.
                }
            }
        }

        return transactions;
    }

    public synchronized Transaction save(Transaction transaction) throws IOException {
        ensureFileExists();
        validateTransaction(transaction);

        String line = toCsvLine(transaction);

        try (BufferedWriter writer = Files.newBufferedWriter(
            csvPath,
            StandardCharsets.UTF_8,
            StandardOpenOption.WRITE,
            StandardOpenOption.APPEND
        )) {
            appendLineBreakIfNeeded(writer);
            writer.write(line);
            writer.newLine();
        }

        return transaction;
    }

    private void ensureFileExists() throws IOException {
        if (csvPath.getParent() != null && Files.notExists(csvPath.getParent())) {
            Files.createDirectories(csvPath.getParent());
        }

        if (Files.notExists(csvPath) || Files.size(csvPath) == 0) {
            try (BufferedWriter writer = Files.newBufferedWriter(
                csvPath,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
            )) {
                writer.write(CSV_HEADER);
                writer.newLine();
                writeSeedData(writer);
            }
        }
    }

    private void writeSeedData(BufferedWriter writer) throws IOException {
        String[] seedRows = {
            "2025-03-01,7289-3445-1121,Maria Johnson,150.00,Settled",
            "2025-03-02,1122-3456-7890,John Smith,75.50,Pending",
            "2025-03-03,3344-5566-7788,Robert Chen,220.25,Settled",
            "2025-03-04,8899-0011-2233,Sarah Williams,310.75,Failed",
            "2025-03-04,9988-7766-5544,David Garcia,45.99,Pending",
            "2025-03-05,2233-4455-6677,Emily Taylor,500.00,Settled",
            "2025-03-06,1357-2468-9012,Michael Brown,99.95,Settled",
            "2025-03-07,5551-2345-6789,Jennifer Lee,175.25,Pending",
            "2025-03-08,7890-1234-5678,Thomas Wilson,62.50,Failed",
            "2025-03-08,1212-3434-5656,Jessica Martin,830.00,Settled",
            "2025-03-09,9876-5432-1011,Christopher Davis,124.75,Pending",
            "2025-03-10,4646-8282-1919,Amanda Robinson,300.50,Settled"
        };

        for (String row : seedRows) {
            writer.write(row);
            writer.newLine();
        }
    }

    private String toCsvLine(Transaction transaction) {
        String accountNumber = requireSingleLine(transaction.getAccountNumber(), "accountNumber");
        String accountHolderName = requireSingleLine(transaction.getAccountHolderName(), "accountHolderName");
        String status = requireSingleLine(transaction.getStatus().getDisplayName(), "status");

        return String.join(",",
            transaction.getTransactionDate().format(DATE_FORMAT),
            escape(accountNumber),
            escape(accountHolderName),
            transaction.getAmount().toPlainString(),
            escape(status)
        );
    }

    private Transaction parseLine(String line) {
        String[] parts = splitCsvLine(line);

        if (parts.length != EXPECTED_COLUMN_COUNT) {
            return null;
        }

        LocalDate date = LocalDate.parse(parts[0].trim(), DATE_FORMAT);
        String accountNumber = parts[1].trim();
        String accountHolderName = parts[2].trim();
        BigDecimal amount = new BigDecimal(parts[3].trim());
        TransactionStatus status = TransactionStatus.fromDisplayName(parts[4].trim());

        return new Transaction(date, accountNumber, accountHolderName, amount, status);
    }

    private String[] splitCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);

            if (character == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (character == ',' && !inQuotes) {
                tokens.add(current.toString());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }

        if (inQuotes) {
            throw new IllegalArgumentException("Malformed CSV line: unclosed quote!");
        }

        tokens.add(current.toString());

        return tokens.toArray(new String[0]);
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }

        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }

    private String requireSingleLine(String value, String fieldName) {
        if (value == null) {
            return "";
        }

        if (value.contains("\n") || value.contains("\r")) {
            throw new IllegalArgumentException(fieldName + " must not contain line breaks!");
        }

        return value;
    }

    private void validateTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction must not be null!");
        }

        if (transaction.getTransactionDate() == null) {
            throw new IllegalArgumentException("Transaction date must not be null!");
        }

        if (transaction.getAccountNumber() == null || transaction.getAccountNumber().isBlank()) {
            throw new IllegalArgumentException("Account number must not be blank!");
        }

        if (transaction.getAccountHolderName() == null || transaction.getAccountHolderName().isBlank()) {
            throw new IllegalArgumentException("Account holder name must not be blank!");
        }

        if (transaction.getAmount() == null) {
            throw new IllegalArgumentException("Amount must not be null!");
        }

        if (transaction.getStatus() == null) {
            throw new IllegalArgumentException("Status must not be null!");
        }
    }

    private void appendLineBreakIfNeeded(BufferedWriter writer) throws IOException {
        if (Files.size(csvPath) == 0 || endsWithLineBreak()) {
            return;
        }

        writer.newLine();
    }

    private boolean endsWithLineBreak() throws IOException {
        long size = Files.size(csvPath);

        if (size == 0) {
            return false;
        }

        try (SeekableByteChannel channel = Files.newByteChannel(csvPath, StandardOpenOption.READ)) {
            channel.position(size - 1);
            ByteBuffer buffer = ByteBuffer.allocate(1);
            channel.read(buffer);
            buffer.flip();

            byte lastByte = buffer.get();
            return lastByte == '\n' || lastByte == '\r';
        }
    }
}
