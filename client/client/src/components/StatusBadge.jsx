import React from 'react';

const STATUS_CLASS_NAMES = {
    pending: 'status-pending',
    settled: 'status-settled',
    failed: 'status-failed',
};

export default function StatusBadge({ status }) {
    const normalizedStatus = String(status ?? '').trim().toLowerCase();
    const modifierClassName = STATUS_CLASS_NAMES[normalizedStatus];
    const className = ['status-badge', modifierClassName].filter(Boolean).join(' ');

    return <span className={className}>{status}</span>;
}
