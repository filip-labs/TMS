import React from 'react';
import {describe, expect, it, vi} from 'vitest';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import AddTransactionModal from '../AddTransactionModal.jsx';

describe('AddTransactionModal', () => {
    it('does not render when isOpen is false', () => {
        render(
            <AddTransactionModal isOpen={false} onClose={() => {}} onSubmit={() => {}} />
        );
        expect(screen.queryByText('Add Transaction')).not.toBeInTheDocument();
    });

    it('renders form fields when open', () => {
        render(
            <AddTransactionModal isOpen={true} onClose={() => {}} onSubmit={() => {}} />
        );
        expect(screen.getByText('Add Transaction')).toBeInTheDocument();
        expect(screen.getByText('Transaction Date')).toBeInTheDocument();
        expect(screen.getByText('Account Number')).toBeInTheDocument();
        expect(screen.getByText('Account Holder Name')).toBeInTheDocument();
        expect(screen.getByText('Amount')).toBeInTheDocument();
    });

    it('calls onClose when Cancel is clicked', async () => {
        const onClose = vi.fn();
        render(
            <AddTransactionModal isOpen={true} onClose={onClose} onSubmit={() => {}} />
        );
        await userEvent.click(screen.getByText('Cancel'));
        expect(onClose).toHaveBeenCalledTimes(1);
    });

    it('calls onClose when Escape key is pressed', async () => {
        const onClose = vi.fn();
        render(
            <AddTransactionModal isOpen={true} onClose={onClose} onSubmit={() => {}} />
        );
        await userEvent.keyboard('{Escape}');
        expect(onClose).toHaveBeenCalled();
    });

    it('calls onSubmit with form values and closes on success', async value => {
        const onSubmit = vi.fn().mockResolvedValue(value);
        const onClose = vi.fn();

        render(
            <AddTransactionModal isOpen={true} onClose={onClose} onSubmit={onSubmit} />
        );

        await userEvent.type(
            screen.getByLabelText(/Transaction Date/i),
            '2025-05-12'
        );
        await userEvent.type(
            screen.getByLabelText(/Account Number/i),
            ' 1234-5678-9012 '
        );
        await userEvent.type(
            screen.getByLabelText(/Account Holder Name/i),
            ' Jane Doe '
        );
        await userEvent.type(screen.getByLabelText(/Amount/i), '100.50');

        await userEvent.click(screen.getByRole('button', { name: /create transaction/i }));

        expect(onSubmit).toHaveBeenCalledWith({
            transactionDate: '2025-05-12',
            accountNumber: '1234-5678-9012',
            accountHolderName: 'Jane Doe',
            amount: 100.5,
        });
        expect(onClose).toHaveBeenCalled();
    });

    it('does not call onSubmit when fields are invalid', async () => {
        const onSubmit = vi.fn();
        render(
            <AddTransactionModal isOpen={true} onClose={() => {}} onSubmit={onSubmit} />
        );
        await userEvent.click(screen.getByRole('button', { name: /create transaction/i }));
        expect(onSubmit).not.toHaveBeenCalled();
    });

    it('shows backend field errors when submit fails with validation details', async () => {
        const error = new Error('Validation failed');
        error.details = {
            accountHolderName: 'Account holder name is invalid.',
        };

        const onSubmit = vi.fn().mockRejectedValue(error);

        render(
            <AddTransactionModal isOpen={true} onClose={() => {
            }} onSubmit={onSubmit}/>
        );

        await userEvent.type(screen.getByLabelText(/Transaction Date/i), '2025-05-12');
        await userEvent.type(screen.getByLabelText(/Account Number/i), '1234-5678-9012');
        await userEvent.type(screen.getByLabelText(/Account Holder Name/i), 'Jane Doe');
        await userEvent.type(screen.getByLabelText(/Amount/i), '100');

        await userEvent.click(screen.getByRole('button', {name: /create transaction/i}));

        expect(await screen.findByText('Validation failed')).toBeInTheDocument();
        expect(await screen.findByText('Account holder name is invalid.')).toBeInTheDocument();
    });
});
