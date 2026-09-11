import { useMemo, useState } from 'react';
import { formatCurrency, formatDate, statusPillClass } from '../format.js';
import { api } from '../api.js';

/**
 * Filters (status + date range) now apply to what's actually shown on
 * screen, not just the CSV export -- so "what you see is what you
 * download". Filtering happens client-side since the full history is
 * already loaded; the CSV export sends the same filters to the backend
 * (which supports them natively) so a very large history would still
 * export correctly even if the UI only ever fetches what's already there.
 */
export default function HistoryTable({ notices, investorId }) {
  const [status, setStatus] = useState('');
  const [from, setFrom] = useState('');
  const [to, setTo] = useState('');

  const filtered = useMemo(() => {
    return notices
      .filter((n) => !status || n.status === status)
      .filter((n) => !from || new Date(n.createdAt) >= new Date(from))
      .filter((n) => !to || new Date(n.createdAt) <= new Date(`${to}T23:59:59`))
      .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
  }, [notices, status, from, to]);

  function handleExport() {
    window.location.href = api.buildExportUrl(investorId, status, from, to);
  }

  function clearFilters() {
    setStatus('');
    setFrom('');
    setTo('');
  }

  const hasActiveFilters = status || from || to;

  return (
    <section className="panel" aria-labelledby="historyTitle">
      <div className="panel__header">
        <h2 id="historyTitle">Withdrawal history</h2>
        <button type="button" className="button button--secondary" onClick={handleExport}>
          Download CSV
        </button>
      </div>

      <div className="history-filters">
        <label>
          Status
          <select value={status} onChange={(e) => setStatus(e.target.value)}>
            <option value="">All</option>
            <option value="APPROVED">Approved</option>
            <option value="PENDING">Pending</option>
            <option value="REJECTED">Rejected</option>
          </select>
        </label>
        <label>
          From
          <input type="date" value={from} onChange={(e) => setFrom(e.target.value)} />
        </label>
        <label>
          To
          <input type="date" value={to} onChange={(e) => setTo(e.target.value)} />
        </label>
        {hasActiveFilters && (
          <button type="button" className="history-filters__clear" onClick={clearFilters}>
            Clear filters
          </button>
        )}
      </div>

      <p className="history-count">
        {filtered.length} of {notices.length} {notices.length === 1 ? 'withdrawal' : 'withdrawals'}
        {hasActiveFilters ? ' matching filters' : ''}
      </p>

      <table className="history-table">
        <thead>
          <tr>
            <th scope="col">Notice</th>
            <th scope="col">Product</th>
            <th scope="col">Amount</th>
            <th scope="col">Balance after</th>
            <th scope="col">Status</th>
            <th scope="col">Date</th>
          </tr>
        </thead>
        <tbody>
          {filtered.length === 0 ? (
            <tr className="empty-row">
              <td colSpan={6}>
                {notices.length === 0 ? 'No withdrawals yet.' : 'No withdrawals match the current filters.'}
              </td>
            </tr>
          ) : (
            filtered.map((n) => (
              <tr key={n.id}>
                <td>#{n.id}</td>
                <td>{n.product.name}</td>
                <td>{formatCurrency(n.amount)}</td>
                <td>{formatCurrency(n.balanceAfterWithdrawal)}</td>
                <td>
                  <span className={`status-pill ${statusPillClass(n.status)}`}>{n.status}</span>
                </td>
                <td>{formatDate(n.createdAt)}</td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </section>
  );
}
