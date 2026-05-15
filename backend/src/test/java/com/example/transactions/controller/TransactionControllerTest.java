package com.example.transactions.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "app.csv.path=./target/test-transactions.csv"
})
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void resetCsv() throws Exception {
        Path path = Path.of("./target/test-transactions.csv").toAbsolutePath().normalize();
        Files.deleteIfExists(path);
    }

    @Test
    void getTransactionsReturnsListWithSeededRows() throws Exception {
        mockMvc.perform(get("/transactions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].accountHolderName").exists())
            .andExpect(jsonPath("$[0].status").exists());
    }

    @Test
    void getTransactionsEscapesHtmlSensitiveCharactersFromStoredValues() throws Exception {
        Path path = Path.of("./target/test-transactions.csv").toAbsolutePath().normalize();
        Files.createDirectories(path.getParent());
        Files.writeString(
            path,
            "Transaction Date,Account Number,Account Holder Name,Amount,Status\n"
                + "2025-05-12,1111-2222-3333,\"Smith, Jane \"\"JJ\"\"\",250.00,Pending\n",
            StandardCharsets.UTF_8
        );

        mockMvc.perform(get("/transactions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].accountHolderName").value("Smith, Jane \"JJ\""));
    }

    @Test
    void postTransactionCreatesAndReturnsTransactionWithStatus() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("transactionDate", "2025-05-12");
        request.put("accountNumber", "1111-2222-3333");
        request.put("accountHolderName", "Controller Test");
        request.put("amount", 250.00);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accountHolderName").value("Controller Test"))
            .andExpect(jsonPath("$.status").exists());
    }

    @Test
    void postTransactionAcceptsCommaAndQuotesInAccountHolderName() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("transactionDate", "2025-05-12");
        request.put("accountNumber", "1111-2222-3333");
        request.put("accountHolderName", "Smith, Jane \"JJ\"");
        request.put("amount", 250.00);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accountHolderName").value("Smith, Jane \"JJ\""));
    }

    @Test
    void postTransactionTrimsValuesBeforeSaving() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("transactionDate", "2025-05-12");
        request.put("accountNumber", "  ACCT_111-222  ");
        request.put("accountHolderName", "  Jane Doe  ");
        request.put("amount", 250.00);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accountNumber").value("ACCT_111-222"))
            .andExpect(jsonPath("$.accountHolderName").value("Jane Doe"));

        mockMvc.perform(get("/transactions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].accountNumber").value("ACCT_111-222"))
            .andExpect(jsonPath("$[0].accountHolderName").value("Jane Doe"));
    }

    @Test
    void postTransactionWithInvalidAccountNumberCharactersReturns400() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("transactionDate", "2025-05-12");
        request.put("accountNumber", "1111@2222");
        request.put("accountHolderName", "Valid Name");
        request.put("amount", 10.00);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors.accountNumber").exists());
    }

    @Test
    void postTransactionRandomlyAssignsValidStatus() throws Exception {
        Set<String> statuses = new HashSet<>();
        Set<String> validStatuses = Set.of("Pending", "Settled", "Failed");

        for (int i = 0; i < 120 && statuses.size() < 3; i++) {
            Map<String, Object> request = new HashMap<>();
            request.put("transactionDate", "2025-05-12");
            request.put("accountNumber", "0000-0000-0000");
            request.put("accountHolderName", "Random Test");
            request.put("amount", 10.00);

            MvcResult result = mockMvc.perform(post("/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

            String body = result.getResponse().getContentAsString();
            for (String s : validStatuses) {
                if (body.contains("\"status\":\"" + s + "\"")) {
                    statuses.add(s);
                    break;
                }
            }
        }

        assertThat(statuses).containsExactlyInAnyOrder("Pending", "Settled", "Failed");
    }

    @Test
    void postTransactionWithMissingFieldsReturns400() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("accountNumber", "1234-5678-9012");

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void postTransactionWithNegativeAmountReturns400() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("transactionDate", "2025-05-12");
        request.put("accountNumber", "1111-2222-3333");
        request.put("accountHolderName", "Bad Amount");
        request.put("amount", -5.00);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors.amount").exists());
    }

    @Test
    void postTransactionWithBlankAccountHolderReturns400() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("transactionDate", "2025-05-12");
        request.put("accountNumber", "1111-2222-3333");
        request.put("accountHolderName", "");
        request.put("amount", 10.00);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors.accountHolderName").exists());
    }
}
