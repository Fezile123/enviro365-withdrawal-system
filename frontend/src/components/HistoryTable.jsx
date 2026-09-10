import { useState } from 'react';
import { formatCurrency, formatDate, statusPillClass } from '../format.js';
import { api } from '../api.js';

export default function HistoryTable({ notices, investorId }) {
  const [status, setStatus] = useState('');

  const sorted = [...notices].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

  function handleExport() {
    window.location.href = api.buildExportUrl(investorId, status);
  }

  return (
    <section className="panel" aria-labelledby="historyTitle">
      <div className="panel__header">
        <h2 id="historyTitle">Withdrawal history</h2>

        <div className="export-controls">
          <label>
            Status
            <select value={status} onChange={(e) => setStatus(e.target.value)}>
              <option value="">All</option>
              <option value="APPROVED">Approved</option>
              <option value="PENDING">Pending</option>
              <option value="REJECTED">Rejected</option>
            </select>
          </label>
          <button type="button" className="button button--secondary" onClick={handleExport}>
            Download CSV
          </button>
        </div>
      </div>

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
          {sorted.length === 0 ? (
            <tr className="empty-row">
              <td colSpan={6}>No withdrawals yet.</td>
            </tr>
          ) : (
            sorted.map((n) => (
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
