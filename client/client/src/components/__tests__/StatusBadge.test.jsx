import React from 'react';
import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import StatusBadge from '../StatusBadge.jsx';

describe('StatusBadge', () => {
    it('renders Pending with the pending class', () => {
        render(<StatusBadge status="Pending" />);
        const badge = screen.getByText('Pending');
        expect(badge).toHaveClass('status-badge');
        expect(badge).toHaveClass('status-pending');
    });

    it('renders Settled with the settled class', () => {
        render(<StatusBadge status="Settled" />);
        const badge = screen.getByText('Settled');
        expect(badge).toHaveClass('status-settled');
    });

    it('renders Failed with the failed class', () => {
        render(<StatusBadge status="Failed" />);
        const badge = screen.getByText('Failed');
        expect(badge).toHaveClass('status-failed');
    });

    it('falls back gracefully for unknown status', () => {
        render(<StatusBadge status="Unknown" />);
        const badge = screen.getByText('Unknown');
        expect(badge).toHaveClass('status-badge');
        expect(badge).not.toHaveClass('status-pending');
        expect(badge).not.toHaveClass('status-settled');
        expect(badge).not.toHaveClass('status-failed');
    });
});