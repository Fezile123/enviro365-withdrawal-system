# Enviro365 Investments — Withdrawal Notice System

Full-stack technical assessment (Junior Developer) for eTalente / Enviro365 Investments.

Allows investors to view portfolios, submit withdrawal notices, view withdrawal
history, and export CSV statements — with backend business-rule validation.

## Tech stack

- **Backend:** Java 17, Spring Boot 3.3, Spring Data JPA, H2 (in-memory)
- **Frontend:** React (Vite)
- **Testing:** JUnit 5 / Spring Boot Test

> Note: the frontend was originally built in plain HTML/JS, then rebuilt in
> React partway through development. See the commit/PR history (the
> `revert/html-js-frontend` PR) for that change.

## Repository structure

```
backend/    Spring Boot REST API
frontend/   React (Vite) UI — portfolio dashboard, withdrawal form, history, CSV export
```

## Business rules implemented

1. Retirement withdrawals only allowed if investor age > 65
2. Withdrawal amount must not exceed the product's balance
3. Withdrawal amount must not exceed 90% of the product's balance

## Advanced requirements implemented (3 of 5)

- **Unit tests**
- **Input validation** (`@Valid` request DTOs, backend)
- **UI validation** (client-side checks in the React form, mirroring the backend)

## Setup

### Backend

Requires Java 17+ and Maven (or use an IDE like IntelliJ, which bundles Maven).

```bash
cd backend
mvn spring-boot:run
```

Runs on http://localhost:8080. H2 is in-memory and reseeds sample data
(one investor, a RETIREMENT product, a VOLUNTARY product) on every startup.

### Frontend

Requires Node.js.

```bash
cd frontend
npm install
npm run dev
```

Runs on http://localhost:5173. Make sure the backend is running first.

## API overview

| Method | Endpoint                                            | Description                          |
|--------|------------------------------------------------------|---------------------------------------|
| GET    | `/api/investors`                                     | List investors                        |
| GET    | `/api/investors/{id}`                                 | Investor detail                       |
| GET    | `/api/investors/{id}/portfolio`                       | Portfolio + products                  |
| POST   | `/api/investors/{id}/withdrawals`                     | Create a withdrawal notice            |
| GET    | `/api/investors/{id}/withdrawals`                     | Withdrawal history                    |
| GET    | `/api/investors/{id}/withdrawals/export`              | CSV export (optional `status`, `from`, `to` filters) |

## AI usage disclosure

This solution was built with AI assistance (Claude, via Anthropic's Claude.ai).
AI was used to help scaffold the Spring Boot project structure, implement the
JPA entities/repositories/services/controllers, write the business rule
validation logic, and build the React frontend components. All AI-assisted
code was reviewed and is understood; code comments throughout explain the
reasoning behind key design decisions (e.g. why withdrawals are tied to a
Product rather than the whole Portfolio, why certain advanced features were
scoped narrowly). Ready to walk through any part of it in the follow-up
interview.
