# Backend Documentation

This module contains the Spring Boot backend for the Transaction Management System. It exposes the REST API, validates incoming requests, assigns transaction status values, and persists transactions to a CSV file.

## Responsibilities

- Expose `GET /transactions` and `POST /transactions`
- Validate incoming transaction payloads
- Read transactions from CSV storage
- Append newly created transactions to the CSV file
- Return consistent JSON responses for success and error cases

## Technology and Runtime

| Item         | Value                   |
|--------------|-------------------------|
| Framework    | Spring Boot 3.5.14      |
| Language     | Java 21                 |
| Build tool   | Maven                   |
| Default port | `8080`                  |
| Base URL     | `http://localhost:8080` |

## Running the Backend

Build and run from the `backend/` directory.

### Install dependencies and run tests

```bash
cd backend
mvn clean install
```

### Start the application

```bash
cd backend
mvn spring-boot:run
```

The API starts at `http://localhost:8080`.

### Run the packaged JAR

```bash
cd backend
mvn clean package
java -jar target/transaction-management-0.0.1-SNAPSHOT.jar
```

## Configuration

Main configuration is in [`src/main/resources/application.properties`](./src/main/resources/application.properties).

| Property                   | Default value                                                                             | Notes                                                          |
|----------------------------|-------------------------------------------------------------------------------------------|----------------------------------------------------------------|
| `server.port`              | `8080`                                                                                    | Backend HTTP port                                              |
| `app.csv.path`             | `./data/transactions.csv`                                                                 | Relative to the working directory where the backend is started |
| `app.cors.allowed-origins` | `http://localhost:5173,http://127.0.0.1:5173,http://localhost:4173,http://127.0.0.1:4173` | Allowed local frontend origins                                 |

## CSV Storage

### File location

When you start the backend from the `backend/` directory, transactions are stored in:

`backend/data/transactions.csv`

If the file or its parent directory does not exist, the backend creates it automatically on startup and seeds it with example rows.

### CSV columns

The header is always:

```text
Transaction Date,Account Number,Account Holder Name,Amount,Status
```

| Column                | Description                                                         |
|-----------------------|---------------------------------------------------------------------|
| `Transaction Date`    | Transaction date in ISO format (`yyyy-MM-dd`)                       |
| `Account Number`      | Account identifier entered by the user                              |
| `Account Holder Name` | Account holder name entered by the user                             |
| `Amount`              | Transaction amount stored as a decimal number                       |
| `Status`              | Randomly assigned backend status: `Pending`, `Settled`, or `Failed` |

### How reading and writing work

- `TransactionCsvRepository` ensures the file exists during startup.
- `GET /transactions` reads every non-header, non-empty CSV row.
- `TransactionService` sorts transactions by date in descending order before returning them.
- `POST /transactions` appends a new row to the file after validation succeeds.
- Read and write methods are `synchronized` to keep file operations safe for concurrent requests.
- Account number, account holder name, and status are escaped when needed, so commas and quotes remain valid CSV data.

## API Endpoints

### Endpoint summary

| Method | Path            | Description                                              |
|--------|-----------------|----------------------------------------------------------|
| `GET`  | `/transactions` | Returns all stored transactions                          |
| `POST` | `/transactions` | Creates a new transaction and returns the created record |

### `GET /transactions`

Returns all transactions currently stored in the CSV file.

Example response:

```json
[
  {
    "transactionDate": "2025-03-10",
    "accountNumber": "4646-8282-1919",
    "accountHolderName": "Amanda Robinson",
    "amount": 300.50,
    "status": "Settled"
  },
  {
    "transactionDate": "2025-03-09",
    "accountNumber": "9876-5432-1011",
    "accountHolderName": "Christopher Davis",
    "amount": 124.75,
    "status": "Pending"
  }
]
```

### `POST /transactions`

Creates a new transaction. The backend assigns a random status from `Pending`, `Settled`, or `Failed`.

Example request:

```json
{
  "transactionDate": "2025-05-12",
  "accountNumber": "1234-5678-9012",
  "accountHolderName": "Jane Doe",
  "amount": 250.75
}
```

Example success response:

```json
{
  "transactionDate": "2025-05-12",
  "accountNumber": "1234-5678-9012",
  "accountHolderName": "Jane Doe",
  "amount": 250.75,
  "status": "Settled"
}
```

## Validation Rules

Incoming payloads are validated by `TransactionRequest`.

| Field               | Rules                                                                                            |
|---------------------|--------------------------------------------------------------------------------------------------|
| `transactionDate`   | Required. Must be a valid date in `yyyy-MM-dd` format.                                           |
| `accountNumber`     | Required. Max 30 characters. Allowed characters: letters, numbers, spaces, underscores, hyphens. |
| `accountHolderName` | Required. Max 100 characters. Allowed characters: letters, spaces, dots, apostrophes, hyphens.   |
| `amount`            | Required. Must be greater than zero.                                                             |

Additional behavior:

- String inputs are trimmed before persistence.
- Line breaks in stored CSV values are rejected.

## Error Responses

The backend returns JSON error payloads from `GlobalExceptionHandler`.

### Validation error example

```json
{
  "timestamp": "2025-05-12T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "transactionDate": "transactionDate is required",
    "amount": "amount must be greater than zero"
  }
}
```

### Common error cases

| Status | When it happens                                    | Response shape                              |
|--------|----------------------------------------------------|---------------------------------------------|
| `400`  | Bean validation failure                            | Includes `message` and field-level `errors` |
| `400`  | Malformed JSON or invalid field type               | Includes `message`, no field map            |
| `400`  | Invalid CSV-safe value such as line breaks         | Includes `message`, no field map            |
| `500`  | CSV read/write failure or unexpected backend error | Includes `message`, no field map            |

## Running Tests

```bash
cd backend
mvn test
```

Current automated coverage includes:

- `TransactionControllerTest`
- `TransactionServiceTest`
- `TransactionCsvRepositoryTest`

These tests now cover:

- random status assignment
- trimmed input persistence
- CSV escaping for commas and quotes
- malformed row skipping
- header-only file handling
- cleanly append behavior when the CSV file has no trailing newline

## Important Packages and Classes

| Class                                 | Responsibility                                                  |
|---------------------------------------|-----------------------------------------------------------------|
| `controller/TransactionController`    | Exposes REST endpoints                                          |
| `service/TransactionService`          | Orchestrates reads, creates transactions, assigns random status |
| `repository/TransactionCsvRepository` | Handles CSV initialization, parsing, and persistence            |
| `dto/TransactionRequest`              | Defines incoming payload validation rules                       |
| `dto/TransactionResponse`             | Defines API response shape                                      |
| `exception/GlobalExceptionHandler`    | Normalizes error responses                                      |
| `util/TransactionInputSanitizer`      | Trims incoming string values                                    |

## Postman Collection

Postman files are available in [`postman/`](./postman):

- [`postman/postman_collection.json`](./postman/postman_collection.json)

## Related Documentation

- [Project overview](../README.md)
- [Client documentation](../client/README.md)
