package com.finances.page;

import com.finances.exception.ApiException;
import com.finances.dto.response.UserResponse;
import com.finances.service.UserService;
import com.finances.util.ConsoleUI;

public class RegisterPage {
    public static void show(UserService userService) {
        ConsoleUI.clearScreen();
        ConsoleUI.printHeader("REGISTER NEW USER");

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

        try {
            UserResponse user = userService.createUser(username, password, "USER");
            if (user != null) {
                ConsoleUI.printSuccess("User registered successfully! Please login with your credentials.");
            } else {
                ConsoleUI.printError("Registration failed. Please try again.");
            }
        } catch (ApiException e) {
            ConsoleUI.printError(e.getDetail());
        }
        ConsoleUI.pause();
    }
}