export function formatCurrency(amount) {
  const value = Number(amount);
  return 'R ' + value.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

export function formatDate(isoString) {
  const date = new Date(isoString);
  return date.toLocaleString('en-ZA', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
}

export function statusPillClass(status) {
  return (
    {
      APPROVED: 'status-pill--approved',
      PENDING: 'status-pill--pending',
      REJECTED: 'status-pill--rejected'
    }[status] || ''
  );
}
