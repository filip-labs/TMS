import { useCallback, useEffect, useState } from 'react';
import { fetchTransactions } from './api/transactionsApi.js';
import './App.css';

export default function App() {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

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

          <button type="button" className="btn btn-secondary" onClick={() => void loadTransactions()}>
            Refresh
          </button>
        </header>

        <main className="app-main">
          {loading && <p className="status-message">Loading transactions…</p>}

          {!loading && error && (
              <div className="error-banner" role="alert">
                <strong>Error:</strong> {error}
                <button className="btn btn-link" onClick={() => void loadTransactions()}>
                  Retry
                </button>
              </div>
          )}

          {!loading && !error && transactions.length === 0 && (
              <p className="empty-state">No transactions found.</p>
          )}

          {!loading && !error && transactions.length > 0 && (
              <div className="table-wrapper">
                <table className="transactions-table">
                  <thead>
                  <tr>
                    <th>Transaction Date</th>
                    <th>Account Number</th>
                    <th>Account Holder Name</th>
                    <th className="amount-col">Amount</th>
                    <th>Status</th>
                  </tr>
                  </thead>

                  <tbody>
                  {transactions.map((transaction, index) => (
                      <tr key={`${transaction.accountNumber}-${transaction.transactionDate}-${index}`}>
                        <td>{transaction.transactionDate}</td>
                        <td className="mono">{transaction.accountNumber}</td>
                        <td>{transaction.accountHolderName}</td>
                        <td className="amount-col">
                          {Number(transaction.amount).toFixed(2)}
                        </td>
                        <td>{transaction.status}</td>
                      </tr>
                  ))}
                  </tbody>
                </table>
              </div>
          )}
        </main>
      </div>
  );
}