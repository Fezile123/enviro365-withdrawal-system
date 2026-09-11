export default function Header({ investorName, onLogout }) {
  return (
    <header className="site-header">
      <div className="wrap site-header__row">
        <span className="wordmark">Enviro365 Investments</span>
        <div className="investor-picker">
          <span>{investorName}</span>
          <button type="button" className="logout-button" onClick={onLogout}>
            Log out
          </button>
        </div>
      </div>
    </header>
  );
}
