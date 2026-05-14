import React, { useEffect, useMemo, useState } from 'react';

const INITIAL_FORM_STATE = {
    transactionDate: '',
    accountNumber: '',
    accountHolderName: '',
    amount: '',
};

function validateForm(values) {
    const errors = {};

    if (!values.transactionDate) {
        errors.transactionDate = 'Transaction date is required.';
    }

    if (!values.accountNumber.trim()) {
        errors.accountNumber = 'Account number is required.';
    }

    if (!values.accountHolderName.trim()) {
        errors.accountHolderName = 'Account holder name is required.';
    }

    if (!values.amount.trim()) {
        errors.amount = 'Amount is required.';
    } else {
        const numericAmount = Number(values.amount);

        if (Number.isNaN(numericAmount) || numericAmount <= 0) {
            errors.amount = 'Amount must be greater than zero.';
        }
    }

    return errors;
}

export default function AddTransactionModal({ isOpen, onClose, onSubmit }) {
    const [values, setValues] = useState(INITIAL_FORM_STATE);
    const [errors, setErrors] = useState({});
    const [formError, setFormError] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        if (!isOpen) {
            setValues(INITIAL_FORM_STATE);
            setErrors({});
            setFormError('');
            setIsSubmitting(false);
            return undefined;
        }

        function handleKeyDown(event) {
            if (event.key === 'Escape') {
                onClose();
            }
        }

        window.addEventListener('keydown', handleKeyDown);

        return () => {
            window.removeEventListener('keydown', handleKeyDown);
        };
    }, [isOpen, onClose]);

    const submitPayload = useMemo(() => ({
        transactionDate: values.transactionDate,
        accountNumber: values.accountNumber.trim(),
        accountHolderName: values.accountHolderName.trim(),
        amount: Number(values.amount),
    }), [values]);

    if (!isOpen) {
        return null;
    }

    async function handleSubmit(event) {
        event.preventDefault();

        const nextErrors = validateForm(values);
        setErrors(nextErrors);

        if (Object.keys(nextErrors).length > 0) {
            return;
        }

        setFormError('');
        setIsSubmitting(true);

        try {
            await onSubmit(submitPayload);
            onClose();
        } catch (error) {
            setFormError(error.message || 'Failed to create transaction.');
        } finally {
            setIsSubmitting(false);
        }
    }

    function handleChange(event) {
        const { name, value } = event.target;

        setValues((currentValues) => ({
            ...currentValues,
            [name]: value,
        }));

        setErrors((currentErrors) => ({
            ...currentErrors,
            [name]: undefined,
        }));
    }

    return (
        <div className="modal-backdrop" role="presentation">
            <div className="modal" role="dialog" aria-modal="true" aria-labelledby="add-transaction-title">
                <div className="modal-header">
                    <h2 id="add-transaction-title">Add Transaction</h2>
                    <button
                        type="button"
                        className="icon-button"
                        aria-label="Close modal"
                        onClick={onClose}
                    >
                        ×
                    </button>
                </div>

                <form className="modal-form" onSubmit={handleSubmit}>
                    <label className="form-field" htmlFor="transactionDate">
                        <span>Transaction Date</span>
                        <input
                            id="transactionDate"
                            name="transactionDate"
                            type="date"
                            value={values.transactionDate}
                            onChange={handleChange}
                        />
                        {errors.transactionDate && <span className="field-error">{errors.transactionDate}</span>}
                    </label>

                    <label className="form-field" htmlFor="accountNumber">
                        <span>Account Number</span>
                        <input
                            id="accountNumber"
                            name="accountNumber"
                            type="text"
                            value={values.accountNumber}
                            onChange={handleChange}
                        />
                        {errors.accountNumber && <span className="field-error">{errors.accountNumber}</span>}
                    </label>

                    <label className="form-field" htmlFor="accountHolderName">
                        <span>Account Holder Name</span>
                        <input
                            id="accountHolderName"
                            name="accountHolderName"
                            type="text"
                            value={values.accountHolderName}
                            onChange={handleChange}
                        />
                        {errors.accountHolderName && <span className="field-error">{errors.accountHolderName}</span>}
                    </label>

                    <label className="form-field" htmlFor="amount">
                        <span>Amount</span>
                        <input
                            id="amount"
                            name="amount"
                            type="number"
                            min="0"
                            step="0.01"
                            value={values.amount}
                            onChange={handleChange}
                        />
                        {errors.amount && <span className="field-error">{errors.amount}</span>}
                    </label>

                    {formError && <div className="form-error">{formError}</div>}

                    <div className="modal-actions">
                        <button type="button" className="btn btn-secondary" onClick={onClose}>
                            Cancel
                        </button>
                        <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
                            {isSubmitting ? 'Creating...' : 'Create Transaction'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
