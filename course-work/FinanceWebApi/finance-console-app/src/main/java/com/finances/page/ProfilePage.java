package com.finances.page;

import com.finances.exception.ApiException;
import com.finances.dto.response.UserResponse;
import com.finances.util.ConsoleUI;
import com.finances.service.UserService;

public class ProfilePage {

    public static void show(UserService userService) {
        ConsoleUI.clearScreen();
        ConsoleUI.printHeader("USER PROFILE");

        UserResponse currentUser;
        try {
            currentUser = userService.getCurrentUser();
        } catch (ApiException e) {
            ConsoleUI.printError(e.getDetail());
            ConsoleUI.pause();
            return;
        }

        if (currentUser == null) {
            ConsoleUI.printError("Failed to load profile. Please try again.");
            ConsoleUI.pause();
            return;
        }

        System.out.println("\nProfile information:");
        System.out.println("- ID:             " + currentUser.getId());
        System.out.println("- Username:       " + currentUser.getUsername());
        System.out.println("- Role:           " + currentUser.getRole());
        System.out.println("- Account Status: " + (Boolean.TRUE.equals(currentUser.getIsActive()) ? "Active" : "Inactive"));
        System.out.println("- Member since:   " + (currentUser.getCreatedAt() != null
            ? currentUser.getCreatedAt().toLocalDate() : "N/A"));

        System.out.println("\n1. Change Password");
        System.out.println("2. Back to Menu");

        String choice = ConsoleUI.readInput("\nSelect option: ");

        switch (choice) {
            case "1":
                handleChangePassword(userService, currentUser.getId());
                break;
            case "2":
                return;
            default:
                ConsoleUI.printError("Invalid option.");
                ConsoleUI.pause();
        }
    }

    private static void handleChangePassword(UserService userService, Integer userId) {
        ConsoleUI.clearScreen();
        ConsoleUI.printHeader("CHANGE PASSWORD");

        String currentPassword = ConsoleUI.readInput("Enter current password: ");
        String newPassword     = ConsoleUI.readInput("Enter new password: ");
        String confirmPassword = ConsoleUI.readInput("Confirm new password: ");

        if (!newPassword.equals(confirmPassword)) {
            ConsoleUI.printError("New passwords do not match.");
            ConsoleUI.pause();
            return;
        }

        if (newPassword.length() < 8) {
            ConsoleUI.printError("New password must be at least 8 characters.");
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