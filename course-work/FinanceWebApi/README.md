# Finance Manager

A personal finance management system with a Spring Boot REST API backend and a Java console application frontend. Users can manage budgets, track expenses and organise expense categories, with role-based access for regular users and admins.

---

## Prerequisites

- Java 22
- Maven 3.8+
- MySQL 8.0+

---

## Installation

### 1. Clone the repository

```bash
git clone https://github.com/EmilSlvavov/distributed-applications-se.git
```

### 2. Create the database

```sql
CREATE DATABASE finances;
```

### 3. Configure the backend

In `finances/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/finances
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.jpa.hibernate.ddl-auto=update
jwt.secret=your-secret-key-minimum-32-characters-long
jwt.expiration=86400000
```
## Running the Application

### 1. Start the backend

Run `FinancesApplication.java` directly from IntelliJ IDEA.

### 2. Insert the first admin user

```sql
INSERT INTO user (username, password, role, is_active, created_at)
VALUES ('admin', '$2b$10$qKAdiYXKBSgVcezsWqhFje6saTVlHwfPiakyXWlg5B50DeFer9pZu', 'ADMIN', true, NOW());
```

This creates an admin account with the password `Secret123!`. Change it after first login.

### 3. Start the frontend

Run `FinanceConsoleApp.java` directly from IntelliJ IDEA.

---

## Notes

- Swagger UI is available at `http://localhost:8080/swagger-ui.html` once the backend is running.
- New users can self-register through the console application with the USER role.
- The admin panel is only visible to users with the ADMIN role.