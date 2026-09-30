# Military Asset Management System (MAMS)

A secure, role-based enterprise application built for military commanders and logistics personnel to manage the movement, assignment, and expenditure of critical assets (weapons, vehicles, ammunition, communications gear) across multiple military bases.

---

## Key Updates & Requirements Summary

1. **Role-Based Access Control (RBAC)**:
   - Authenticated user role comes strictly from backend JWT / SecurityContext.
   - **`ADMIN`**: Full access to all bases, data, operations, and system audit logs.
   - **`BASE_COMMANDER`**: Restricted to their assigned base (assigned base comes from user database record; cannot be altered from React or URL parameters).
   - **`LOGISTICS_OFFICER`**: Access restricted to Purchases and Transfers APIs (Assignments, Expenditures, and Audit Logs are restricted with HTTP 403 Forbidden).

2. **JWT Authentication & Security**:
   - React frontend authenticates via `POST /api/auth/login` and sends `Authorization: Bearer <JWT>` with every protected API request.
   - Unauthenticated requests return **HTTP 401 Unauthorized**; unauthorized access returns **HTTP 403 Forbidden**.

3. **No Silent Mock Data Replacement**:
   - Real backend API errors (400, 401, 403, 500) are surfaced directly in the React UI as meaningful error messages.

4. **Historical Date-Range Opening Balance Calculation**:
   - Opening Balance for a reporting period $[T_{\text{start}}, T_{\text{end}}]$ equals the stock at $T_{\text{start}}$:
     $$\text{Opening Balance}(T_{\text{start}}) = \text{Initial Stock} + \text{Purchases}(<T_{\text{start}}) + \text{TransfersIn}(<T_{\text{start}}) - \text{TransfersOut}(<T_{\text{start}}) - \text{Expenditures}(<T_{\text{start}})$$
   - Net Movement in period = $\text{Purchases} + \text{Transfers In} - \text{Transfers Out}$
   - Closing Balance = $\text{Opening Balance} + \text{Net Movement} - \text{Expended in period}$

5. **Secrets & Environment Variables**:
   - Environment variables supported: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`.
   - `.env.example` provided with placeholder values.

---

## Standard Demo Accounts for Testing

| Role | Username | Password | Scope / Permissions |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `password123` | Full access across all bases & audit logs. |
| **Base Commander** | `commander_bragg` | `password123` | Assigned to Fort Liberty (Bragg) [Base ID: 1]. Restricted to Bragg assets. |
| **Base Commander** | `commander_pendleton` | `password123` | Assigned to Camp Pendleton [Base ID: 2]. Restricted to Pendleton assets. |
| **Logistics Officer**| `logistics_officer` | `password123` | Logistics access across Purchases and Inter-Base Transfers. |

---

## How to Launch

### 1. Backend (Spring Boot)
```bash
cd backend
mvn spring-boot:run
```

### 2. Frontend (React)
```bash
cd frontend
npm run dev
```
Open **[http://localhost:5173](http://localhost:5173)** to access the live app.
