package com.finances.page;

import com.finances.exception.ApiException;
import com.finances.service.BudgetService;
import com.finances.dto.response.BudgetResponse;
import com.finances.util.ConsoleUI;

public class EditBudgetPage {
    public static void show(BudgetService budgetService, Integer budgetId) {
        BudgetResponse budget;
        // ApiException propagates to caller (BudgetListPage) which handles it
        budget = budgetService.getBudgetById(budgetId);

        if (budget == null) {
            ConsoleUI.printError("Failed to load budget.");
            ConsoleUI.pause();
            return;
        }

        ConsoleUI.clearScreen();
        ConsoleUI.printHeader("EDIT BUDGET");

        System.out.println("\nCurrent Budget Details:");
        System.out.println("ID: " + budget.getId());
        System.out.println("Name: " + budget.getName());
        System.out.println("Value: " + budget.getOriginalAmount());
        System.out.println("Currency: " + budget.getCurrency());
        System.out.println("Recurring: " + (budget.getIsRecurring() ? "Yes" : "No"));

        System.out.println("\n1. Edit Name");
        System.out.println("2. Edit Value");
        System.out.println("3. Edit Currency");
        System.out.println("4. Toggle Recurring");
        System.out.println("5. Cancel");

        String choice = ConsoleUI.readInput("\nSelect option: ");

        try {
            switch (choice) {
                case "1":
                    String newName = ConsoleUI.readInput("Enter new budget name: ");
                    if (!newName.trim().isEmpty()) {
                        BudgetResponse updated = budgetService.updateBudget(budgetId, newName,
                            budget.getOriginalAmount(), budget.getCurrency(), budget.getIsRecurring());
                        if (updated != null) ConsoleUI.printSuccess("Budget updated successfully!");
                        else ConsoleUI.printError("Failed to update budget.");
                    }
                    break;
                case "2":
                    String valueStr = ConsoleUI.readInput("Enter new budget value: ");
                    try {
                        Double newValue = Double.parseDouble(valueStr);
                        BudgetResponse updated = budgetService.updateBudget(budgetId, budget.getName(),
                            newValue, budget.getCurrency(), budget.getIsRecurring());
                        if (updated != null) ConsoleUI.printSuccess("Budget updated successfully!");
                        else ConsoleUI.printError("Failed to update budget.");
                    } catch (NumberFormatException e) {
                        ConsoleUI.printError("Invalid amount format.");
                    }
                    break;
                case "3":
                    String newCurrency = ConsoleUI.readInput("Enter new currency (USD, EUR, GBP, etc.): ");
                    BudgetResponse updated = budgetService.updateBudget(budgetId, budget.getName(),
                        budget.getOriginalAmount(), newCurrency, budget.getIsRecurring());
                    if (updated != null) ConsoleUI.printSuccess("Budget updated successfully!");
                    else ConsoleUI.printError("Failed to update budget.");
                    break;
                case "4":
                    BudgetResponse toggled = budgetService.updateBudget(budgetId, budget.getName(),
                        budget.getOriginalAmount(), budget.getCurrency(), !budget.getIsRecurring());
                    if (toggled != null) ConsoleUI.printSuccess("Budget updated successfully!");
                    else ConsoleUI.printError("Failed to update budget.");
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