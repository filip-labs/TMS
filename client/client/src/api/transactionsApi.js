const configuredApiBaseUrl = import.meta.env.VITE_API_BASE_URL;
const API_BASE_URL = (configuredApiBaseUrl || 'http://localhost:8080').replace(/\/$/, '');

async function parseErrorResponse(response, fallbackMessage) {
    const errorBody = await response.json().catch(() => ({}));

    const message = errorBody.message || `${fallbackMessage}: ${response.status}`;
    const error = new Error(message);

    error.details = errorBody.errors || null;

    throw error;
}

export async function fetchTransactions() {
    const response = await fetch(`${API_BASE_URL}/transactions`);

    if (!response.ok) {
        await parseErrorResponse(response, 'Failed to fetch transactions');
    }

    return response.json();
}

export async function createTransaction(payload) {
    const response = await fetch(`${API_BASE_URL}/transactions`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
    });

    if (!response.ok) {
        await parseErrorResponse(response, 'Failed to create transaction');
    }

    return response.json();
}