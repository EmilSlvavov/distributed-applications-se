# Finance Console Application

Java console application frontend for the Finance Web API.

## Features

- **Authentication**: Login and registration with JWT token support
- **Budget Management**: Create and view budgets with pagination
- **Expense Management**: Create and view expenses with pagination
- **Category Management**: Create and view expense categories with spending tracking
- **Admin Panel**: User management capabilities for administrators
- **User Profile**: View and manage user profile information

## Prerequisites

- Java 22 or higher
- Maven 3.6+
- Finance Web API running on `http://localhost:8080`

## Build

```bash
mvn clean package
```

## Run

```bash
java -jar target/finance-console-app-1.0.0.jar
```

Or using Maven:

```bash
mvn exec:java -Dexec.mainClass="com.finances.FinanceConsoleApp"
```

## Project Structure

```
finance-console-app/
├── src/main/java/com/finances/
│   ├── FinanceConsoleApp.java        # Main entry point
│   ├── dto/
│   │   ├── request/                  # Request DTOs
│   │   └── response/                 # Response DTOs
│   ├── service/                      # API service layer
│   ├── page/                         # UI pages/screens
│   └── util/                         # Utility classes
└── pom.xml
```

## Configuration

Update the API base URL in `FinanceConsoleApp.java`:

```java
private static final String API_BASE_URL = "http://localhost:8080";
```

## Supported Currencies

CAD, CNY, EUR, GBP, JPY, MXN, NOK, NZD, RUB, TRY, USD

## Expense Categories

HOUSING, TRANSPORTATION, FOOD, HEALTHCARE, DEBT, ENTERTAINMENT, CLOTHING_AND_PERSONAL_ITEMS, TRAVEL, PETS, SAVINGS

## Notes

- User ID is required for creating expense categories (currently requires manual input)
- The application stores the JWT token in memory during the session
- All dates should be formatted as `yyyy-MM-dd HH:mm:ss`

## Future Enhancements

- Edit and delete operations for budgets and expenses
- Expense filtering and advanced search
- Budget reporting and analytics
- User management interface for admins
- Password change functionality
- Session persistence
