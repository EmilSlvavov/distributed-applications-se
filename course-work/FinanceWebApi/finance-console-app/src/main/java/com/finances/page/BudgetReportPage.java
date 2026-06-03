package com.finances.page;

import com.finances.exception.ApiException;
import com.finances.service.BudgetService;
import com.finances.dto.response.BudgetResponse;
import com.finances.util.ConsoleUI;

public class BudgetReportPage {
    public static void show(BudgetService budgetService) {
        int currentPage = 0;
        final int PAGE_SIZE = 5;

        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader("BUDGET REPORTS & ANALYTICS");

            var response = budgetService.getAllBudgets(currentPage, PAGE_SIZE);

            if (response == null || response.getContent() == null || response.getContent().isEmpty()) {
                ConsoleUI.printInfo("No budgets found.");
                System.out.println("\nPress Enter to go back...");
                ConsoleUI.readInput("");
                return;
            }

            var budgets = response.getContent();
            Double totalBudget = 0.0;
            Double totalSpent = 0.0;

            System.out.println("\n" + String.format("%-4s %-20s %-12s %-12s %-12s %-10s",
                "ID", "Name", "Budget", "Spent", "Remaining", "% Used"));
            ConsoleUI.printLine();

            for (BudgetResponse budget : budgets) {
                Double spent = budget.getSpentAmount() != null ? budget.getSpentAmount() : 0;
                Double remaining = budget.getOriginalAmount() - spent;
                Double percentUsed = (spent / budget.getOriginalAmount()) * 100;

                totalBudget += budget.getOriginalAmount();
                totalSpent += spent;

                System.out.println(String.format("%-4d %-20s %-12.2f %-12.2f %-12.2f %-10.1f%%",
                    budget.getId(),
                    truncate(budget.getName(), 20),
                    budget.getOriginalAmount(),
                    spent,
                    remaining,
                    percentUsed));
            }

            ConsoleUI.printLine();
            System.out.println("\n=== SUMMARY ===");
            System.out.println(String.format("Total Budget: %.2f", totalBudget));
            System.out.println(String.format("Total Spent: %.2f", totalSpent));
            System.out.println(String.format("Total Remaining: %.2f", totalBudget - totalSpent));
            System.out.println(String.format("Overall Usage: %.1f%%", (totalSpent / totalBudget) * 100));

            int totalPages = response.getTotalPages() != null ? response.getTotalPages() : 1;

            System.out.println("\n1. Next Page");
            System.out.println("2. Previous Page");
            System.out.println("3. View Budget Details");
            System.out.println("4. Back to Menu");
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
                    String budgetIdStr = ConsoleUI.readInput("Enter budget ID: ");
                    try {
                        Integer budgetId = Integer.parseInt(budgetIdStr);
                        try {
                            displayBudgetDetails(budgetService, budgetId);
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
                    return;
                default:
                    ConsoleUI.printError("Invalid option.");
                    ConsoleUI.pause();
            }
        }
    }

    private static void displayBudgetDetails(BudgetService budgetService, Integer budgetId) {
        ConsoleUI.clearScreen();

        // ApiException propagates to caller which handles it
        BudgetResponse budget = budgetService.getBudgetById(budgetId);

        if (budget == null) {
            ConsoleUI.printError("Failed to load budget.");
            ConsoleUI.pause();
            return;
        }

        ConsoleUI.printHeader("BUDGET DETAILS");

        Double spent = budget.getSpentAmount() != null ? budget.getSpentAmount() : 0;
        Double remaining = budget.getOriginalAmount() - spent;
        Double percentUsed = (spent / budget.getOriginalAmount()) * 100;

        System.out.println("\nName: " + budget.getName());
        System.out.println("Currency: " + budget.getCurrency());
        System.out.println("Total Budget: " + budget.getOriginalAmount());
        System.out.println("Amount Spent: " + spent);
        System.out.println("Remaining Amount: " + remaining);
        System.out.println("Percentage Used: " + String.format("%.1f%%", percentUsed));
        System.out.println("Recurring: " + (budget.getIsRecurring() ? "Yes" : "No"));
        System.out.println("Created At: " + budget.getCreatedAt());

        if (percentUsed > 90) {
            ConsoleUI.printWarning("⚠️  WARNING: You have exceeded 90% of your budget!");
        } else if (percentUsed > 75) {
            ConsoleUI.printWarning("⚠️  CAUTION: You have used 75% of your budget.");
        }

        System.out.println("\nPress Enter to go back...");
        ConsoleUI.readInput("");
    }

    private static String truncate(String str, int length) {
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }
}