import React, { useCallback, useEffect, useState } from 'react';
import TransactionTable from './components/TransactionTable.jsx';
import AddTransactionModal from './components/AddTransactionModal.jsx';
import { fetchTransactions, createTransaction } from './api/transactionsApi.js';

export default function App() {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const loadTransactions = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const data = await fetchTransactions();
      setTransactions(data);
    } catch (err) {
      setError(err.message || 'Failed to load transactions');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void loadTransactions();
  }, [loadTransactions]);

  const handleCreate = async (payload) => {
    await createTransaction(payload);
    await loadTransactions();
  };

  return (
      <div className="app">
        <header className="app-header">
          <div>
            <h1>Transaction Management</h1>
            <p className="subtitle">
              {loading
                  ? 'Loading transactions…'
                  : `${transactions.length} ${transactions.length === 1 ? 'transaction' : 'transactions'}`}
            </p>
          </div>
          <button
              type="button"
              className="btn btn-primary"
              onClick={() => setIsModalOpen(true)}
          >
            + Add Transaction
          </button>
        </header>

        <main className="app-main">
          {loading && <p className="status-message">Loading transactions…</p>}
          {error && (
              <div className="error-banner" role="alert">
                <strong>Error:</strong> {error}
                <button className="btn btn-link" onClick={() => void loadTransactions()}>
                  Retry
                </button>
              </div>
          )}
          {!loading && !error && <TransactionTable transactions={transactions} />}
        </main>

        <AddTransactionModal
            isOpen={isModalOpen}
            onClose={() => setIsModalOpen(false)}
            onSubmit={handleCreate}
        />
      </div>
  );
}