package com.finances.page;

import com.finances.service.AuthService;
import com.finances.util.ConsoleUI;

public class LoginPage {
    public static void show(AuthService authService) {
        ConsoleUI.clearScreen();
        ConsoleUI.printHeader("LOGIN");

        String username = ConsoleUI.readInput("\nEnter username: ");
        String password = ConsoleUI.readPassword("Enter password: ");

        if (authService.login(username, password)) {
            ConsoleUI.printSuccess("Login successful!");
            ConsoleUI.pause();
        } else {
            ConsoleUI.printError("Login failed. Invalid credentials.");
            ConsoleUI.pause();
        }
    }
}
