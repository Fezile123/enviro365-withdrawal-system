import { useState } from 'react';
import { api } from '../api.js';

/**
 * Simple "who am I" screen -- no password. Two modes: log in with an
 * existing email, or register as a new investor. See AuthService on the
 * backend for why this isn't real authentication.
 */
export default function LoginPage({ onLoggedIn }) {
  const [mode, setMode] = useState('login'); // 'login' | 'register'
  const [email, setEmail] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [dateOfBirth, setDateOfBirth] = useState('');
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  async function handleLogin(e) {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      const investor = await api.login(email);
      onLoggedIn(investor);
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  async function handleRegister(e) {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      const investor = await api.register(firstName, lastName, dateOfBirth, email);
      onLoggedIn(investor);
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="login-screen">
      <div className="login-card">
        <p className="wordmark login-card__wordmark">Enviro365 Investments</p>

        <div className="login-tabs">
          <button
            type="button"
            className={`login-tab ${mode === 'login' ? 'login-tab--active' : ''}`}
            onClick={() => { setMode('login'); setError(null); }}
          >
            Log in
          </button>
          <button
            type="button"
            className={`login-tab ${mode === 'register' ? 'login-tab--active' : ''}`}
            onClick={() => { setMode('register'); setError(null); }}
          >
            Register
          </button>
        </div>

        {mode === 'login' ? (
          <form onSubmit={handleLogin}>
            <div className="field">
              <label htmlFor="loginEmail">Email</label>
              <input
                id="loginEmail"
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="thandiwe.nkosi@example.com"
              />
            </div>
            <button type="submit" className="button button--primary" disabled={submitting}>
              {submitting ? 'Logging in…' : 'Log in'}
            </button>
            <p className="login-hint">
              Try <code>thandiwe.nkosi@example.com</code> (71) or <code>sipho.dlamini@example.com</code> (40) —
              seeded investors on opposite sides of the retirement age rule.
            </p>
          </form>
        ) : (
          <form onSubmit={handleRegister}>
            <div className="field">
              <label htmlFor="regFirstName">First name</label>
              <input id="regFirstName" required value={firstName} onChange={(e) => setFirstName(e.target.value)} />
            </div>
            <div className="field">
              <label htmlFor="regLastName">Last name</label>
              <input id="regLastName" required value={lastName} onChange={(e) => setLastName(e.target.value)} />
            </div>
            <div className="field">
              <label htmlFor="regDob">Date of birth</label>
              <input
                id="regDob"
                type="date"
                required
                value={dateOfBirth}
                onChange={(e) => setDateOfBirth(e.target.value)}
              />
            </div>
            <div className="field">
              <label htmlFor="regEmail">Email</label>
              <input
                id="regEmail"
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>
            <button type="submit" className="button button--primary" disabled={submitting}>
              {submitting ? 'Registering…' : 'Register'}
            </button>
            <p className="login-hint">New investors start with an empty portfolio (no products yet).</p>
          </form>
        )}

        {error && <div className="feedback feedback--error" style={{ marginTop: 16 }}>{error}</div>}
      </div>
    </div>
  );
}
