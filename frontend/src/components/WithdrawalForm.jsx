import { useState } from 'react';
import { formatCurrency } from '../format.js';

/**
 * Client-side validation mirrors the backend's @Valid checks so the
 * investor gets instant feedback without a round trip. The backend
 * remains the source of truth -- these checks never replace it, they
 * just improve the experience (this satisfies the "UI validation"
 * advanced requirement).
 */
export default function WithdrawalForm({ products, onSubmit }) {
  const [productId, setProductId] = useState(products[0]?.id ?? '');
  const [amount, setAmount] = useState('');
  const [fieldErrors, setFieldErrors] = useState({});
  const [feedback, setFeedback] = useState(null); // { message, isError }
  const [submitting, setSubmitting] = useState(false);

  function validate() {
    const errors = {};
    if (!productId) {
      errors.productId = 'Please select a product.';
    }

    const numericAmount = Number(amount);
    if (!amount || Number.isNaN(numericAmount)) {
      errors.amount = 'Please enter an amount.';
    } else if (numericAmount <= 0) {
      errors.amount = 'Amount must be greater than 0.';
    }

    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setFeedback(null);

    if (!validate()) {
      return;
    }

    setSubmitting(true);
    try {
      await onSubmit(Number(productId), Number(amount));
      setFeedback({ message: 'Withdrawal submitted successfully.', isError: false });
      setAmount('');
    } catch (err) {
      if (err.fieldErrors) {
        setFieldErrors(err.fieldErrors);
      } else {
        setFeedback({ message: err.message, isError: true });
      }
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="panel" aria-labelledby="withdrawTitle">
      <h2 id="withdrawTitle">Request a withdrawal</h2>

      <form onSubmit={handleSubmit} noValidate>
        <div className={`field ${fieldErrors.productId ? 'field--invalid' : ''}`}>
          <label htmlFor="productSelect">Product</label>
          <select id="productSelect" value={productId} onChange={(e) => setProductId(e.target.value)}>
            {products.map((p) => (
              <option key={p.id} value={p.id}>
                {p.name} — {formatCurrency(p.balance)} available
              </option>
            ))}
          </select>
          {fieldErrors.productId && <p className="field__error">{fieldErrors.productId}</p>}
        </div>

        <div className={`field ${fieldErrors.amount ? 'field--invalid' : ''}`}>
          <label htmlFor="amountInput">Amount (R)</label>
          <input
            type="number"
            id="amountInput"
            min="0.01"
            step="0.01"
            placeholder="0.00"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
          />
          {fieldErrors.amount && <p className="field__error">{fieldErrors.amount}</p>}
        </div>

        <button type="submit" className="button button--primary" disabled={submitting}>
          {submitting ? 'Submitting…' : 'Submit withdrawal'}
        </button>
      </form>

      {feedback && (
        <div className={`feedback ${feedback.isError ? 'feedback--error' : 'feedback--success'}`}>
          {feedback.message}
        </div>
      )}
    </section>
  );
}
