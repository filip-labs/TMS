import React from 'react';
import {beforeEach, describe, expect, it, vi} from 'vitest';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import App from './App.jsx';
import {createTransaction, fetchTransactions} from './api/transactionsApi.js';

vi.mock('./api/transactionsApi.js', () => ({
    fetchTransactions: vi.fn(),
    createTransaction: vi.fn(),
}));

describe('App', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('reloads the table after a successful transaction create', async () => {
        fetchTransactions
            .mockResolvedValueOnce([
                {
                    transactionDate: '2025-03-10',
                    accountNumber: '4646-8282-1919',
                    accountHolderName: 'Amanda Robinson',
                    amount: 300.5,
                    status: 'Settled',
                },
            ])
            .mockResolvedValueOnce([
                {
                    transactionDate: '2025-05-12',
                    accountNumber: '1111-2222-3333',
                    accountHolderName: 'Smith, Jane "JJ"',
                    amount: 250,
                    status: 'Pending',
                },
                {
                    transactionDate: '2025-03-10',
                    accountNumber: '4646-8282-1919',
                    accountHolderName: 'Amanda Robinson',
                    amount: 300.5,
                    status: 'Settled',
                },
            ]);
        createTransaction.mockResolvedValue({
            transactionDate: '2025-05-12',
            accountNumber: '1111-2222-3333',
            accountHolderName: 'Smith, Jane "JJ"',
            amount: 250,
            status: 'Pending',
        });

        render(<App/>);

        await screen.findByText('Amanda Robinson');

        await userEvent.click(screen.getByRole('button', {name: /\+ add transaction/i}));

        fireEvent.change(screen.getByLabelText(/transaction date/i), {
            target: {value: '2025-05-12'},
        });
        fireEvent.change(screen.getByLabelText(/account number/i), {
            target: {value: '1111-2222-3333'},
        });
        fireEvent.change(screen.getByLabelText(/account holder name/i), {
            target: {value: 'Smith, Jane "JJ"'},
        });
        fireEvent.change(screen.getByLabelText(/amount/i), {
            target: {value: '250.00'},
        });

        await userEvent.click(screen.getByRole('button', {name: /create transaction/i}));

        await waitFor(() => {
            expect(createTransaction).toHaveBeenCalledWith({
                transactionDate: '2025-05-12',
                accountNumber: '1111-2222-3333',
                accountHolderName: 'Smith, Jane "JJ"',
                amount: 250,
            });
        });

        await waitFor(() => {
            expect(fetchTransactions).toHaveBeenCalledTimes(2);
        });

        expect(await screen.findByText('Smith, Jane "JJ"')).toBeInTheDocument();
    });
});
