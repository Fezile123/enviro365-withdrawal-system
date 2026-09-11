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
    <div className="auth-screen">
      <aside className="auth-panel">
        <div className="auth-panel__content">
          <p className="auth-panel__wordmark">Enviro365 Investments</p>
          <h1 className="auth-panel__headline">
            Every withdrawal,<br />handled with care.
          </h1>
          <p className="auth-panel__copy">
            View your portfolio, request withdrawals, and download statements —
            all in one place, backed by the same rules our advisors apply
            by hand.
          </p>

          <ul className="auth-panel__list">
            <li>Real-time portfolio balances</li>
            <li>Automatic eligibility checks</li>
            <li>Downloadable statements</li>
          </ul>
        </div>

        <svg className="auth-panel__motif" viewBox="0 0 400 400" aria-hidden="true">
          <circle cx="330" cy="70" r="120" fill="none" stroke="currentColor" strokeOpacity="0.18" strokeWidth="1.5" />
          <circle cx="330" cy="70" r="80" fill="none" stroke="currentColor" strokeOpacity="0.25" strokeWidth="1.5" />
          <circle cx="60" cy="360" r="90" fill="none" stroke="currentColor" strokeOpacity="0.15" strokeWidth="1.5" />
        </svg>
      </aside>

      <main className="auth-form-side">
        <div className="auth-card">
          <div className="auth-switch">
            <button
              type="button"
              className={`auth-switch__option ${mode === 'login' ? 'auth-switch__option--active' : ''}`}
              onClick={() => { setMode('login'); setError(null); }}
            >
              Log in
            </button>
            <button
              type="button"
              className={`auth-switch__option ${mode === 'register' ? 'auth-switch__option--active' : ''}`}
              onClick={() => { setMode('register'); setError(null); }}
            >
              Register
            </button>
          </div>

          {mode === 'login' ? (
            <>
              <h2 className="auth-card__title">Welcome back</h2>
              <p className="auth-card__subtitle">Log in with your registered email.</p>

              <form onSubmit={handleLogin}>
                <div className="field">
                  <label htmlFor="loginEmail">Email</label>
                  <input
                    id="loginEmail"
                    type="email"
                    required
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="you@example.com"
                  />
                </div>
                <button type="submit" className="button button--primary auth-submit" disabled={submitting}>
                  {submitting ? 'Logging in…' : 'Log in'}
                </button>
              </form>
            </>
          ) : (
            <>
              <h2 className="auth-card__title">Create your account</h2>
              <p className="auth-card__subtitle">A new portfolio is set up for you automatically.</p>

              <form onSubmit={handleRegister}>
                <div className="field-row">
                  <div className="field">
                    <label htmlFor="regFirstName">First name</label>
                    <input id="regFirstName" required value={firstName} onChange={(e) => setFirstName(e.target.value)} />
                  </div>
                  <div className="field">
                    <label htmlFor="regLastName">Last name</label>
                    <input id="regLastName" required value={lastName} onChange={(e) => setLastName(e.target.value)} />
                  </div>
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
                    placeholder="you@example.com"
                  />
                </div>
                <button type="submit" className="button button--primary auth-submit" disabled={submitting}>
                  {submitting ? 'Registering…' : 'Register'}
                </button>
              </form>
            </>
          )}

          {error && <div className="feedback feedback--error auth-error">{error}</div>}
        </div>
      </main>
    </div>
  );
}
