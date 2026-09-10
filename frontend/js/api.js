/**
 * Thin wrapper around the backend REST API.
 * All functions return parsed JSON and throw an Error with a readable
 * message (pulled from the backend's error body) on non-2xx responses,
 * so app.js can just try/catch and display feedback.
 */
const API_BASE = 'http://localhost:8080/api';

async function handleResponse(response) {
  if (response.ok) {
    // 201/200 with no body edge case (not currently used, but safe)
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

const api = {
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

  /** Builds the CSV export URL; triggering the download is left to app.js. */
  buildExportUrl(investorId, status) {
    const url = new URL(`${API_BASE}/investors/${investorId}/withdrawals/export`);
    if (status) {
      url.searchParams.set('status', status);
    }
    return url.toString();
  }
};
