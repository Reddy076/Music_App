package com.revplay.ui;

import java.util.Scanner;

public class ConsoleUtil {
    private static final Scanner scanner = new Scanner(System.in);

    public static void printHeader(String title) {
        System.out.println("\n" + title);
        System.out.println();
    }

    public static void printSectionHeader(String title) {
        System.out.println("\n" + title);
    }

    public static void printDivider() {
        System.out.println();
    }

    public static void printMenu(String[] options) {
        System.out.println();
        for (int i = 0; i < options.length; i++) {
            System.out.println("  " + (i + 1) + ". " + options[i]);
        }
        System.out.println();
    }

    public static void printMenu(String[] options, int startIndex) {
        System.out.println();
        for (int i = 0; i < options.length; i++) {
            System.out.println("  " + (startIndex + i) + ". " + options[i]);
        }
        System.out.println();
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                printError("Please enter a valid number.");
            }
        }
    }

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            printError("Please enter a number between " + min + " and " + max + ".");
        }
    }

    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static String readNonEmptyString(String prompt) {
        while (true) {
            String input = readString(prompt);
            if (!input.isEmpty()) {
                return input;
            }
            printError("This field cannot be empty.");
        }
    }

    public static String readOptionalString(String prompt) {
        return readString(prompt);
    }

    public static String readPassword(String prompt) {
        if (System.console() != null) {
            char[] password = System.console().readPassword(prompt);
            return new String(password);
        } else {
            return readNonEmptyString(prompt);
        }
    }

    public static boolean readConfirmation(String prompt) {
        while (true) {
            String input = readString(prompt + " (y/n): ").toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            }
            printError("Please enter 'y' for yes or 'n' for no.");
        }
    }

    public static void printSuccess(String message) {
        System.out.println(message);
    }

    public static void printError(String message) {
        System.out.println("Error: " + message);
    }

    public static void printWarning(String message) {
        System.out.println("Warning: " + message);
    }

    public static void printInfo(String message) {
        System.out.println(message);
    }

    public static void pressEnterToContinue() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public static String centerText(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
    }

    public static String formatDuration(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }

    public static String formatLongDuration(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, secs);
        }
        return String.format("%d:%02d", minutes, secs);
    }

    public static void printNumberedList(java.util.List<?> items) {
        if (items.isEmpty()) {
            printInfo("No items to display.");
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + items.get(i));
        }
    }

    public static void printWelcomeBanner() {
        System.out.println("\nRevPlay - Music Streaming Console Application\n");
    }

    public static void printGoodbye() {
        System.out.println("\nThank you for using RevPlay! See you next time!\n");
    }

    public static String icon(String unicode, String ascii) {
        return ascii;
    }

    public static String progressBar(int current, int total, int width) {
        int percent = (total > 0) ? (current * 100 / total) : 0;
        return current + "/" + total + " (" + percent + "%)";
    }

    public static void close() {
        scanner.close();
    }
}
