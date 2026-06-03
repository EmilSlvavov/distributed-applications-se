package com.finances.page;

import com.finances.exception.ApiException;
import com.finances.service.ExpenseService;
import com.finances.service.ExpenseCategoryService;
import com.finances.service.BudgetService;
import com.finances.dto.response.BudgetResponse;
import com.finances.dto.response.ExpenseCategoryResponse;
import com.finances.dto.response.PagedResponse;
import com.finances.util.ConsoleUI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CreateExpensePage {
    public static void show(ExpenseService expenseService, ExpenseCategoryService categoryService, BudgetService budgetService) {
        ConsoleUI.clearScreen();
        ConsoleUI.printHeader("CREATE NEW EXPENSE");

        PagedResponse<ExpenseCategoryResponse> categoryResponse = categoryService.getAllCategories(0, 100);
        if (categoryResponse == null || categoryResponse.getContent() == null || categoryResponse.getContent().isEmpty()) {
            ConsoleUI.printError("No expense categories available. Please create a category first.");
            ConsoleUI.pause();
            return;
        }

        System.out.println("\nSelect expense category:");
        var categories = categoryResponse.getContent();
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i).getExpenseType()
                + " (Budget: " + categories.get(i).getCategoryBudget() + ")");
        }

        String categoryChoice = ConsoleUI.readInput("Enter choice: ");
        Integer expenseCategoryId;
        try {
            expenseCategoryId = categories.get(Integer.parseInt(categoryChoice) - 1).getId();
        } catch (Exception e) {
            ConsoleUI.printError("Invalid category choice.");
            ConsoleUI.pause();
            return;
        }

        PagedResponse<BudgetResponse> budgetResponse = budgetService.getAllBudgets(0, 100);
        if (budgetResponse == null || budgetResponse.getContent() == null || budgetResponse.getContent().isEmpty()) {
            ConsoleUI.printError("No budgets available. Please create a budget first.");
            ConsoleUI.pause();
            return;
        }

        System.out.println("\nSelect budget:");
        var budgets = budgetResponse.getContent();
        for (int i = 0; i < budgets.size(); i++) {
            System.out.println((i + 1) + ". " + budgets.get(i).getName()
                + " (Remaining: " + budgets.get(i).getRemainingAmount() + " " + budgets.get(i).getCurrency() + ")");
        }

        String budgetChoice = ConsoleUI.readInput("Enter choice: ");
        Integer budgetId;
        try {
            budgetId = budgets.get(Integer.parseInt(budgetChoice) - 1).getId();
        } catch (Exception e) {
            ConsoleUI.printError("Invalid budget choice.");
            ConsoleUI.pause();
            return;
        }

        String amountInput = ConsoleUI.readInput("\nEnter expense amount: ");
        Double amount;
        try {
            amount = Double.parseDouble(amountInput);
        } catch (NumberFormatException e) {
            ConsoleUI.printError("Invalid amount. Please enter a valid number.");
            ConsoleUI.pause();
            return;
        }

        String dateInput = ConsoleUI.readInput("Enter expense date (yyyy-MM-dd HH:mm:ss) [default: now]: ");
        LocalDateTime expenseDate;
        try {
            expenseDate = dateInput.isEmpty() ? LocalDateTime.now()
                : LocalDateTime.parse(dateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            ConsoleUI.printError("Invalid date format. Using current time.");
            expenseDate = LocalDateTime.now();
        }

        System.out.println("\nIs this a recurring expense?");
        System.out.println("1. Yes");
        System.out.println("2. No");
        Boolean isRecurring = ConsoleUI.readInput("Enter choice (1 or 2): ").equals("1");

        String description = ConsoleUI.readInput("Enter description (optional): ");
        if (description.isEmpty()) description = null;

        try {
            var expense = expenseService.createExpense(expenseCategoryId, budgetId, amount, expenseDate, isRecurring, description);
            if (expense != null) {
                ConsoleUI.printSuccess("Expense created successfully!");
                System.out.println("Expense ID: " + expense.getId());
                System.out.println("Category: " + expense.getExpenseCategoryType());
                System.out.println("Amount: " + expense.getAmount());
                System.out.println("Date: " + expense.getExpenseDate());
            } else {
                ConsoleUI.printError("Failed to create expense. Please try again.");
            }
        } catch (ApiException e) {
            ConsoleUI.printError(e.getDetail());
        }
        ConsoleUI.pause();
    }
}