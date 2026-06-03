package com.finances.page;

import com.finances.exception.ApiException;
import com.finances.service.UserService;
import com.finances.dto.response.UserResponse;
import com.finances.dto.response.PagedResponse;
import com.finances.util.ConsoleUI;
import java.util.List;

public class UserManagementPage {
    private static final int PAGE_SIZE = 5;

    public static void show(UserService userService) {
        int currentPage = 0;

        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader("USER MANAGEMENT");

            PagedResponse<UserResponse> response = userService.getAllUsers(currentPage, PAGE_SIZE);

            if (response == null || response.getContent() == null || response.getContent().isEmpty()) {
                ConsoleUI.printInfo("No users found.");
                System.out.println("\n1. Create New User");
                System.out.println("2. Back to Admin Menu");
                String choice = ConsoleUI.readInput("\nSelect option: ");
                if (choice.equals("1")) CreateUserPage.show(userService);
                else return;
                continue;
            }

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
                    user.getIsActive() != null && user.getIsActive() ? "Yes" : "No",
                    dateStr.substring(0, Math.min(19, dateStr.length()))));
            }

            ConsoleUI.printLine();
            int totalPages = response.getTotalPages() != null ? response.getTotalPages() : 1;
            long totalElements = response.getTotalElements() != null ? response.getTotalElements() : 0;

            System.out.println("\nPage " + (currentPage + 1) + " of " + totalPages);
            System.out.println("Total users: " + totalElements);

            System.out.println("\n1. Next Page");
            System.out.println("2. Previous Page");
            System.out.println("3. Create New User");
            System.out.println("4. Edit User");
            System.out.println("5. Delete User");
            System.out.println("6. Back to Admin Menu");
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
                    CreateUserPage.show(userService);
                    break;
                case "4":
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
                case "5":
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

        // Fetch current values so skipped fields keep their existing data
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

        // Use current values for any skipped fields
        String finalUsername = username.isEmpty() ? current.getUsername() : username;
        String finalRole     = role.isEmpty()     ? current.getRole().toString() : role.toUpperCase();
        // Password is only updated if provided — if skipped we still need to send something
        // so we send a placeholder the backend will re-encode (requires a known current password)
        if (password.isEmpty()) {
            ConsoleUI.printInfo("Password not changed.");
        }

        if (username.isEmpty() && password.isEmpty() && role.isEmpty()) {
            ConsoleUI.printInfo("No changes made.");
            ConsoleUI.pause();
            return;
        }

        try {
            if (userService.updateUser(userId, finalUsername,
                password.isEmpty() ? null : password, finalRole) != null) {
                ConsoleUI.printSuccess("User updated successfully!");
            } else {
                ConsoleUI.printError("Failed to update user.");
            }
        } catch (ApiException e) {
            ConsoleUI.printError(e.getDetail());
        }
        ConsoleUI.pause();
    }
}