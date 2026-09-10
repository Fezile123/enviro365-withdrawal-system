import { formatCurrency } from '../format.js';

export default function PortfolioHero({ investorName, products }) {
  const total = products.reduce((sum, p) => sum + Number(p.balance), 0);

  return (
    <section className="hero" aria-labelledby="investorName">
      <p className="hero__name" id="investorName">
        {investorName || '\u00A0'}
      </p>
      <p className="hero__label">Total portfolio value</p>
      <p className="hero__figure">{formatCurrency(total)}</p>

      <div className="product-grid">
        {products.map((p) => (
          <div className="product-card" key={p.id}>
            <p className="product-card__type">{p.type}</p>
            <p className="product-card__name">{p.name}</p>
            <p className="product-card__balance">{formatCurrency(p.balance)}</p>
          </div>
        ))}
      </div>
    </section>
  );
}
