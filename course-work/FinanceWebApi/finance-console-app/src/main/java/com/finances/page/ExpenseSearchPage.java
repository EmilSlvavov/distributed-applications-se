package com.finances.page;

import com.finances.exception.ApiException;
import com.finances.service.ExpenseService;
import com.finances.dto.response.ExpenseResponse;
import com.finances.dto.response.PagedResponse;
import com.finances.util.ConsoleUI;
import java.util.List;

public class ExpenseSearchPage {
    private static final int PAGE_SIZE = 5;

    public static void show(ExpenseService expenseService) {
        ConsoleUI.clearScreen();
        ConsoleUI.printHeader("EXPENSE SEARCH & FILTER");

        System.out.println("\n1. Search by Category");
        System.out.println("2. Search by Amount Range");
        System.out.println("3. Search by Category and Amount");
        System.out.println("4. View All Expenses");
        System.out.println("5. Back to Menu");

        String choice = ConsoleUI.readInput("\nSelect search type: ");

        switch (choice) {
            case "1": searchByCategory(expenseService); break;
            case "2": searchByAmountRange(expenseService); break;
            case "3": searchByAll(expenseService); break;
            case "4": viewAllExpenses(expenseService); break;
            case "5": return;
            default:
                ConsoleUI.printError("Invalid option.");
                ConsoleUI.pause();
        }
    }

    private static void searchByCategory(ExpenseService expenseService) {
        ConsoleUI.clearScreen();
        System.out.println("Available categories: HOUSING, TRANSPORTATION, FOOD, HEALTHCARE, DEBT, ENTERTAINMENT, CLOTIHING_AND_PERSONAL_ITEMS, TRAVEL, PETS, SAVINGS");
        String category = ConsoleUI.readInput("\nEnter category to search: ");
        displaySearchResults(expenseService.searchExpenses(category, null, null, 0, PAGE_SIZE), 0, expenseService, category, null, null);
    }

    private static void searchByAmountRange(ExpenseService expenseService) {
        ConsoleUI.clearScreen();
        String minStr = ConsoleUI.readInput("Enter minimum amount (or press Enter to skip): ");
        String maxStr = ConsoleUI.readInput("Enter maximum amount (or press Enter to skip): ");
        Double minAmount = minStr.isEmpty() ? null : Double.parseDouble(minStr);
        Double maxAmount = maxStr.isEmpty() ? null : Double.parseDouble(maxStr);
        displaySearchResults(expenseService.searchExpenses(null, minAmount, maxAmount, 0, PAGE_SIZE), 0, expenseService, null, minAmount, maxAmount);
    }

    private static void searchByAll(ExpenseService expenseService) {
        ConsoleUI.clearScreen();
        System.out.println("Available categories: HOUSING, TRANSPORTATION, FOOD, HEALTHCARE, DEBT, ENTERTAINMENT, CLOTIHING_AND_PERSONAL_ITEMS, TRAVEL, PETS, SAVINGS");
        String category = ConsoleUI.readInput("\nEnter category (or press Enter to skip): ");
        String minStr = ConsoleUI.readInput("Enter minimum amount (or press Enter to skip): ");
        String maxStr = ConsoleUI.readInput("Enter maximum amount (or press Enter to skip): ");
        Double minAmount = minStr.isEmpty() ? null : Double.parseDouble(minStr);
        Double maxAmount = maxStr.isEmpty() ? null : Double.parseDouble(maxStr);
        String categoryFilter = category.isEmpty() ? null : category;
        displaySearchResults(expenseService.searchExpenses(categoryFilter, minAmount, maxAmount, 0, PAGE_SIZE), 0, expenseService, categoryFilter, minAmount, maxAmount);
    }

    private static void viewAllExpenses(ExpenseService expenseService) {
        displaySearchResults(expenseService.getAllExpenses(0, PAGE_SIZE), 0, expenseService, null, null, null);
    }

    private static void displaySearchResults(PagedResponse<ExpenseResponse> response, int currentPage,
        ExpenseService expenseService, String category, Double minAmount, Double maxAmount) {
        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader("SEARCH RESULTS");

            if (response == null || response.getContent() == null || response.getContent().isEmpty()) {
                ConsoleUI.printInfo("No expenses found matching your criteria.");
                System.out.println("\nPress Enter to go back...");
                ConsoleUI.readInput("");
                return;
            }

            List<ExpenseResponse> expenses = response.getContent();

            System.out.println("\n" + String.format("%-4s %-25s %-12s %-12s %-10s",
                "ID", "Category", "Amount", "Date", "Recurring"));
            ConsoleUI.printLine();

            for (ExpenseResponse expense : expenses) {
                String dateStr = expense.getExpenseDate() != null ? expense.getExpenseDate().toString() : "N/A";
                System.out.println(String.format("%-4d %-25s %-12.2f %-12s %-10s",
                    expense.getId(),
                    truncate(expense.getExpenseCategoryType(), 25),
                    expense.getAmount(),
                    dateStr.substring(0, Math.min(12, dateStr.length())),
                    expense.getIsRecurring() != null && expense.getIsRecurring() ? "Yes" : "No"));
            }

            ConsoleUI.printLine();
            int totalPages = response.getTotalPages() != null ? response.getTotalPages() : 1;
            long totalElements = response.getTotalElements() != null ? response.getTotalElements() : 0;

            System.out.println("\nPage " + (currentPage + 1) + " of " + totalPages);
            System.out.println("Total expenses: " + totalElements);

            System.out.println("\n1. Next Page");
            System.out.println("2. Previous Page");
            System.out.println("3. Edit Expense");
            System.out.println("4. Delete Expense");
            System.out.println("5. Back to Menu");
            String choice = ConsoleUI.readInput("\nSelect option: ");

            switch (choice) {
                case "1":
                    if (currentPage < totalPages - 1) {
                        currentPage++;
                        response = (category != null || minAmount != null || maxAmount != null)
                            ? expenseService.searchExpenses(category, minAmount, maxAmount, currentPage, PAGE_SIZE)
                            : expenseService.getAllExpenses(currentPage, PAGE_SIZE);
                    } else { ConsoleUI.printInfo("You are on the last page."); ConsoleUI.pause(); }
                    break;
                case "2":
                    if (currentPage > 0) {
                        currentPage--;
                        response = (category != null || minAmount != null || maxAmount != null)
                            ? expenseService.searchExpenses(category, minAmount, maxAmount, currentPage, PAGE_SIZE)
                            : expenseService.getAllExpenses(currentPage, PAGE_SIZE);
                    } else { ConsoleUI.printInfo("You are on the first page."); ConsoleUI.pause(); }
                    break;
                case "3":
                    String expenseIdStr = ConsoleUI.readInput("Enter expense ID to edit: ");
                    try {
                        Integer expenseId = Integer.parseInt(expenseIdStr);
                        try {
                            EditExpensePage.show(expenseService, expenseId);
                        } catch (ApiException e) {
                            ConsoleUI.printError(e.getDetail());
                            ConsoleUI.pause();
                        }
                        response = (category != null || minAmount != null || maxAmount != null)
                            ? expenseService.searchExpenses(category, minAmount, maxAmount, currentPage, PAGE_SIZE)
                            : expenseService.getAllExpenses(currentPage, PAGE_SIZE);
                    } catch (NumberFormatException e) {
                        ConsoleUI.printError("Invalid ID format.");
                        ConsoleUI.pause();
                    }
                    break;
                case "4":
                    String deleteIdStr = ConsoleUI.readInput("Enter expense ID to delete: ");
                    try {
                        Integer expenseId = Integer.parseInt(deleteIdStr);
                        try {
                            if (expenseService.deleteExpense(expenseId)) {
                                ConsoleUI.printSuccess("Expense deleted successfully!");
                                response = (category != null || minAmount != null || maxAmount != null)
                                    ? expenseService.searchExpenses(category, minAmount, maxAmount, currentPage, PAGE_SIZE)
                                    : expenseService.getAllExpenses(currentPage, PAGE_SIZE);
                            } else {
                                ConsoleUI.printError("Failed to delete expense.");
                            }
                        } catch (ApiException e) {
                            ConsoleUI.printError(e.getDetail());
                        }
                        ConsoleUI.pause();
                    } catch (NumberFormatException e) {
                        ConsoleUI.printError("Invalid ID format.");
                        ConsoleUI.pause();
                    }
                    break;
                case "5":
                    return;
                default:
                    ConsoleUI.printError("Invalid option.");
                    ConsoleUI.pause();
            }
        }
    }

    private static String truncate(String str, int length) {
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }
}