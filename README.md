# Transaction Management System

This repository contains a small full-stack coding test project with:

- a Spring Boot backend API
- a React + Vite frontend
- CSV file storage instead of a database

The app lists transactions from a CSV file and lets a user create a new transaction from the UI. The backend assigns the
transaction status randomly as `Pending`, `Settled`, or `Failed`.

## Project Overview

The project is split into two runnable parts:

- `backend/` contains the Java Spring Boot API
- `client/client/` contains the React frontend

There is an extra outer `client/` folder that only holds documentation and IDE files. The actual Vite app is inside
`client/client/`.

## Repository Structure

```text
TMS/
├── backend/
│   ├── data/transactions.csv
│   ├── pom.xml
│   ├── postman/postman_collection.json
│   └── src/
├── client/
│   ├── README.md
│   └── client/
│       ├── .env.example
│       ├── package.json
│       ├── vite.config.js
│       └── src/
└── README.md
```

## Prerequisites

Install these tools first:

| Tool    | Required Version   | Why You Need It                |
|---------|--------------------|--------------------------------|
| Java    | 21                 | Runs the Spring Boot backend   |
| Maven   | 3.8+               | Builds and runs the backend    |
| Node.js | 18+ or 20+         | Runs the frontend toolchain    |
| npm     | 9+                 | Installs frontend dependencies |
| Git     | Any recent version | Clones the repository          |

Verified locally during review:

- Java `21.0.10`
- Maven `3.8.7`
- Node `v22.22.3`
- npm `10.9.8`

## Installation

1. Clone the repository:

```bash
git clone <your-repository-url>
cd TMS
```

2. Install backend dependencies and run tests once:

```bash
cd backend
mvn test
cd ..
```

3. Install frontend dependencies:

```bash
cd client/client
npm install
cd ../..
```

## Configuration

### Backend

Backend configuration lives in `backend/src/main/resources/application.properties`.

Default settings:

- backend port: `8080`
- CSV path: `./data/transactions.csv`
- allowed frontend origins:
  - `http://localhost:5173`
  - `http://127.0.0.1:5173`
  - `http://localhost:4173`
  - `http://127.0.0.1:4173`

Important note:

- `app.csv.path=./data/transactions.csv` is resolved relative to the directory where you start the backend.
- If you run the backend from `backend/`, the CSV file used is `backend/data/transactions.csv`.

### Frontend

The frontend uses `VITE_API_BASE_URL`.

Default behavior:

- if `VITE_API_BASE_URL` is not set, the frontend calls `http://localhost:8080`

Create a frontend env file from the example:

```bash
cd client/client
cp .env.example .env
```

Example `.env`:

```env
VITE_API_BASE_URL=http://localhost:8080
```

## How to Run

Open two terminals.

### Run Backend

From the repository root:

```bash
cd backend
mvn spring-boot:run
```

Backend URL:

```text
http://localhost:8080
```

### Run Frontend

From the repository root:

```bash
cd client/client
npm run dev
```

Frontend URL:

```text
http://localhost:5173
```

## API Documentation

Base URL:

```text
http://localhost:8080
```

### GET `/transactions`

Reads all transactions from the CSV file and returns them as JSON.

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

### POST `/transactions`

Creates a transaction and appends it to the CSV file.

The frontend does not send `status`. The backend assigns it randomly.

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
  "status": "Pending"
}
```

Example validation error response:

```json
{
  "timestamp": "2026-05-15T09:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "amount": "amount must be greater than zero"
  }
}
```

## Frontend Features

The frontend currently includes:

- transaction table with all required columns
- `Add Transaction` button
- modal form with:
  - transaction date
  - account number
  - account holder name
  - amount
- backend submission through `POST /transactions`
- automatic table refresh after successful creation
- color-coded status badges:
  - `Pending` -> yellow
  - `Settled` -> green
  - `Failed` -> red
- understandable error messages for load failures and form submission failures

## CSV Storage

Transactions are stored in:

```text
backend/data/transactions.csv
```

CSV header:

```text
Transaction Date,Account Number,Account Holder Name,Amount,Status
```

Current sample data:

```csv
Transaction Date,Account Number,Account Holder Name,Amount,Status
2025-03-01,7289-3445-1121,Maria Johnson,150.00,Settled
2025-03-02,1122-3456-7890,John Smith,75.50,Pending
2025-03-03,3344-5566-7788,Robert Chen,220.25,Settled
2025-03-04,8899-0011-2233,Sarah Williams,310.75,Failed
2025-03-04,9988-7766-5544,David Garcia,45.99,Pending
2025-03-05,2233-4455-6677,Emily Taylor,500.00,Settled
2025-03-06,1357-2468-9012,Michael Brown,99.95,Settled
2025-03-07,5551-2345-6789,Jennifer Lee,175.25,Pending
2025-03-08,7890-1234-5678,Thomas Wilson,62.50,Failed
2025-03-08,1212-3434-5656,Jessica Martin,830.00,Settled
2025-03-09,9876-5432-1011,Christopher Davis,124.75,Pending
2025-03-10,4646-8282-1919,Amanda Robinson,300.50,Settled
```

CSV behavior during review:

- missing file -> recreated automatically
- empty file -> recreated with header and sample rows
- header-only file -> handled without crashing
- malformed rows -> skipped instead of breaking the whole response
- names with commas and quotes -> written with CSV escaping and read back correctly
- append logic -> now avoids corrupting header-only files that do not end with a newline

## Testing

### Backend Tests

```bash
cd backend
mvn test
```

Coverage includes:

- controller endpoint behavior
- random status assignment
- CSV repository behavior
- malformed row handling
- escaping of commas and quotes
- header-only file handling

### Frontend Tests

```bash
cd client/client
npm test
```

Coverage includes:

- modal rendering and validation
- status badge styling
- table rendering
- app-level refresh after successful create

## Postman

A Postman collection is included here:

```text
backend/postman/postman_collection.json
```

## AI Usage Summary

AI tooling was used during review to:

- inspect the repository against the specification
- identify gaps and edge cases
- add focused tests
- improve CSV safety
- improve frontend error handling
- rewrite the README and module documentation

The architecture was kept intact. Changes were limited to small, practical fixes.
