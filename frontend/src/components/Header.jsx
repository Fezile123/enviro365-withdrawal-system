export default function Header({ investors, investorId, onChange }) {
  return (
    <header className="site-header">
      <div className="wrap site-header__row">
        <span className="wordmark">Enviro365 Investments</span>
        <label className="investor-picker">
          Viewing
          <select value={investorId ?? ''} onChange={(e) => onChange(Number(e.target.value))}>
            {investors.map((inv) => (
              <option key={inv.id} value={inv.id}>
                {inv.firstName} {inv.lastName}
              </option>
            ))}
          </select>
        </label>
      </div>
    </header>
  );
}
