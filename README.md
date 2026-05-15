# Transaction Management System

Transaction Management System is a small full-stack application for viewing and creating financial transactions. The backend exposes a REST API, the client provides a simple UI for listing and submitting transactions, and data is persisted to a CSV file instead of a database.

## Architecture Overview

| Layer | Technology | Responsibility |
| --- | --- | --- |
| Backend | Spring Boot, Java 21 | Exposes transaction endpoints, validates input, reads and writes transaction data |
| Client | React, Vite | Displays transactions, opens the add-transaction modal, submits new transactions |
| Storage | CSV file | Persists transaction records on disk in a simple tabular format |
| Communication | REST API over HTTP | Connects the React client to the Spring Boot backend |

## Project Structure

```text
FirstCircle/
├── backend/
│   ├── data/
│   ├── postman/
│   ├── src/
│   ├── pom.xml
│   └── README.md
├── client/
│   ├── src/
│   ├── package.json
│   └── README.md
└── README.md
```

## Prerequisites

Install the following before running the project:

| Tool | Version / Notes |
| --- | --- |
| Java | 21 |
| Maven | 3.9+ recommended |
| Node.js | 18.18+ or 20+ |
| npm | 9+ recommended |
| Git | Required to clone the repository |
| Postman | Optional, for importing the collection in [`backend/postman/`](./backend/postman/) |

## Quick Start

1. Clone the repository.
2. Start the backend from `backend/`.
3. Start the frontend from `client/`.
4. Open the app in your browser at `http://localhost:5173`.

Typical local workflow:

```bash
git clone <repository-url>
cd FirstCircle

cd backend
mvn spring-boot:run

cd ../client
npm install
npm run dev
```

## Documentation

<details>
  <summary>Backend</summary>

Detailed backend setup, CSV storage behavior, API documentation, validation rules, testing, and Postman notes are in [backend/README.md](./backend/README.md).

</details>

<details>
  <summary>Frontend / Client</summary>

Detailed frontend setup, API integration, UI structure, and test instructions are in [client/README.md](./client/README.md).

</details>

<details>
  <summary>Postman</summary>

Postman collection files are stored in [backend/postman/](./backend/postman/). The backend README explains how they map to the available API endpoints.

</details>