import { useEffect, useState } from 'react';
import { api } from './api.js';
import LoginPage from './components/LoginPage.jsx';
import Header from './components/Header.jsx';
import PortfolioHero from './components/PortfolioHero.jsx';
import WithdrawalForm from './components/WithdrawalForm.jsx';
import HistoryTable from './components/HistoryTable.jsx';
import './App.css';

export default function App() {
  const [investor, setInvestor] = useState(null); // the "logged in" investor, or null
  const [portfolio, setPortfolio] = useState(null);
  const [history, setHistory] = useState([]);
  const [loadError, setLoadError] = useState(null);

  useEffect(() => {
    if (!investor) return;

    setLoadError(null);
    Promise.all([api.getPortfolio(investor.id), api.getWithdrawalHistory(investor.id)])
      .then(([portfolioData, historyData]) => {
        setPortfolio(portfolioData);
        setHistory(historyData);
      })
      .catch((err) => setLoadError(`Could not load investor data: ${err.message}`));
  }, [investor]);

  async function refreshInvestorData() {
    const [portfolioData, historyData] = await Promise.all([
      api.getPortfolio(investor.id),
      api.getWithdrawalHistory(investor.id)
    ]);
    setPortfolio(portfolioData);
    setHistory(historyData);
  }

  async function handleWithdrawalSubmit(productId, amount) {
    await api.createWithdrawal(investor.id, productId, amount);
    await refreshInvestorData();
  }

  function handleLogout() {
    setInvestor(null);
    setPortfolio(null);
    setHistory([]);
    setLoadError(null);
  }

  if (!investor) {
    return <LoginPage onLoggedIn={setInvestor} />;
  }

  const investorName = `${investor.firstName} ${investor.lastName}`;

  return (
    <>
      <Header investorName={investorName} onLogout={handleLogout} />

      <main className="wrap">
        {loadError && <div className="feedback feedback--error" style={{ marginTop: 24 }}>{loadError}</div>}

        {portfolio && (
          <>
            <PortfolioHero investorName={investorName} products={portfolio.products} />
            {portfolio.products.length > 0 ? (
              <WithdrawalForm products={portfolio.products} onSubmit={handleWithdrawalSubmit} />
            ) : (
              <section className="panel">
                <h2>Request a withdrawal</h2>
                <p style={{ color: 'var(--ink-soft)', margin: 0 }}>
                  No products in this portfolio yet -- nothing to withdraw from.
                </p>
              </section>
            )}
            <HistoryTable notices={history} investorId={investor.id} />
          </>
        )}
      </main>
    </>
  );
}
