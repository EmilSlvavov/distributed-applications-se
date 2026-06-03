package com.finances.page;

import com.finances.exception.ApiException;
import com.finances.service.AuthService;
import com.finances.util.ConsoleUI;

public class LoginPage {
    public static void show(AuthService authService) {
        ConsoleUI.clearScreen();
        ConsoleUI.printHeader("LOGIN");

        String username = ConsoleUI.readInput("\nEnter username: ");
        String password = ConsoleUI.readPassword("Enter password: ");

        try {
            if (authService.login(username, password)) {
                ConsoleUI.printSuccess("Login successful!");
            } else {
                ConsoleUI.printError("Login failed. Invalid credentials.");
            }
        } catch (ApiException e) {
            ConsoleUI.printError(e.getDetail());
        }
        ConsoleUI.pause();
    }
}
