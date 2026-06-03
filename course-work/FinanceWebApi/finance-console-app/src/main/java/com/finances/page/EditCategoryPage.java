package com.finances.page;

import com.finances.exception.ApiException;
import com.finances.service.ExpenseCategoryService;
import com.finances.dto.response.ExpenseCategoryResponse;
import com.finances.util.ConsoleUI;

public class EditCategoryPage {

    public static void show(ExpenseCategoryService categoryService, Integer categoryId) {
        ExpenseCategoryResponse category;
        // ApiException propagates to caller (CategoryListPage) which handles it
        category = categoryService.getCategoryById(categoryId);

        if (category == null) {
            ConsoleUI.printError("Failed to load category.");
            ConsoleUI.pause();
            return;
        }

        ConsoleUI.clearScreen();
        ConsoleUI.printHeader("EDIT EXPENSE CATEGORY");

        System.out.println("\nCurrent Category Details:");
        System.out.println("ID:           " + category.getId());
        System.out.println("Expense Type: " + category.getExpenseType());
        System.out.println("Budget:       " + category.getCategoryBudget());

        System.out.println("\n1. Edit Expense Type");
        System.out.println("2. Edit Budget");
        System.out.println("3. Cancel");

        String choice = ConsoleUI.readInput("\nSelect option: ");

        // ApiException caught here so update errors show backend message
        try {
            switch (choice) {
                case "1":
                    String[] expenseTypes = {
                        "HOUSING", "TRANSPORTATION", "FOOD", "HEALTHCARE", "DEBT",
                        "ENTERTAINMENT", "CLOTIHING_AND_PERSONAL_ITEMS", "TRAVEL", "PETS", "SAVINGS"
                    };
                    System.out.println("\nSelect new expense type:");
                    for (int i = 0; i < expenseTypes.length; i++) {
                        System.out.println((i + 1) + ". " + expenseTypes[i]);
                    }
                    String typeChoice = ConsoleUI.readInput("Enter choice (1-10): ");
                    try {
                        String newType = expenseTypes[Integer.parseInt(typeChoice) - 1];
                        ExpenseCategoryResponse updated = categoryService.updateCategory(
                            categoryId, newType, category.getCategoryBudget(), category.getUserId());
                        if (updated != null) ConsoleUI.printSuccess("Category updated successfully!");
                        else ConsoleUI.printError("Failed to update category.");
                    } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                        ConsoleUI.printError("Invalid choice.");
                    }
                    break;
                case "2":
                    String budgetStr = ConsoleUI.readInput("Enter new budget amount: ");
                    try {
                        Double newBudget = Double.parseDouble(budgetStr);
                        ExpenseCategoryResponse updated = categoryService.updateCategory(
                            categoryId, category.getExpenseType(), newBudget, category.getUserId());
                        if (updated != null) ConsoleUI.printSuccess("Category updated successfully!");
                        else ConsoleUI.printError("Failed to update category.");
                    } catch (NumberFormatException e) {
                        ConsoleUI.printError("Invalid amount format.");
                    }
                    break;
                case "3":
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