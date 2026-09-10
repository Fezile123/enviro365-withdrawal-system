/**
 * App state + wiring. Kept intentionally simple (no framework, no build
 * step) per the assessment's HTML/JS frontend requirement.
 */
const state = {
  investors: [],
  investorId: null,
  portfolio: null
};

const el = {
  investorSelect: document.getElementById('investorSelect'),
  investorName: document.getElementById('investorName'),
  totalBalance: document.getElementById('totalBalance'),
  productGrid: document.getElementById('productGrid'),
  productSelect: document.getElementById('productSelect'),
  amountInput: document.getElementById('amountInput'),
  productError: document.getElementById('productError'),
  amountError: document.getElementById('amountError'),
  form: document.getElementById('withdrawalForm'),
  formFeedback: document.getElementById('formFeedback'),
  historyBody: document.getElementById('historyBody'),
  exportStatus: document.getElementById('exportStatus'),
  exportButton: document.getElementById('exportButton')
};

function formatCurrency(amount) {
  const value = Number(amount);
  return 'R ' + value.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function formatDate(isoString) {
  const date = new Date(isoString);
  return date.toLocaleString('en-ZA', {
    year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
  });
}

function statusPillClass(status) {
  return {
    APPROVED: 'status-pill--approved',
    PENDING: 'status-pill--pending',
    REJECTED: 'status-pill--rejected'
  }[status] || '';
}

/* ---------- Rendering ---------- */

function renderInvestorOptions() {
  el.investorSelect.innerHTML = state.investors
    .map(inv => `<option value="${inv.id}">${inv.firstName} ${inv.lastName}</option>`)
    .join('');
  el.investorSelect.value = state.investorId;
}

function renderPortfolio(portfolio) {
  const investor = state.investors.find(inv => inv.id === state.investorId);
  el.investorName.textContent = investor ? `${investor.firstName} ${investor.lastName}` : '';

  const total = portfolio.products.reduce((sum, p) => sum + Number(p.balance), 0);
  el.totalBalance.textContent = formatCurrency(total);

  el.productGrid.innerHTML = portfolio.products.map(p => `
    <div class="product-card">
      <p class="product-card__type">${p.type}</p>
      <p class="product-card__name">${p.name}</p>
      <p class="product-card__balance">${formatCurrency(p.balance)}</p>
    </div>
  `).join('');

  el.productSelect.innerHTML = portfolio.products
    .map(p => `<option value="${p.id}">${p.name} — ${formatCurrency(p.balance)} available</option>`)
    .join('');
}

function renderHistory(notices) {
  if (!notices.length) {
    el.historyBody.innerHTML = `<tr class="empty-row"><td colspan="6">No withdrawals yet.</td></tr>`;
    return;
  }

  // Most recent first
  const sorted = [...notices].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

  el.historyBody.innerHTML = sorted.map(n => `
    <tr>
      <td>#${n.id}</td>
      <td>${n.product.name}</td>
      <td>${formatCurrency(n.amount)}</td>
      <td>${formatCurrency(n.balanceAfterWithdrawal)}</td>
      <td><span class="status-pill ${statusPillClass(n.status)}">${n.status}</span></td>
      <td>${formatDate(n.createdAt)}</td>
    </tr>
  `).join('');
}

function showFeedback(message, isError) {
  el.formFeedback.textContent = message;
  el.formFeedback.hidden = false;
  el.formFeedback.className = 'feedback ' + (isError ? 'feedback--error' : 'feedback--success');
}

function clearFieldErrors() {
  [el.productError, el.amountError].forEach(node => { node.hidden = true; node.textContent = ''; });
  el.productSelect.closest('.field').classList.remove('field--invalid');
  el.amountInput.closest('.field').classList.remove('field--invalid');
}

function setFieldError(node, inputWrapper, message) {
  node.textContent = message;
  node.hidden = false;
  inputWrapper.closest('.field').classList.add('field--invalid');
}

/* ---------- UI validation (advanced feature) ----------
 * Client-side checks that mirror the server's input validation, so the
 * investor gets instant feedback without waiting on a round trip. The
 * server remains the source of truth -- these checks never replace the
 * backend's @Valid annotations, they just improve the experience.
 */
function validateForm() {
  clearFieldErrors();
  let valid = true;

  if (!el.productSelect.value) {
    setFieldError(el.productError, el.productSelect, 'Please select a product.');
    valid = false;
  }

  const amount = Number(el.amountInput.value);
  if (!el.amountInput.value || Number.isNaN(amount)) {
    setFieldError(el.amountError, el.amountInput, 'Please enter an amount.');
    valid = false;
  } else if (amount <= 0) {
    setFieldError(el.amountError, el.amountInput, 'Amount must be greater than 0.');
    valid = false;
  }

  return valid;
}

/* ---------- Data loading ---------- */

async function loadInvestorData(investorId) {
  state.investorId = investorId;
  const [portfolio, history] = await Promise.all([
    api.getPortfolio(investorId),
    api.getWithdrawalHistory(investorId)
  ]);
  state.portfolio = portfolio;
  renderPortfolio(portfolio);
  renderHistory(history);
}

async function init() {
  try {
    state.investors = await api.getInvestors();
    if (!state.investors.length) {
      el.investorName.textContent = 'No investors found.';
      return;
    }
    state.investorId = state.investors[0].id;
    renderInvestorOptions();
    await loadInvestorData(state.investorId);
  } catch (err) {
    showFeedback(`Could not load data from the server: ${err.message}. Is the backend running on http://localhost:8080?`, true);
  }
}

/* ---------- Event wiring ---------- */

el.investorSelect.addEventListener('change', async (e) => {
  el.formFeedback.hidden = true;
  await loadInvestorData(Number(e.target.value));
});

el.form.addEventListener('submit', async (e) => {
  e.preventDefault();
  el.formFeedback.hidden = true;

  if (!validateForm()) {
    return;
  }

  const productId = Number(el.productSelect.value);
  const amount = Number(el.amountInput.value);

  try {
    await api.createWithdrawal(state.investorId, productId, amount);
    showFeedback('Withdrawal submitted successfully.', false);
    el.form.reset();
    await loadInvestorData(state.investorId);
  } catch (err) {
    if (err.fieldErrors) {
      if (err.fieldErrors.productId) {
        setFieldError(el.productError, el.productSelect, err.fieldErrors.productId);
      }
      if (err.fieldErrors.amount) {
        setFieldError(el.amountError, el.amountInput, err.fieldErrors.amount);
      }
    } else {
      showFeedback(err.message, true);
    }
  }
});

el.exportButton.addEventListener('click', () => {
  const url = api.buildExportUrl(state.investorId, el.exportStatus.value);
  window.location.href = url;
});

init();
