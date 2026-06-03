package com.finances.service;

import java.io.*;
import java.nio.file.*;

/**
 * Handles session persistence - stores and retrieves auth token and user info
 */
public class SessionManager {
    private static final String SESSION_DIR = System.getProperty("user.home") + "/.finances";
    private static final String TOKEN_FILE = SESSION_DIR + "/token.dat";
    private static final String USER_FILE = SESSION_DIR + "/user.dat";

    static {
        try {
            Files.createDirectories(Paths.get(SESSION_DIR));
        } catch (IOException e) {
            System.err.println("Failed to create session directory: " + e.getMessage());
        }
    }

    public static void saveToken(String token) {
        try {
            Files.write(Paths.get(TOKEN_FILE), token.getBytes());
        } catch (IOException e) {
            System.err.println("Failed to save session token: " + e.getMessage());
        }
    }

    public static String loadToken() {
        try {
            if (Files.exists(Paths.get(TOKEN_FILE))) {
                return new String(Files.readAllBytes(Paths.get(TOKEN_FILE)));
            }
        } catch (IOException e) {
            System.err.println("Failed to load session token: " + e.getMessage());
        }
        return null;
    }

    public static void saveUserInfo(String username) {
        try {
            Files.write(Paths.get(USER_FILE), username.getBytes());
        } catch (IOException e) {
            System.err.println("Failed to save user info: " + e.getMessage());
        }
    }

    public static String loadUserInfo() {
        try {
            if (Files.exists(Paths.get(USER_FILE))) {
                return new String(Files.readAllBytes(Paths.get(USER_FILE)));
            }
        } catch (IOException e) {
            System.err.println("Failed to load user info: " + e.getMessage());
        }
        return null;
    }

    public static void clearSession() {
        try {
            Files.deleteIfExists(Paths.get(TOKEN_FILE));
            Files.deleteIfExists(Paths.get(USER_FILE));
        } catch (IOException e) {
            System.err.println("Failed to clear session: " + e.getMessage());
        }
    }

    public static boolean hasValidSession() {
        return loadToken() != null && !loadToken().isEmpty();
    }
}
