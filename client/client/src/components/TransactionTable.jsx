import React from 'react';
import StatusBadge from './StatusBadge.jsx';

function formatAmount(amount) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD',
    }).format(Number(amount) || 0);
}

export default function TransactionTable({ transactions }) {
    if (!transactions.length) {
        return <p className="empty-state">No transactions found.</p>;
    }

    return (
        <div className="table-wrapper">
            <table className="transactions-table">
                <thead>
                <tr>
                    <th scope="col">Transaction Date</th>
                    <th scope="col">Account Number</th>
                    <th scope="col">Account Holder Name</th>
                    <th scope="col" className="amount-col">Amount</th>
                    <th scope="col">Status</th>
                </tr>
                </thead>
                <tbody>
                {transactions.map((transaction) => (
                    <tr
                        key={[
                            transaction.transactionDate,
                            transaction.accountNumber,
                            transaction.accountHolderName,
                            transaction.amount,
                            transaction.status,
                        ].join('-')}
                    >
                        <td>{transaction.transactionDate}</td>
                        <td className="mono">{transaction.accountNumber}</td>
                        <td>{transaction.accountHolderName}</td>
                        <td className="amount-col">{formatAmount(transaction.amount)}</td>
                        <td>
                            <StatusBadge status={transaction.status} />
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}
