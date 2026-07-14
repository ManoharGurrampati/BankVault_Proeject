# BankVault

A console-based Bank Management System built in Java, using JDBC and MySQL. Simulates core banking operations — account creation, deposits, withdrawals, fund transfers, and transaction history — with a layered architecture and atomic transaction handling.

## Features

- Open a new account (Savings/Current) with PIN-based authentication
- Deposit and withdraw funds, with minimum balance enforcement
- Transfer funds between accounts atomically (all-or-nothing, with rollback on failure)
- Check balance and view full transaction history
- Close an account, with automatic settlement of any remaining balance
- PINs are never stored in plain text — hashed using SHA-256

## Tech Stack

- **Language:** Java 21
- **Build Tool:** Maven
- **Database:** MySQL 8
- **Database Access:** JDBC (MySQL Connector/J 8.4.0)
- **IDE:** Eclipse

## Architecture

The project follows a layered architecture, keeping console I/O, business logic, and database access fully separate:

```
Main.java (console menu, user input)
      |
      v
BankService (business rules: minimum balance, PIN checks, atomic transfers)
      |
      v
AccountDAO / TransactionDAO (raw SQL, one DAO per table)
      |
      v
DBConnection (reads db.properties, opens JDBC connections)
      |
      v
MySQL (accounts, transactions tables)
```

Each layer only communicates with the layer directly below it. The DAO layer contains no business logic — it only executes SQL. All business rules (minimum balance, PIN verification, account status checks) live exclusively in `BankService`.

## Database Schema

**accounts**

| Column | Type | Notes |
|---|---|---|
| account_number | BIGINT, PK, AUTO_INCREMENT | |
| account_holder_name | VARCHAR(100) | NOT NULL |
| email | VARCHAR(100) | UNIQUE |
| phone | VARCHAR(15) | NOT NULL |
| account_type | ENUM('SAVINGS','CURRENT') | NOT NULL |
| balance | DECIMAL(15,2) | NOT NULL, DEFAULT 0.00 |
| pin_hash | VARCHAR(255) | NOT NULL, SHA-256 hash |
| account_status | ENUM('ACTIVE','CLOSED','FROZEN') | NOT NULL, DEFAULT 'ACTIVE' |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

**transactions**

| Column | Type | Notes |
|---|---|---|
| transaction_id | BIGINT, PK, AUTO_INCREMENT | |
| account_number | BIGINT, FK → accounts | NOT NULL |
| transaction_type | ENUM('DEPOSIT','WITHDRAW','TRANSFER_IN','TRANSFER_OUT') | NOT NULL |
| amount | DECIMAL(15,2) | NOT NULL |
| balance_after | DECIMAL(15,2) | NOT NULL |
| related_account | BIGINT | nullable, populated for transfers |
| time_stamp | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

## Key Design Decisions

- **BigDecimal for all monetary values** — avoids floating-point rounding errors inherent to `double`/`float`.
- **Atomic fund transfers** — `transfer()` opens a single JDBC connection, disables auto-commit, performs both the debit and credit plus both transaction log inserts, then commits only if every step succeeds. Any failure triggers a full rollback, preventing partial transfers.
- **Scoped database user** — the application connects using a dedicated MySQL user (`bank_app`) with only `SELECT`, `INSERT`, `UPDATE`, `DELETE` privileges on the application database — following the principle of least privilege, not root access.
- **PIN hashing** — PINs are hashed with SHA-256 before storage; the raw PIN is never persisted or logged.
- **Closure logic** — closing an account automatically settles any remaining balance as a final withdrawal transaction, rather than requiring the user to manually withdraw down to zero (which would otherwise conflict with the minimum balance rule).

## Setup

1. Create the MySQL database and tables using the schema above (see `/schema` if included, or the SQL in this README).
2. Create a dedicated MySQL user and grant it `SELECT, INSERT, UPDATE, DELETE` on the database.
3. Copy `db.properties.example` to `db.properties` in `src/main/resources` and fill in your actual database URL, username, and password.
4. Build with Maven: `mvn clean package`
5. Run: `java -cp target/bank-management-system.jar com.bank.Main`

## Known Limitations / Future Improvements

- PIN hashing uses plain SHA-256 (no salt) for simplicity — production systems should use BCrypt or Argon2 for built-in salting and adjustable cost.
- Each database operation opens a new connection rather than using a connection pool (e.g., HikariCP) — acceptable for a console app, but not ideal at scale.
- No automated tests yet — a planned next step is adding JUnit/Mockito coverage for the service layer.
- Currently console-only — a natural next iteration is exposing these operations as a REST API (Spring Boot) with a web frontend.

## Author

Gurrampati Manohar Reddy
