package com.example.transactions.repository;

import com.example.transactions.model.Transaction;
import com.example.transactions.model.TransactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransactionCsvRepositoryTest {

    @TempDir
    Path tempDir;

    private TransactionCsvRepository repository;
    private Path csvPath;

    @BeforeEach
    void setUp() throws IOException {
        csvPath = tempDir.resolve("transactions.csv");
        repository = new TransactionCsvRepository(csvPath.toString());
        repository.init();
    }

    @Test
    void initCreatesFileWithHeaderAndSeedData() throws IOException {
        assertThat(Files.exists(csvPath)).isTrue();
        List<String> lines = Files.readAllLines(csvPath);
        assertThat(lines.getFirst()).isEqualTo(TransactionCsvRepository.CSV_HEADER);
        assertThat(lines).hasSizeGreaterThan(1);
    }

    @Test
    void findAllReturnsAllSeededTransactions() throws IOException {
        List<Transaction> all = repository.findAll();
        assertThat(all).hasSize(12);
        assertThat(all.getFirst().getAccountHolderName()).isEqualTo("Maria Johnson");
        assertThat(all.getFirst().getStatus()).isEqualTo(TransactionStatus.SETTLED);
    }

    @Test
    void saveAppendsTransactionToFile() throws IOException {
        int initialCount = repository.findAll().size();
        Transaction newTx = new Transaction(
            LocalDate.of(2025, 4, 1),
            "1111-2222-3333",
            "Test User",
            new BigDecimal("99.99"),
            TransactionStatus.PENDING
        );
        repository.save(newTx);

        List<Transaction> after = repository.findAll();
        assertThat(after).hasSize(initialCount + 1);
        Transaction last = after.getLast();
        assertThat(last.getAccountHolderName()).isEqualTo("Test User");
        assertThat(last.getAmount()).isEqualByComparingTo(new BigDecimal("99.99"));
        assertThat(last.getStatus()).isEqualTo(TransactionStatus.PENDING);
    }

    @Test
    void headerIsPreservedAfterSave() throws IOException {
        Transaction newTx = new Transaction(
            LocalDate.of(2025, 4, 2),
            "1234-5678-9012",
            "Header Test",
            new BigDecimal("10.00"),
            TransactionStatus.FAILED
        );
        repository.save(newTx);

        List<String> lines = Files.readAllLines(csvPath);
        assertThat(lines.getFirst()).isEqualTo(TransactionCsvRepository.CSV_HEADER);
    }

    @Test
    void saveRejectsLineBreaksInTextFields() {
        Transaction newTx = new Transaction(
            LocalDate.of(2025, 4, 2),
            "1234-5678-9012",
            "Header\nTest",
            new BigDecimal("10.00"),
            TransactionStatus.FAILED
        );

        assertThatThrownBy(() -> repository.save(newTx))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("accountHolderName must not contain line breaks");
    }

    @Test
    void findAllReturnsEmptyListForHeaderOnlyFile() throws IOException {
        Files.writeString(csvPath, TransactionCsvRepository.CSV_HEADER);

        List<Transaction> transactions = repository.findAll();

        assertThat(transactions).isEmpty();
    }

    @Test
    void findAllSkipsMalformedRowsAndParsesEscapedValues() throws IOException {
        Files.writeString(
            csvPath,
            TransactionCsvRepository.CSV_HEADER + System.lineSeparator()
                + "2025-05-01,1111-2222-3333,\"Smith, Jane \"\"JJ\"\"\",50.25,Pending" + System.lineSeparator()
                + "bad,row" + System.lineSeparator()
                + "2025-05-02,2222-3333-4444,Normal Name,12.00,Settled" + System.lineSeparator()
        );

        List<Transaction> transactions = repository.findAll();

        assertThat(transactions).hasSize(2);
        assertThat(transactions.getFirst().getAccountHolderName()).isEqualTo("Smith, Jane \"JJ\"");
        assertThat(transactions.getLast().getStatus()).isEqualTo(TransactionStatus.SETTLED);
    }

    @Test
    void saveEscapesCommaAndQuotesInAccountHolderName() throws IOException {
        Transaction newTx = new Transaction(
            LocalDate.of(2025, 4, 3),
            "1234-5678-9012",
            "Smith, Jane \"JJ\"",
            new BigDecimal("10.00"),
            TransactionStatus.SETTLED
        );

        repository.save(newTx);

        String csvContents = Files.readString(csvPath);
        assertThat(csvContents).contains("\"Smith, Jane \"\"JJ\"\"\"");
        assertThat(repository.findAll().getLast().getAccountHolderName()).isEqualTo("Smith, Jane \"JJ\"");
    }

    @Test
    void saveAppendsCleanlyToHeaderOnlyFileWithoutTrailingLineBreak() throws IOException {
        Files.writeString(csvPath, TransactionCsvRepository.CSV_HEADER);

        repository.save(new Transaction(
            LocalDate.of(2025, 4, 4),
            "9999-8888-7777",
            "Header Only",
            new BigDecimal("20.00"),
            TransactionStatus.FAILED
        ));

        List<String> lines = Files.readAllLines(csvPath);
        assertThat(lines).hasSize(2);
        assertThat(lines.get(1)).contains("Header Only");
    }
}
