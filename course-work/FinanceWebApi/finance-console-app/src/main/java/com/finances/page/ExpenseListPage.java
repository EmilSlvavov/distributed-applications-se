package com.finances.page;

import com.finances.exception.ApiException;
import com.finances.service.ExpenseService;
import com.finances.dto.response.ExpenseResponse;
import com.finances.dto.response.PagedResponse;
import com.finances.util.ConsoleUI;
import java.util.List;

public class ExpenseListPage {
    private static final int PAGE_SIZE = 5;

    public static void show(ExpenseService expenseService) {
        int currentPage = 0;

        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader("YOUR EXPENSES");

            PagedResponse<ExpenseResponse> response = expenseService.getAllExpenses(currentPage, PAGE_SIZE);

            if (response == null || response.getContent() == null || response.getContent().isEmpty()) {
                ConsoleUI.printInfo("No expenses found.");
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
                    if (currentPage < totalPages - 1) currentPage++;
                    else { ConsoleUI.printInfo("You are on the last page."); ConsoleUI.pause(); }
                    break;
                case "2":
                    if (currentPage > 0) currentPage--;
                    else { ConsoleUI.printInfo("You are on the first page."); ConsoleUI.pause(); }
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