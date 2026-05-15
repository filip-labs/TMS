# Client Documentation

This module contains the React client for the Transaction Management System. It renders the transaction list, lets users create new transactions through a modal form, and communicates with the Spring Boot backend over HTTP.

## Responsibilities

- Fetch transactions from the backend API
- Display transactions in a table
- Open and close the add-transaction modal
- Submit new transaction payloads
- Show loading, validation, and request error states in the UI

## Technology and Runtime

| Item            | Value                   |
|-----------------|-------------------------|
| Framework       | React 18                |
| Tooling         | Vite                    |
| Package manager | npm                     |
| Default dev URL | `http://localhost:5173` |

## Install Dependencies

```bash
cd client
npm install
```

## Run the Frontend

```bash
cd client
npm run dev
```

Vite starts the development server at `http://localhost:5173`.

## Backend API Connection

The frontend calls the backend from [`src/api/transactionsApi.js`](./src/api/transactionsApi.js).

| Setting                       | Value                   |
|-------------------------------|-------------------------|
| Default API base URL          | `http://localhost:8080` |
| Environment variable override | `VITE_API_BASE_URL`     |
| Config file location          | `client/.env`           |

Example `.env` file:

```env
VITE_API_BASE_URL=http://localhost:8080
```

The backend CORS configuration already allows the default Vite origin `http://localhost:5173`.

## Main UI Features

| Feature                      | Description                                                                               |
|------------------------------|-------------------------------------------------------------------------------------------|
| Transaction table            | Displays transaction date, account number, account holder name, amount, and status        |
| Add Transaction button       | Opens the modal form from the main page header                                            |
| Modal form                   | Collects transaction date, account number, account holder name, and amount                |
| Validation and error display | Shows client-side validation messages, backend field errors, and request failure messages |

### Current UI flow

- `App.jsx` loads transactions on page load.
- The page shows a loading state while data is being fetched.
- If the initial fetch fails, the UI shows an error banner with a retry action.
- `AddTransactionModal.jsx` validates required fields and positive amounts before submitting.
- If backend validation fails, field-level errors are displayed inside the modal.
- On successful creation, the modal closes and the transaction list is reloaded from the backend.

## Important Files

| File                                                                                 | Purpose                                       |
|--------------------------------------------------------------------------------------|-----------------------------------------------|
| [`src/App.jsx`](./src/App.jsx)                                                       | Main page flow and API orchestration          |
| [`src/api/transactionsApi.js`](./src/api/transactionsApi.js)                         | Fetch and create API requests                 |
| [`src/components/TransactionTable.jsx`](./src/components/TransactionTable.jsx)       | Renders the transaction table and empty state |
| [`src/components/AddTransactionModal.jsx`](./src/components/AddTransactionModal.jsx) | Modal form and validation handling            |
| [`src/components/StatusBadge.jsx`](./src/components/StatusBadge.jsx)                 | Status badge styling logic                    |

## Run Frontend Tests

```bash
cd client
npm test
```

Watch mode:

```bash
cd client
npm run test:watch
```

Existing tests cover the main UI components in [`src/components/__tests__/`](./src/components/__tests__).

## Related Documentation

- [Project overview](../README.md)
- [Backend documentation](../backend/README.md)