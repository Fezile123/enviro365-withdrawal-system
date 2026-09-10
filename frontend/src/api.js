const API_BASE = 'http://localhost:8080/api';

async function handleResponse(response) {
  if (response.ok) {
    const text = await response.text();
    return text ? JSON.parse(text) : null;
  }

  let errorBody;
  try {
    errorBody = await response.json();
  } catch {
    throw new Error(`Request failed with status ${response.status}`);
  }

  if (errorBody.fieldErrors) {
    const err = new Error('Validation failed');
    err.fieldErrors = errorBody.fieldErrors;
    throw err;
  }

  throw new Error(errorBody.message || `Request failed with status ${response.status}`);
}

export const api = {
  getInvestors() {
    return fetch(`${API_BASE}/investors`).then(handleResponse);
  },

  getPortfolio(investorId) {
    return fetch(`${API_BASE}/investors/${investorId}/portfolio`).then(handleResponse);
  },

  getWithdrawalHistory(investorId) {
    return fetch(`${API_BASE}/investors/${investorId}/withdrawals`).then(handleResponse);
  },

  createWithdrawal(investorId, productId, amount) {
    return fetch(`${API_BASE}/investors/${investorId}/withdrawals`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ productId, amount })
    }).then(handleResponse);
  },

  buildExportUrl(investorId, status) {
    const url = new URL(`${API_BASE}/investors/${investorId}/withdrawals/export`);
    if (status) {
      url.searchParams.set('status', status);
    }
    return url.toString();
  }
};
