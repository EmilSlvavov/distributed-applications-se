package com.finances.page;

import com.finances.exception.ApiException;
import com.finances.service.UserService;
import com.finances.util.ConsoleUI;

public class ChangePasswordPage {
    public static void show(UserService userService, Integer userId) {
        ConsoleUI.clearScreen();
        ConsoleUI.printHeader("CHANGE PASSWORD");

        String currentPassword = ConsoleUI.readInput("Enter current password: ");
        String newPassword = ConsoleUI.readInput("Enter new password: ");
        String confirmPassword = ConsoleUI.readInput("Confirm new password: ");

        if (!newPassword.equals(confirmPassword)) {
            ConsoleUI.printError("Passwords do not match.");
            ConsoleUI.pause();
            return;
        }

        if (newPassword.length() < 8) {
            ConsoleUI.printError("Password must be at least 8 characters long.");
            ConsoleUI.pause();
            return;
        }

        try {
            if (userService.changePassword(userId, currentPassword, newPassword)) {
                ConsoleUI.printSuccess("Password changed successfully!");
            } else {
                ConsoleUI.printError("Failed to change password.");
            }
        } catch (ApiException e) {
            ConsoleUI.printError(e.getDetail());
        }
        ConsoleUI.pause();
    }
}