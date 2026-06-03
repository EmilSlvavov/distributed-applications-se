package com.finances.page;

import com.finances.exception.ApiException;
import com.finances.service.BudgetService;
import com.finances.dto.response.BudgetResponse;
import com.finances.util.ConsoleUI;

public class CreateBudgetPage {
    public static void show(BudgetService budgetService) {
        ConsoleUI.clearScreen();
        ConsoleUI.printHeader("CREATE NEW BUDGET");

        String name = ConsoleUI.readInput("\nEnter budget name: ");

        String valueInput = ConsoleUI.readInput("Enter budget value: ");
        Double originalAmount;
        try {
            originalAmount = Double.parseDouble(valueInput);
        } catch (NumberFormatException e) {
            ConsoleUI.printError("Invalid amount. Please enter a valid number.");
            ConsoleUI.pause();
            return;
        }

        System.out.println("\nSelect currency:");
        String[] currencies = {"CAD", "CNY", "EUR", "GBP", "JPY", "MXN", "NOK", "NZD", "RUB", "TRY", "USD"};
        for (int i = 0; i < currencies.length; i++) {
            System.out.println((i + 1) + ". " + currencies[i]);
        }
        String currencyChoice = ConsoleUI.readInput("Enter choice (1-11): ");
        String currency;
        try {
            currency = currencies[Integer.parseInt(currencyChoice) - 1];
        } catch (Exception e) {
            ConsoleUI.printError("Invalid currency choice.");
            ConsoleUI.pause();
            return;
        }

        System.out.println("\nIs this a recurring budget?");
        System.out.println("1. Yes");
        System.out.println("2. No");
        String recurringChoice = ConsoleUI.readInput("Enter choice (1 or 2): ");
        Boolean isRecurring = recurringChoice.equals("1");

        try {
            BudgetResponse budget = budgetService.createBudget(name, originalAmount, currency, isRecurring);
            if (budget != null) {
                ConsoleUI.printSuccess("Budget created successfully!");
                System.out.println("Budget ID: " + budget.getId());
                System.out.println("Name: " + budget.getName());
                System.out.println("Value: " + budget.getOriginalAmount() + " " + budget.getCurrency());
            } else {
                ConsoleUI.printError("Failed to create budget. Please try again.");
            }
        } catch (ApiException e) {
            ConsoleUI.printError(e.getDetail());
        }
        ConsoleUI.pause();
    }
}