# Hotel Booking Hackathon Project

## Tech Stack

- Backend: Java 17, Spring Boot 3, Spring Security 6, JWT cookie auth, JPA, MySQL, Lombok, Swagger
- Frontend: React 19, Vite, React Router v6, Axios interceptors

## Project Structure

- `backend/` Spring Boot REST API
- `frontend/` React app
- `sql/` schema and seed scripts

## Backend Run Steps

1. Create MySQL DB (or run `sql/create_tables.sql`).
2. Set environment variables:
	- `DB_USERNAME`
	- `DB_PASSWORD`
	- `JWT_SECRET` (Base64-encoded secret string, at least 32-byte key)
3. From `backend/` run:

```bash
mvn spring-boot:run
```

4. Swagger UI:
	- `http://localhost:8080/swagger-ui/index.html`

## Frontend Run Steps

1. From `frontend/` install packages:

```bash
npm install
```

2. Start frontend:

```bash
npm run dev
```

3. Open app:
	- `http://localhost:5173`

## Seed Data

1. Execute `sql/seed.sql` after schema.
2. Seed users:
	- ADMIN: `admin@hotel.com` / `password`
	- CUSTOMER: `customer@hotel.com` / `password`

## Notes

- JWT is stored in an HttpOnly cookie (`HOTEL_AUTH`), not localStorage.
- Frontend rehydrates auth state via `/api/auth/me`.
- Soft delete is enabled for hotels and rooms via `isActive`.
