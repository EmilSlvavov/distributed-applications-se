package com.finances.page;

import com.finances.exception.ApiException;
import com.finances.service.UserService;
import com.finances.util.ConsoleUI;

public class CreateUserPage {
    public static void show(UserService userService) {
        ConsoleUI.clearScreen();
        ConsoleUI.printHeader("CREATE NEW USER (ADMIN ONLY)");

        String username = ConsoleUI.readInput("\nEnter username: ");
        String password = ConsoleUI.readPassword("Enter password (min 8 characters): ");
        String confirmPassword = ConsoleUI.readPassword("Confirm password: ");

        if (!password.equals(confirmPassword)) {
            ConsoleUI.printError("Passwords do not match!");
            ConsoleUI.pause();
            return;
        }

        if (password.length() < 8) {
            ConsoleUI.printError("Password must be at least 8 characters long!");
            ConsoleUI.pause();
            return;
        }

        System.out.println("\nSelect role:");
        System.out.println("1. USER");
        System.out.println("2. ADMIN");
        String roleChoice = ConsoleUI.readInput("Enter choice (1 or 2): ");
        String role = roleChoice.equals("2") ? "ADMIN" : "USER";

        try {
            var user = userService.createUser(username, password, role);
            if (user != null) {
                ConsoleUI.printSuccess("User created successfully!");
                System.out.println("User ID: " + user.getId());
                System.out.println("Username: " + user.getUsername());
                System.out.println("Role: " + user.getRole());
            } else {
                ConsoleUI.printError("Failed to create user. Please try again.");
            }
        } catch (ApiException e) {
            ConsoleUI.printError(e.getDetail());
        }
        ConsoleUI.pause();
    }
}