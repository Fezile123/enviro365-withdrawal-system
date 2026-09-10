import { useEffect, useState } from 'react';
import { api } from './api.js';
import Header from './components/Header.jsx';
import PortfolioHero from './components/PortfolioHero.jsx';
import WithdrawalForm from './components/WithdrawalForm.jsx';
import HistoryTable from './components/HistoryTable.jsx';
import './App.css';

export default function App() {
  const [investors, setInvestors] = useState([]);
  const [investorId, setInvestorId] = useState(null);
  const [portfolio, setPortfolio] = useState(null);
  const [history, setHistory] = useState([]);
  const [loadError, setLoadError] = useState(null);

  // Initial load: fetch investors, default to the first one.
  useEffect(() => {
    api
      .getInvestors()
      .then((data) => {
        setInvestors(data);
        if (data.length > 0) {
          setInvestorId(data[0].id);
        }
      })
      .catch((err) =>
        setLoadError(`Could not load data from the server: ${err.message}. Is the backend running on http://localhost:8080?`)
      );
  }, []);

  // Whenever the selected investor changes, load their portfolio + history.
  useEffect(() => {
    if (investorId == null) return;

    setLoadError(null);
    Promise.all([api.getPortfolio(investorId), api.getWithdrawalHistory(investorId)])
      .then(([portfolioData, historyData]) => {
        setPortfolio(portfolioData);
        setHistory(historyData);
      })
      .catch((err) => setLoadError(`Could not load investor data: ${err.message}`));
  }, [investorId]);

  async function refreshInvestorData() {
    const [portfolioData, historyData] = await Promise.all([
      api.getPortfolio(investorId),
      api.getWithdrawalHistory(investorId)
    ]);
    setPortfolio(portfolioData);
    setHistory(historyData);
  }

  async function handleWithdrawalSubmit(productId, amount) {
    await api.createWithdrawal(investorId, productId, amount);
    await refreshInvestorData();
  }

  const investor = investors.find((inv) => inv.id === investorId);
  const investorName = investor ? `${investor.firstName} ${investor.lastName}` : '';

  return (
    <>
      <Header investors={investors} investorId={investorId} onChange={setInvestorId} />

      <main className="wrap">
        {loadError && <div className="feedback feedback--error" style={{ marginTop: 24 }}>{loadError}</div>}

        {portfolio && (
          <>
            <PortfolioHero investorName={investorName} products={portfolio.products} />
            <WithdrawalForm products={portfolio.products} onSubmit={handleWithdrawalSubmit} />
            <HistoryTable notices={history} investorId={investorId} />
          </>
        )}
      </main>
    </>
  );
}
