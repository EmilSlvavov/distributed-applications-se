package com.finances.page;

import com.finances.exception.ApiException;
import com.finances.service.ExpenseCategoryService;
import com.finances.dto.response.ExpenseCategoryResponse;
import com.finances.dto.response.PagedResponse;
import com.finances.util.ConsoleUI;
import java.util.List;

public class CategoryListPage {
    private static final int PAGE_SIZE = 5;

    public static void show(ExpenseCategoryService categoryService) {
        int currentPage = 0;

        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader("EXPENSE CATEGORIES");

            PagedResponse<ExpenseCategoryResponse> response = categoryService.getAllCategories(currentPage, PAGE_SIZE);

            if (response == null || response.getContent() == null || response.getContent().isEmpty()) {
                ConsoleUI.printInfo("No expense categories found.");
                System.out.println("\nPress Enter to go back...");
                ConsoleUI.readInput("");
                return;
            }

            List<ExpenseCategoryResponse> categories = response.getContent();

            System.out.println("\n" + String.format("%-4s %-25s %-12s %-10s %-10s",
                "ID", "Type", "Budget", "Spent", "Spent %"));
            ConsoleUI.printLine();

            for (ExpenseCategoryResponse category : categories) {
                System.out.println(String.format("%-4d %-25s %-12.2f %-10.2f %-10.1f%%",
                    category.getId(),
                    truncate(category.getExpenseType(), 25),
                    category.getCategoryBudget(),
                    category.getTotalSpent()   != null ? category.getTotalSpent()   : 0.0,
                    category.getSpentPercent() != null ? category.getSpentPercent() : 0.0));
            }

            ConsoleUI.printLine();
            int totalPages     = response.getTotalPages()    != null ? response.getTotalPages()    : 1;
            long totalElements = response.getTotalElements() != null ? response.getTotalElements() : 0;

            System.out.println("\nPage " + (currentPage + 1) + " of " + totalPages);
            System.out.println("Total categories: " + totalElements);

            System.out.println("\n1. Next Page");
            System.out.println("2. Previous Page");
            System.out.println("3. Edit Category");
            System.out.println("4. Delete Category");
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
                    String editIdStr = ConsoleUI.readInput("Enter category ID to edit: ");
                    try {
                        Integer categoryId = Integer.parseInt(editIdStr);
                        try {
                            EditCategoryPage.show(categoryService, categoryId);
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
                    String deleteIdStr = ConsoleUI.readInput("Enter category ID to delete: ");
                    try {
                        Integer categoryId = Integer.parseInt(deleteIdStr);
                        try {
                            if (categoryService.deleteCategory(categoryId)) {
                                ConsoleUI.printSuccess("Category deleted successfully!");
                            } else {
                                ConsoleUI.printError("Failed to delete category.");
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
        return str != null && str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }
}