package com.finances.page;

import com.finances.exception.ApiException;
import com.finances.service.ExpenseCategoryService;
import com.finances.dto.response.ExpenseCategoryResponse;
import com.finances.dto.response.PagedResponse;
import com.finances.util.ConsoleUI;
import java.util.List;

public class CategorySearchPage {
    private static final int PAGE_SIZE = 5;

    public static void show(ExpenseCategoryService categoryService) {
        ConsoleUI.clearScreen();
        ConsoleUI.printHeader("SEARCH EXPENSE CATEGORIES");

        System.out.println("\nEnter filters (press Enter to skip any):\n");

        System.out.println("Expense types: HOUSING, TRANSPORTATION, FOOD, HEALTHCARE, DEBT,");
        System.out.println("               ENTERTAINMENT, CLOTIHING_AND_PERSONAL_ITEMS, TRAVEL, PETS, SAVINGS");
        String expenseType = ConsoleUI.readInput("Expense type: ");
        if (expenseType.isEmpty()) expenseType = null;

        String minStr = ConsoleUI.readInput("Min category budget: ");
        Double minBudget = minStr.isEmpty() ? null : parseDouble(minStr);

        String maxStr = ConsoleUI.readInput("Max category budget: ");
        Double maxBudget = maxStr.isEmpty() ? null : parseDouble(maxStr);

        System.out.println("\nSort by:");
        System.out.println("1. Created date (default)");
        System.out.println("2. Expense type");
        System.out.println("3. Category budget");
        String sortChoice = ConsoleUI.readInput("Enter choice (1-3): ");
        String sortBy = switch (sortChoice) {
            case "2" -> "expenseType";
            case "3" -> "categoryBudget";
            default  -> "createdAt";
        };

        System.out.println("\nSort direction:");
        System.out.println("1. Descending (default)");
        System.out.println("2. Ascending");
        String dirChoice = ConsoleUI.readInput("Enter choice (1-2): ");
        String sortDir = dirChoice.equals("2") ? "asc" : "desc";

        displayResults(categoryService, expenseType, minBudget, maxBudget, sortBy, sortDir, 0);
    }

    private static void displayResults(
        ExpenseCategoryService categoryService,
        String expenseType, Double minBudget, Double maxBudget,
        String sortBy, String sortDir, int currentPage) {

        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader("CATEGORY SEARCH RESULTS");

            PagedResponse<ExpenseCategoryResponse> response;
            try {
                response = categoryService.searchCategories(expenseType, minBudget, maxBudget,
                    sortBy, sortDir, currentPage, PAGE_SIZE);
            } catch (ApiException e) {
                ConsoleUI.printError(e.getDetail());
                ConsoleUI.pause();
                return;
            }

            if (response == null || response.getContent() == null || response.getContent().isEmpty()) {
                ConsoleUI.printInfo("No categories found matching your criteria.");
                System.out.println("\nPress Enter to go back...");
                ConsoleUI.readInput("");
                return;
            }

            System.out.println("\nFilters: "
                + (expenseType != null ? "type=" + expenseType + " " : "")
                + (minBudget != null ? "min=" + minBudget + " " : "")
                + (maxBudget != null ? "max=" + maxBudget + " " : "")
                + "sortBy=" + sortBy + " sortDir=" + sortDir);

            List<ExpenseCategoryResponse> categories = response.getContent();

            System.out.println("\n" + String.format("%-4s %-30s %-14s %-10s %-10s",
                "ID", "Type", "Budget", "Spent", "Used %"));
            ConsoleUI.printLine();

            for (ExpenseCategoryResponse cat : categories) {
                System.out.println(String.format("%-4d %-30s %-14.2f %-10.2f %-10.1f%%",
                    cat.getId(),
                    truncate(cat.getExpenseType(), 30),
                    cat.getCategoryBudget(),
                    cat.getTotalSpent() != null ? cat.getTotalSpent() : 0.0,
                    cat.getSpentPercent() != null ? cat.getSpentPercent() : 0.0));
            }

            ConsoleUI.printLine();
            int totalPages     = response.getTotalPages()    != null ? response.getTotalPages()    : 1;
            long totalElements = response.getTotalElements() != null ? response.getTotalElements() : 0;

            System.out.println("\nPage " + (currentPage + 1) + " of " + totalPages);
            System.out.println("Total results: " + totalElements);

            System.out.println("\n1. Next Page");
            System.out.println("2. Previous Page");
            System.out.println("3. New Search");
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
                    show(categoryService);
                    return;
                case "4":
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