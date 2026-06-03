package com.finances.page;

import com.finances.exception.ApiException;
import com.finances.service.ExpenseService;
import com.finances.dto.response.ExpenseResponse;
import com.finances.util.ConsoleUI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EditExpensePage {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void show(ExpenseService expenseService, Integer expenseId) {
        ExpenseResponse expense;
        // ApiException propagates to caller (ExpenseListPage/ExpenseSearchPage) which handles it
        expense = expenseService.getExpenseById(expenseId);

        if (expense == null) {
            ConsoleUI.printError("Failed to load expense.");
            ConsoleUI.pause();
            return;
        }

        ConsoleUI.clearScreen();
        ConsoleUI.printHeader("EDIT EXPENSE");

        System.out.println("\nCurrent Expense Details:");
        System.out.println("ID: " + expense.getId());
        System.out.println("Category: " + expense.getExpenseCategoryType());
        System.out.println("Amount: " + expense.getAmount());
        System.out.println("Date: " + (expense.getExpenseDate() != null ? expense.getExpenseDate().format(formatter) : "N/A"));
        System.out.println("Recurring: " + (expense.getIsRecurring() ? "Yes" : "No"));
        System.out.println("Description: " + (expense.getDescription() != null ? expense.getDescription() : "None"));

        System.out.println("\n1. Edit Amount");
        System.out.println("2. Edit Date");
        System.out.println("3. Toggle Recurring");
        System.out.println("4. Edit Description");
        System.out.println("5. Cancel");

        String choice = ConsoleUI.readInput("\nSelect option: ");

        try {
            switch (choice) {
                case "1":
                    String amountStr = ConsoleUI.readInput("Enter new amount: ");
                    try {
                        Double newAmount = Double.parseDouble(amountStr);
                        ExpenseResponse updated = expenseService.updateExpense(expenseId,
                            expense.getExpenseCategoryId(), expense.getBudgetId(), newAmount,
                            expense.getExpenseDate(), expense.getIsRecurring(), expense.getDescription());
                        if (updated != null) ConsoleUI.printSuccess("Expense updated successfully!");
                        else ConsoleUI.printError("Failed to update expense.");
                    } catch (NumberFormatException e) {
                        ConsoleUI.printError("Invalid amount format.");
                    }
                    break;
                case "2":
                    String dateStr = ConsoleUI.readInput("Enter new date (yyyy-MM-dd HH:mm): ");
                    try {
                        LocalDateTime newDate = LocalDateTime.parse(dateStr, formatter);
                        ExpenseResponse updated = expenseService.updateExpense(expenseId,
                            expense.getExpenseCategoryId(), expense.getBudgetId(), expense.getAmount(),
                            newDate, expense.getIsRecurring(), expense.getDescription());
                        if (updated != null) ConsoleUI.printSuccess("Expense updated successfully!");
                        else ConsoleUI.printError("Failed to update expense.");
                    } catch (Exception e) {
                        ConsoleUI.printError("Invalid date format. Use: yyyy-MM-dd HH:mm");
                    }
                    break;
                case "3":
                    ExpenseResponse toggled = expenseService.updateExpense(expenseId,
                        expense.getExpenseCategoryId(), expense.getBudgetId(), expense.getAmount(),
                        expense.getExpenseDate(), !expense.getIsRecurring(), expense.getDescription());
                    if (toggled != null) ConsoleUI.printSuccess("Expense updated successfully!");
                    else ConsoleUI.printError("Failed to update expense.");
                    break;
                case "4":
                    String newDesc = ConsoleUI.readInput("Enter new description: ");
                    ExpenseResponse updated = expenseService.updateExpense(expenseId,
                        expense.getExpenseCategoryId(), expense.getBudgetId(), expense.getAmount(),
                        expense.getExpenseDate(), expense.getIsRecurring(), newDesc);
                    if (updated != null) ConsoleUI.printSuccess("Expense updated successfully!");
                    else ConsoleUI.printError("Failed to update expense.");
                    break;
                case "5":
                    return;
                default:
                    ConsoleUI.printError("Invalid option.");
            }
        } catch (ApiException e) {
            ConsoleUI.printError(e.getDetail());
        }

        ConsoleUI.pause();
    }
}