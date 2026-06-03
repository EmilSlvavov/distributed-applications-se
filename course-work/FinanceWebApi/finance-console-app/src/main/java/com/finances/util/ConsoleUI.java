package com.finances.util;

public class ConsoleUI {
    private static final String HEADER_BORDER = "╔════════════════════════════════════════════════════════════════╗";
    private static final String FOOTER_BORDER = "╚════════════════════════════════════════════════════════════════╝";
    private static final String MIDDLE_BORDER = "╠════════════════════════════════════════════════════════════════╣";
    private static final int MAX_WIDTH = 64;

    public static void printHeader(String title) {
        System.out.println(HEADER_BORDER);
        System.out.println("║ " + padCenter(title, MAX_WIDTH) + " ║");
        System.out.println(FOOTER_BORDER);
    }

    public static void printSubHeader(String title) {
        System.out.println("\n" + MIDDLE_BORDER);
        System.out.println("║ " + padLeft(title, MAX_WIDTH) + " ║");
        System.out.println(MIDDLE_BORDER);
    }

    public static void printWarning(String message) {
        System.out.println("\n⚠️ WARNING: " + message);
    }
    public static void printFooter() {
        System.out.println("\n" + FOOTER_BORDER);
    }

    public static void printMenu(String... options) {
        for (String option : options) {
            System.out.println(option);
        }
    }

    public static void printError(String message) {
        System.out.println("\n❌ ERROR: " + message);
    }

    public static void printSuccess(String message) {
        System.out.println("\n✓ SUCCESS: " + message);
    }

    public static void printInfo(String message) {
        System.out.println("\nℹ INFO: " + message);
    }

    public static void printLine() {
        System.out.println("─".repeat(66));
    }

    private static String padCenter(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        int totalPad = width - text.length();
        int leftPad = totalPad / 2;
        int rightPad = totalPad - leftPad;
        return " ".repeat(leftPad) + text + " ".repeat(rightPad);
    }

    private static String padLeft(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        return text + " ".repeat(width - text.length());
    }

    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    public static String readInput(String prompt) {
        System.out.print(prompt);
        return System.console() != null ? System.console().readLine() : new java.util.Scanner(System.in).nextLine();
    }

    public static String readPassword(String prompt) {
        System.out.print(prompt);
        if (System.console() != null) {
            char[] password = System.console().readPassword();
            return new String(password);
        } else {
            return new java.util.Scanner(System.in).nextLine();
        }
    }

    public static void pause() {
        System.out.println("\nPress Enter to continue...");
        try {
            System.in.read();
        } catch (Exception ignored) {
        }
    }
}
