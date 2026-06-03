package com.finances.page;

import com.finances.exception.ApiException;
import com.finances.service.BudgetService;
import com.finances.dto.response.BudgetResponse;
import com.finances.dto.response.PagedResponse;
import com.finances.util.ConsoleUI;
import java.util.List;

public class BudgetSearchPage {
    private static final int PAGE_SIZE = 5;

    public static void show(BudgetService budgetService) {
        ConsoleUI.clearScreen();
        ConsoleUI.printHeader("SEARCH BUDGETS");

        System.out.println("\nEnter filters (press Enter to skip any):\n");

        String currency = ConsoleUI.readInput("Currency (e.g. EUR, USD): ");
        if (currency.isEmpty()) currency = null;

        String recurringInput = ConsoleUI.readInput("Recurring? (yes / no / skip): ").toLowerCase();
        Boolean isRecurring = recurringInput.equals("yes") ? Boolean.TRUE
            : recurringInput.equals("no") ? Boolean.FALSE : null;

        String minStr = ConsoleUI.readInput("Min original amount: ");
        Double minOriginalAmount = minStr.isEmpty() ? null : parseDouble(minStr);

        String maxStr = ConsoleUI.readInput("Max original amount: ");
        Double maxOriginalAmount = maxStr.isEmpty() ? null : parseDouble(maxStr);

        System.out.println("\nSort by:");
        System.out.println("1. Created date (default)");
        System.out.println("2. Name");
        System.out.println("3. Original amount");
        String sortChoice = ConsoleUI.readInput("Enter choice (1-3): ");
        String sortBy = switch (sortChoice) {
            case "2" -> "name";
            case "3" -> "value";
            default  -> "createdAt";
        };

        System.out.println("\nSort direction:");
        System.out.println("1. Descending (default)");
        System.out.println("2. Ascending");
        String dirChoice = ConsoleUI.readInput("Enter choice (1-2): ");
        String sortDir = dirChoice.equals("2") ? "asc" : "desc";

        displayResults(budgetService, currency, isRecurring,
            minOriginalAmount, maxOriginalAmount, sortBy, sortDir, 0);
    }

    private static void displayResults(
        BudgetService budgetService,
        String currency, Boolean isRecurring,
        Double minOriginalAmount, Double maxOriginalAmount,
        String sortBy, String sortDir, int currentPage) {

        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader("BUDGET SEARCH RESULTS");

            PagedResponse<BudgetResponse> response;
            try {
                response = budgetService.searchBudgets(currency, isRecurring,
                    minOriginalAmount, maxOriginalAmount, sortBy, sortDir,
                    currentPage, PAGE_SIZE);
            } catch (ApiException e) {
                ConsoleUI.printError(e.getDetail());
                ConsoleUI.pause();
                return;
            }

            if (response == null || response.getContent() == null || response.getContent().isEmpty()) {
                ConsoleUI.printInfo("No budgets found matching your criteria.");
                System.out.println("\nPress Enter to go back...");
                ConsoleUI.readInput("");
                return;
            }

            // Print active filters summary
            System.out.println("\nFilters: "
                + (currency != null ? "currency=" + currency + " " : "")
                + (isRecurring != null ? "recurring=" + isRecurring + " " : "")
                + (minOriginalAmount != null ? "min=" + minOriginalAmount + " " : "")
                + (maxOriginalAmount != null ? "max=" + maxOriginalAmount + " " : "")
                + "sortBy=" + sortBy + " sortDir=" + sortDir);

            List<BudgetResponse> budgets = response.getContent();

            System.out.println("\n" + String.format("%-4s %-20s %-12s %-10s %-12s %-12s %-10s",
                "ID", "Name", "Budget", "Currency", "Spent", "Remaining", "Used %"));
            ConsoleUI.printLine();

            for (BudgetResponse budget : budgets) {
                double original  = budget.getOriginalAmount()  != null ? budget.getOriginalAmount()  : 0.0;
                double spent     = budget.getSpentAmount()     != null ? budget.getSpentAmount()     : 0.0;
                double remaining = budget.getRemainingAmount() != null ? budget.getRemainingAmount() : original - spent;
                double percent   = budget.getSpentPercent()    != null ? budget.getSpentPercent()    : 0.0;

                System.out.println(String.format("%-4d %-20s %-12.2f %-10s %-12.2f %-12.2f %-10.1f%%",
                    budget.getId(),
                    truncate(budget.getName(), 20),
                    original,
                    budget.getCurrency(),
                    spent,
                    remaining,
                    percent));

                if (budget.getWarning() != null) {
                    System.out.println("     ⚠  " + budget.getWarning());
                }
            }

            ConsoleUI.printLine();
            int totalPages    = response.getTotalPages()    != null ? response.getTotalPages()    : 1;
            long totalElements = response.getTotalElements() != null ? response.getTotalElements() : 0;

            System.out.println("\nPage " + (currentPage + 1) + " of " + totalPages);
            System.out.println("Total results: " + totalElements);

            System.out.println("\n1. Next Page");
            System.out.println("2. Previous Page");
            System.out.println("3. Edit Budget");
            System.out.println("4. Delete Budget");
            System.out.println("5. New Search");
            System.out.println("6. Back to Menu");
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
                    String editIdStr = ConsoleUI.readInput("Enter budget ID to edit: ");
                    try {
                        Integer budgetId = Integer.parseInt(editIdStr);
                        try {
                            EditBudgetPage.show(budgetService, budgetId);
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
                    String deleteIdStr = ConsoleUI.readInput("Enter budget ID to delete: ");
                    try {
                        Integer budgetId = Integer.parseInt(deleteIdStr);
                        try {
                            if (budgetService.deleteBudget(budgetId)) {
                                ConsoleUI.printSuccess("Budget deleted successfully!");
                            } else {
                                ConsoleUI.printError("Failed to delete budget.");
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
                    show(budgetService);
                    return;
                case "6":
                    return;
                default:
                    ConsoleUI.printError("Invalid option.");
                    ConsoleUI.pause();
            }
        }
    }

    private static Double parseDouble(String input) {
        try { return Double.parseDouble(input); }
        catch (NumberFormatException e) { return null; }
    }

    private static String truncate(String str, int length) {
        return str != null && str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }
}