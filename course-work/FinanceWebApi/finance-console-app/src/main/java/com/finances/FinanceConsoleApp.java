package com.finances;

import com.finances.exception.ApiException;
import com.finances.service.*;
import com.finances.page.*;
import com.finances.util.ConsoleUI;

public class FinanceConsoleApp {
    private static final String API_BASE_URL = "http://localhost:8080";
    private static final int PAGE_SIZE = 5;

    public static void main(String[] args) {
        ApiClient apiClient = new ApiClient(API_BASE_URL);
        AuthService authService = new AuthService(apiClient);

        try {
            if (authService.isLoggedIn()) {
                showMainMenu(apiClient, authService);
            } else {
                showAuthMenu(apiClient, authService);
            }
        } catch (ApiException e) {
            ConsoleUI.printError(e.getDetail());
        } catch (Exception e) {
            ConsoleUI.printError("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void showAuthMenu(ApiClient apiClient, AuthService authService) {
        while (!authService.isLoggedIn()) {
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader("FINANCE MANAGER - AUTHENTICATION");

            System.out.println("\n1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");

            String choice = ConsoleUI.readInput("\nSelect an option: ");

            switch (choice) {
                case "1":
                    LoginPage.show(authService);
                    break;
                case "2":
                    UserService userService = new UserService(apiClient);
                    RegisterPage.show(userService);
                    break;
                case "3":
                    System.out.println("Goodbye!");
                    System.exit(0);
                    break;
                default:
                    ConsoleUI.printError("Invalid option.");
                    ConsoleUI.pause();
            }
        }

        showMainMenu(apiClient, authService);
    }

    private static void showMainMenu(ApiClient apiClient, AuthService authService) {
        UserService userService = new UserService(apiClient);
        BudgetService budgetService = new BudgetService(apiClient);
        ExpenseService expenseService = new ExpenseService(apiClient);
        ExpenseCategoryService categoryService = new ExpenseCategoryService(apiClient);

        com.finances.dto.response.UserResponse me = userService.getCurrentUser();
        boolean isAdmin = me != null && "ADMIN".equals(me.getRole());

        while (authService.isLoggedIn()) {
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader("FINANCE MANAGER - DASHBOARD");

            String currentUser = authService.getCurrentUsername();
            if (currentUser != null) {
                System.out.println("Welcome, " + currentUser + "!\n");
            }

            System.out.println("1. Budgets");
            System.out.println("2. Expenses");
            System.out.println("3. Expense Categories");
            System.out.println("4. Profile");
            if (isAdmin) {
                System.out.println("5. Admin Panel");
                System.out.println("6. Logout");
            } else {
                System.out.println("5. Logout");
            }

            String choice = ConsoleUI.readInput("\nSelect an option: ");

            switch (choice) {
                case "1":
                    showBudgetMenu(budgetService);
                    break;
                case "2":
                    showExpenseMenu(expenseService, categoryService, budgetService);
                    break;
                case "3":
                    showCategoryMenu(categoryService, userService);
                    break;
                case "4":
                    showProfileMenu(userService);
                    break;
                case "5":
                    if (isAdmin) {
                        showAdminMenu(userService);
                    } else {
                        authService.logout();
                        SessionManager.clearSession();
                    }
                    break;
                case "6":
                    if (isAdmin) {
                        authService.logout();
                        SessionManager.clearSession();
                    } else {
                        ConsoleUI.printError("Invalid option.");
                        ConsoleUI.pause();
                    }
                    break;
                default:
                    ConsoleUI.printError("Invalid option.");
                    ConsoleUI.pause();
            }
        }

        showAuthMenu(apiClient, authService);
    }

    private static void showBudgetMenu(BudgetService budgetService) {
        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader("BUDGET MANAGEMENT");

            System.out.println("\n1. View All Budgets");
            System.out.println("2. Create New Budget");
            System.out.println("3. View Budget Reports");
            System.out.println("4. Search Budgets");
            System.out.println("5. Back to Main Menu");

            String choice = ConsoleUI.readInput("\nSelect an option: ");

            switch (choice) {
                case "1":
                    BudgetListPage.show(budgetService);
                    break;
                case "2":
                    CreateBudgetPage.show(budgetService);
                    break;
                case "3":
                    BudgetReportPage.show(budgetService);
                    break;
                case "4":
                    BudgetSearchPage.show(budgetService);
                    break;
                case "5":
                    return;
                default:
                    ConsoleUI.printError("Invalid option.");
                    ConsoleUI.pause();
            }
        }
    }

    private static void showExpenseMenu(ExpenseService expenseService, ExpenseCategoryService categoryService, BudgetService budgetService) {
        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader("EXPENSE MANAGEMENT");

            System.out.println("\n1. View All Expenses");
            System.out.println("2. Create New Expense");
            System.out.println("3. Search & Filter Expenses");
            System.out.println("4. Back to Main Menu");

            String choice = ConsoleUI.readInput("\nSelect an option: ");

            switch (choice) {
                case "1":
                    ExpenseListPage.show(expenseService);
                    break;
                case "2":
                    CreateExpensePage.show(expenseService, categoryService, budgetService);
                    break;
                case "3":
                    ExpenseSearchPage.show(expenseService);
                    break;
                case "4":
                    return;
                default:
                    ConsoleUI.printError("Invalid option.");
                    ConsoleUI.pause();
            }
        }
    }

    private static void showCategoryMenu(ExpenseCategoryService categoryService, UserService userService) {
        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader("EXPENSE CATEGORY MANAGEMENT");

            System.out.println("\n1. View All Categories");
            System.out.println("2. Create New Category");
            System.out.println("3. Search Categories");
            System.out.println("4. Back to Main Menu");

            String choice = ConsoleUI.readInput("\nSelect an option: ");

            switch (choice) {
                case "1":
                    CategoryListPage.show(categoryService);
                    break;
                case "2":
                    CreateCategoryPage.show(categoryService, userService);
                    break;
                case "3":
                    CategorySearchPage.show(categoryService);
                    break;
                case "4":
                    return;
                default:
                    ConsoleUI.printError("Invalid option.");
                    ConsoleUI.pause();
            }
        }
    }

    private static void showProfileMenu(UserService userService) {
        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader("USER PROFILE");

            System.out.println("\n1. View Profile");
            System.out.println("2. Change Password");
            System.out.println("3. Back to Main Menu");

            String choice = ConsoleUI.readInput("\nSelect an option: ");

            switch (choice) {
                case "1":
                    ProfilePage.show(userService);
                    break;
                case "2":
                    String userIdStr = ConsoleUI.readInput("Enter your user ID: ");
                    try {
                        Integer userId = Integer.parseInt(userIdStr);
                        ChangePasswordPage.show(userService, userId);
                    } catch (NumberFormatException e) {
                        ConsoleUI.printError("Invalid ID format.");
                        ConsoleUI.pause();
                    }
                    break;
                case "3":
                    return;
                default:
                    ConsoleUI.printError("Invalid option.");
                    ConsoleUI.pause();
            }
        }
    }

    private static void showAdminMenu(UserService userService) {
        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader("ADMIN PANEL");

            System.out.println("\n1. User Management");
            System.out.println("2. Create New User");
            System.out.println("3. Search Users");
            System.out.println("4. Back to Main Menu");

            String choice = ConsoleUI.readInput("\nSelect an option: ");

            switch (choice) {
                case "1":
                    UserManagementPage.show(userService);
                    break;
                case "2":
                    CreateUserPage.show(userService);
                    break;
                case "3":
                    UserSearchPage.show(userService);
                    break;
                case "4":
                    return;
                default:
                    ConsoleUI.printError("Invalid option.");
                    ConsoleUI.pause();
            }
        }
    }
}