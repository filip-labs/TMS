# Frontend Documentation

The actual React + Vite application for this project lives in:

```text
client/client/
```

This file is the single frontend README for the repository.

## What It Does

- fetches transactions from the backend
- shows them in a table
- opens a modal to add a transaction
- submits the form to `POST /transactions`
- refreshes the table after a successful create
- shows readable error messages when requests fail

## Requirements

- Node.js 18+ or 20+
- npm 9+

## Install

From the repository root:

```bash
cd client/client
npm install
```

## Run

From the repository root:

```bash
cd client/client
npm run dev
```

Default frontend URL:

```text
http://localhost:5173
```

## API Configuration

The frontend reads the backend base URL from `VITE_API_BASE_URL`.

Default fallback:

```text
http://localhost:8080
```

Example `.env`:

```env
VITE_API_BASE_URL=http://localhost:8080
```

You can copy the sample file:

```bash
cd client/client
cp .env.example .env
```

## Tests

```bash
cd client/client
npm test
```

## Important Files

- `client/client/src/App.jsx` - main page flow and refresh logic
- `client/client/src/api/transactionsApi.js` - backend API calls
- `client/client/src/components/TransactionTable.jsx` - table rendering
- `client/client/src/components/AddTransactionModal.jsx` - modal form and error handling
- `client/client/src/components/StatusBadge.jsx` - status colors
