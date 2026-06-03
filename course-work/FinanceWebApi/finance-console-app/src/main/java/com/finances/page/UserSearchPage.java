package com.finances.page;

import com.finances.exception.ApiException;
import com.finances.service.UserService;
import com.finances.dto.response.UserResponse;
import com.finances.dto.response.PagedResponse;
import com.finances.util.ConsoleUI;
import java.util.List;

public class UserSearchPage {
    private static final int PAGE_SIZE = 5;

    public static void show(UserService userService) {
        ConsoleUI.clearScreen();
        ConsoleUI.printHeader("SEARCH USERS");

        System.out.println("\nEnter filters (press Enter to skip any):\n");

        String username = ConsoleUI.readInput("Username contains: ");
        if (username.isEmpty()) username = null;

        System.out.println("Role filter:");
        System.out.println("1. USER");
        System.out.println("2. ADMIN");
        System.out.println("3. All roles (skip)");
        String roleChoice = ConsoleUI.readInput("Enter choice (1-3): ");
        String role = switch (roleChoice) {
            case "1" -> "USER";
            case "2" -> "ADMIN";
            default  -> null;
        };

        System.out.println("\nActive status:");
        System.out.println("1. Active only");
        System.out.println("2. Inactive only");
        System.out.println("3. All (skip)");
        String activeChoice = ConsoleUI.readInput("Enter choice (1-3): ");
        Boolean isActive = switch (activeChoice) {
            case "1" -> Boolean.TRUE;
            case "2" -> Boolean.FALSE;
            default  -> null;
        };

        System.out.println("\nSort by:");
        System.out.println("1. Created date (default)");
        System.out.println("2. Username");
        System.out.println("3. Role");
        String sortChoice = ConsoleUI.readInput("Enter choice (1-3): ");
        String sortBy = switch (sortChoice) {
            case "2" -> "username";
            case "3" -> "role";
            default  -> "createdAt";
        };

        System.out.println("\nSort direction:");
        System.out.println("1. Descending (default)");
        System.out.println("2. Ascending");
        String sortDir = ConsoleUI.readInput("Enter choice (1-2): ").equals("2") ? "asc" : "desc";

        displayResults(userService, username, role, isActive, sortBy, sortDir, 0);
    }

    private static void displayResults(
        UserService userService,
        String username, String role, Boolean isActive,
        String sortBy, String sortDir, int currentPage) {

        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader("USER SEARCH RESULTS");

            PagedResponse<UserResponse> response;
            try {
                response = userService.searchUsers(username, role, isActive,
                    sortBy, sortDir, currentPage, PAGE_SIZE);
            } catch (ApiException e) {
                ConsoleUI.printError(e.getDetail());
                ConsoleUI.pause();
                return;
            }

            if (response == null || response.getContent() == null || response.getContent().isEmpty()) {
                ConsoleUI.printInfo("No users found matching your criteria.");
                System.out.println("\nPress Enter to go back...");
                ConsoleUI.readInput("");
                return;
            }

            // Print active filters summary
            System.out.println("\nFilters: "
                + (username != null ? "username~=" + username + " " : "")
                + (role != null ? "role=" + role + " " : "")
                + (isActive != null ? "active=" + isActive + " " : "")
                + "sortBy=" + sortBy + " sortDir=" + sortDir);

            List<UserResponse> users = response.getContent();

            System.out.println("\n" + String.format("%-4s %-20s %-10s %-8s %-20s",
                "ID", "Username", "Role", "Active", "Created At"));
            ConsoleUI.printLine();

            for (UserResponse user : users) {
                String dateStr = user.getCreatedAt() != null ? user.getCreatedAt().toString() : "N/A";
                System.out.println(String.format("%-4d %-20s %-10s %-8s %-20s",
                    user.getId(),
                    user.getUsername() != null ? user.getUsername() : "N/A",
                    user.getRole() != null ? user.getRole() : "N/A",
                    Boolean.TRUE.equals(user.getIsActive()) ? "Yes" : "No",
                    dateStr.substring(0, Math.min(19, dateStr.length()))));
            }

            ConsoleUI.printLine();
            int totalPages     = response.getTotalPages()    != null ? response.getTotalPages()    : 1;
            long totalElements = response.getTotalElements() != null ? response.getTotalElements() : 0;

            System.out.println("\nPage " + (currentPage + 1) + " of " + totalPages);
            System.out.println("Total results: " + totalElements);

            System.out.println("\n1. Next Page");
            System.out.println("2. Previous Page");
            System.out.println("3. Edit User");
            System.out.println("4. Delete User");
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
                    String editIdStr = ConsoleUI.readInput("Enter user ID to edit: ");
                    try {
                        Integer userId = Integer.parseInt(editIdStr);
                        try {
                            editUser(userService, userId);
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
                    String deleteIdStr = ConsoleUI.readInput("Enter user ID to delete: ");
                    try {
                        Integer userId = Integer.parseInt(deleteIdStr);
                        try {
                            if (userService.deleteUser(userId)) {
                                ConsoleUI.printSuccess("User deleted successfully!");
                            } else {
                                ConsoleUI.printError("Failed to delete user.");
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
                    show(userService);
                    return;
                case "6":
                    return;
                default:
                    ConsoleUI.printError("Invalid option.");
                    ConsoleUI.pause();
            }
        }
    }

    private static void editUser(UserService userService, Integer userId) {
        ConsoleUI.clearScreen();
        ConsoleUI.printHeader("EDIT USER");

        UserResponse current = userService.getUserById(userId);
        if (current == null) {
            ConsoleUI.printError("User not found.");
            ConsoleUI.pause();
            return;
        }

        System.out.println("Current username: " + current.getUsername());
        System.out.println("Current role:     " + current.getRole());
        System.out.println();

        String username = ConsoleUI.readInput("Enter new username (or press Enter to keep current): ");
        String password = ConsoleUI.readInput("Enter new password (or press Enter to keep current): ");
        String role     = ConsoleUI.readInput("Enter role (ADMIN/USER or press Enter to keep current): ");

        String finalUsername = username.isEmpty() ? current.getUsername() : username;
        String finalRole     = role.isEmpty() ? current.getRole().toString() : role.toUpperCase();

        if (password.isEmpty()) ConsoleUI.printInfo("Password not changed.");

        if (username.isEmpty() && password.isEmpty() && role.isEmpty()) {
            ConsoleUI.printInfo("No changes made.");
            ConsoleUI.pause();
            return;
        }

        if (userService.updateUser(userId, finalUsername,
            password.isEmpty() ? null : password, finalRole) != null) {
            ConsoleUI.printSuccess("User updated successfully!");
        } else {
            ConsoleUI.printError("Failed to update user.");
        }
        ConsoleUI.pause();
    }
}