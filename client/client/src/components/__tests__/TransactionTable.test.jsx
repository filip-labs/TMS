import React from 'react';
import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import TransactionTable from '../TransactionTable.jsx';

const sample = [
    {
        transactionDate: '2025-03-01',
        accountNumber: '7289-3445-1121',
        accountHolderName: 'Maria Johnson',
        amount: 150.00,
        status: 'Settled',
    },
    {
        transactionDate: '2025-03-02',
        accountNumber: '1122-3456-7890',
        accountHolderName: 'John Smith',
        amount: 75.50,
        status: 'Pending',
    },
];

describe('TransactionTable', () => {
    it('renders all expected column headers', () => {
        render(<TransactionTable transactions={sample} />);
        expect(screen.getByText('Transaction Date')).toBeInTheDocument();
        expect(screen.getByText('Account Number')).toBeInTheDocument();
        expect(screen.getByText('Account Holder Name')).toBeInTheDocument();
        expect(screen.getByText('Amount')).toBeInTheDocument();
        expect(screen.getByText('Status')).toBeInTheDocument();
    });

    it('renders one row per transaction', () => {
        render(<TransactionTable transactions={sample} />);
        expect(screen.getByText('Maria Johnson')).toBeInTheDocument();
        expect(screen.getByText('John Smith')).toBeInTheDocument();
        expect(screen.getByText('7289-3445-1121')).toBeInTheDocument();
        expect(screen.getByText('1122-3456-7890')).toBeInTheDocument();
        expect(screen.getByText('$150.00')).toBeInTheDocument();
        expect(screen.getByText('$75.50')).toBeInTheDocument();
    });

    it('renders status badges for each row', () => {
        render(<TransactionTable transactions={sample} />);
        expect(screen.getByText('Settled')).toHaveClass('status-settled');
        expect(screen.getByText('Pending')).toHaveClass('status-pending');
    });

    it('shows an empty state when there are no transactions', () => {
        render(<TransactionTable transactions={[]} />);
        expect(screen.getByText(/no transactions/i)).toBeInTheDocument();
    });

    it('renders HTML-like values as plain text', () => {
        render(
            <TransactionTable
                transactions={[
                    {
                        transactionDate: '2025-03-03',
                        accountNumber: 'ACCT-123',
                        accountHolderName: '<script>alert(1)</script>',
                        amount: 10,
                        status: 'Pending',
                    },
                ]}
            />
        );

        expect(screen.getByText('<script>alert(1)</script>')).toBeInTheDocument();
        expect(document.querySelector('script')).not.toBeInTheDocument();
    });
});