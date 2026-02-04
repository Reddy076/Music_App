package com.revplay.ui;

import com.revplay.model.Artist;
import com.revplay.model.User;
import com.revplay.model.User.UserRole;
import com.revplay.service.ArtistService;
import com.revplay.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class MainMenu extends BaseMenu {
    private static final Logger logger = LogManager.getLogger(MainMenu.class);
    private final UserService userService;
    private final ArtistService artistService;

    public MainMenu() {
        super();
        this.userService = new UserService();
        this.artistService = new ArtistService();
    }

    public void display() {
        boolean running = true;

        while (running) {
            if (session.isLoggedIn()) {
                running = showLoggedInMenu();
            } else {
                running = showGuestMenu();
            }
        }
    }

    private boolean showGuestMenu() {
        ConsoleUtil.printHeader("RevPlay - Welcome");

        String[] options = {
                "Login",
                "Register as Listener",
                "Register as Artist",
                "Recover Password",
                "Exit"
        };
        ConsoleUtil.printMenu(options);

        int choice = ConsoleUtil.readInt("Enter your choice: ", 1, 5);

        switch (choice) {
            case 1 -> handleLogin();
            case 2 -> handleRegisterListener();
            case 3 -> handleRegisterArtist();
            case 4 -> handlePasswordRecovery();
            case 5 -> {
                return false;
            }
        }
        return true;
    }

    private boolean showLoggedInMenu() {
        String welcomeMsg = "Welcome, " + session.getCurrentUsername();
        if (session.isArtist()) {
            welcomeMsg += " (Artist)";
        }
        ConsoleUtil.printHeader(welcomeMsg);

        String[] options;
        if (session.isArtist()) {
            options = new String[] {
                    "Browse & Search Music",
                    "My Playlists",
                    "My Favorites",
                    "Music Player",
                    "Listening History",
                    "Artist Dashboard",
                    "Account Settings",
                    "Logout",
                    "Exit"
            };
        } else {
            options = new String[] {
                    "Browse & Search Music",
                    "My Playlists",
                    "My Favorites",
                    "Music Player",
                    "Listening History",
                    "Account Settings",
                    "Logout",
                    "Exit"
            };
        }
        ConsoleUtil.printMenu(options);

        int maxChoice = session.isArtist() ? 9 : 8;
        int choice = ConsoleUtil.readInt("Enter your choice: ", 1, maxChoice);

        if (session.isArtist()) {
            switch (choice) {
                case 1 -> new SearchMenu().display();
                case 2 -> new PlaylistMenu().display();
                case 3 -> new FavoritesMenu().display();
                case 4 -> new PlayerMenu().display();
                case 5 -> new HistoryMenu().display();
                case 6 -> new ArtistMenu().display();
                case 7 -> showAccountSettings();
                case 8 -> handleLogout();
                case 9 -> {
                    handleLogout();
                    return false;
                }
            }
        } else {
            switch (choice) {
                case 1 -> new SearchMenu().display();
                case 2 -> new PlaylistMenu().display();
                case 3 -> new FavoritesMenu().display();
                case 4 -> new PlayerMenu().display();
                case 5 -> new HistoryMenu().display();
                case 6 -> showAccountSettings();
                case 7 -> handleLogout();
                case 8 -> {
                    handleLogout();
                    return false;
                }
            }
        }
        return true;
    }

    private void handleLogin() {
        ConsoleUtil.printSectionHeader("Login");

        String email = ConsoleUtil.readNonEmptyString("Email: ");
        String password = ConsoleUtil.readPassword("Password: ");

        try {
            User user = userService.login(email, password);
            session.login(user);

            if (user.isArtist()) {
                Optional<Artist> artistOpt = artistService.getArtistByUserId(user.getUserId());
                artistOpt.ifPresent(session::setArtistProfile);
            }

            logger.info("User logged in: {}", user.getEmail());
            ConsoleUtil.printSuccess("Login successful! Welcome back, " + user.getUsername() + "!");

        } catch (Exception e) {
            logger.warn("Login failed for email: {}", email);
            ConsoleUtil.printError(e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void handleRegisterListener() {
        ConsoleUtil.printSectionHeader("Register as Listener");

        String email = ConsoleUtil.readNonEmptyString("Email: ");
        String username = ConsoleUtil.readNonEmptyString("Username: ");
        String password = ConsoleUtil.readPassword("Password: ");
        String confirmPassword = ConsoleUtil.readPassword("Confirm Password: ");

        if (!password.equals(confirmPassword)) {
            ConsoleUtil.printError("Passwords do not match!");
            ConsoleUtil.pressEnterToContinue();
            return;
        }

        ConsoleUtil.printInfo("Security Question (for password recovery):");
        String[] questions = {
                "What is your pet's name?",
                "What city were you born in?",
                "What is your mother's maiden name?",
                "What was your first car?",
                "What is your favorite movie?"
        };
        ConsoleUtil.printMenu(questions);
        int questionChoice = ConsoleUtil.readInt("Select a question: ", 1, 5);
        String securityQuestion = questions[questionChoice - 1];
        String securityAnswer = ConsoleUtil.readNonEmptyString("Your answer: ");

        try {
            User user = userService.register(email, password, username, UserRole.USER,
                    securityQuestion, securityAnswer);
            logger.info("New listener registered: {}", user.getEmail());
            ConsoleUtil.printSuccess("Registration successful! You can now login.");

        } catch (Exception e) {
            logger.warn("Registration failed: {}", e.getMessage());
            ConsoleUtil.printError(e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void handleRegisterArtist() {
        ConsoleUtil.printSectionHeader("Register as Artist");

        String email = ConsoleUtil.readNonEmptyString("Email: ");
        String username = ConsoleUtil.readNonEmptyString("Username: ");
        String password = ConsoleUtil.readPassword("Password: ");
        String confirmPassword = ConsoleUtil.readPassword("Confirm Password: ");

        if (!password.equals(confirmPassword)) {
            ConsoleUtil.printError("Passwords do not match!");
            ConsoleUtil.pressEnterToContinue();
            return;
        }

        ConsoleUtil.printInfo("Artist Profile Information");
        String artistName = ConsoleUtil.readNonEmptyString("Artist/Band Name: ");
        String genre = ConsoleUtil.readNonEmptyString("Primary Genre: ");
        String bio = ConsoleUtil.readOptionalString("Bio (optional): ");

        ConsoleUtil.printInfo("Security Question (for password recovery):");
        String[] questions = {
                "What is your pet's name?",
                "What city were you born in?",
                "What is your mother's maiden name?",
                "What was your first car?",
                "What is your favorite movie?"
        };
        ConsoleUtil.printMenu(questions);
        int questionChoice = ConsoleUtil.readInt("Select a question: ", 1, 5);
        String securityQuestion = questions[questionChoice - 1];
        String securityAnswer = ConsoleUtil.readNonEmptyString("Your answer: ");

        try {
            Artist artist = userService.registerArtist(email, password, username,
                    artistName, genre, bio,
                    securityQuestion, securityAnswer);
            logger.info("New artist registered: {}", artist.getArtistName());
            ConsoleUtil.printSuccess("Artist registration successful! You can now login.");

        } catch (Exception e) {
            logger.warn("Artist registration failed: {}", e.getMessage());
            ConsoleUtil.printError(e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void handlePasswordRecovery() {
        ConsoleUtil.printSectionHeader("Password Recovery");

        String email = ConsoleUtil.readNonEmptyString("Enter your email: ");

        try {
            Optional<User> userOpt = userService.getUserByEmail(email);
            if (userOpt.isEmpty()) {
                ConsoleUtil.printError("Email not found.");
                ConsoleUtil.pressEnterToContinue();
                return;
            }

            User user = userOpt.get();
            if (user.getSecurityQuestion() == null) {
                String hint = userService.getPasswordHint(email);
                ConsoleUtil.printInfo("Password hint: " + hint);
                ConsoleUtil.pressEnterToContinue();
                return;
            }

            ConsoleUtil.printInfo("Security Question: " + user.getSecurityQuestion());
            String answer = ConsoleUtil.readNonEmptyString("Your answer: ");

            String newPassword = ConsoleUtil.readPassword("Enter new password: ");
            String confirmPassword = ConsoleUtil.readPassword("Confirm new password: ");

            if (!newPassword.equals(confirmPassword)) {
                ConsoleUtil.printError("Passwords do not match!");
                ConsoleUtil.pressEnterToContinue();
                return;
            }

            boolean success = userService.recoverPassword(email, answer, newPassword);
            if (success) {
                logger.info("Password recovered for: {}", email);
                ConsoleUtil.printSuccess("Password reset successful! You can now login with your new password.");
            } else {
                ConsoleUtil.printError("Password reset failed.");
            }

        } catch (Exception e) {
            logger.warn("Password recovery failed: {}", e.getMessage());
            ConsoleUtil.printError(e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void handleLogout() {
        String username = session.getCurrentUsername();
        session.logout();
        logger.info("User logged out: {}", username);
        ConsoleUtil.printSuccess("You have been logged out. Goodbye, " + username + "!");
        ConsoleUtil.pressEnterToContinue();
    }

    private void showAccountSettings() {
        ConsoleUtil.printSectionHeader("Account Settings");

        String[] options = {
                "Change Password",
                "View Profile",
                "Deactivate Account",
                "Back"
        };
        ConsoleUtil.printMenu(options);

        int choice = ConsoleUtil.readInt("Enter your choice: ", 1, 4);

        switch (choice) {
            case 1 -> handleChangePassword();
            case 2 -> viewProfile();
            case 3 -> handleDeactivateAccount();
            case 4 -> {
            }
        }
    }

    private void handleChangePassword() {
        ConsoleUtil.printSectionHeader("Change Password");

        String currentPassword = ConsoleUtil.readPassword("Current password: ");
        String newPassword = ConsoleUtil.readPassword("New password: ");
        String confirmPassword = ConsoleUtil.readPassword("Confirm new password: ");

        if (!newPassword.equals(confirmPassword)) {
            ConsoleUtil.printError("New passwords do not match!");
            ConsoleUtil.pressEnterToContinue();
            return;
        }

        try {
            boolean success = userService.changePassword(session.getCurrentUserId(),
                    currentPassword, newPassword);
            if (success) {
                logger.info("Password changed for user: {}", session.getCurrentUsername());
                ConsoleUtil.printSuccess("Password changed successfully!");
            } else {
                ConsoleUtil.printError("Failed to change password.");
            }
        } catch (Exception e) {
            ConsoleUtil.printError(e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void viewProfile() {
        ConsoleUtil.printSectionHeader("My Profile");

        User user = session.getCurrentUser();
        System.out.println("  Username: " + user.getUsername());
        System.out.println("  Email: " + user.getEmail());
        System.out.println("  Role: " + user.getRole());
        System.out.println("  Member since: " + user.getCreatedAt());

        if (session.isArtist() && session.getCurrentArtist() != null) {
            Artist artist = session.getCurrentArtist();
            System.out.println();
            System.out.println("  Artist Name: " + artist.getArtistName());
            System.out.println("  Genre: " + artist.getGenre());
            if (artist.getBio() != null && !artist.getBio().isEmpty()) {
                System.out.println("  Bio: " + artist.getBio());
            }
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void handleDeactivateAccount() {
        ConsoleUtil.printWarning("This will deactivate your account. You won't be able to login again.");

        if (ConsoleUtil.readConfirmation("Are you sure you want to deactivate your account?")) {
            try {
                boolean success = userService.deactivateAccount(session.getCurrentUserId());
                if (success) {
                    logger.info("Account deactivated: {}", session.getCurrentUsername());
                    ConsoleUtil.printSuccess("Account deactivated.");
                    session.logout();
                } else {
                    ConsoleUtil.printError("Failed to deactivate account.");
                }
            } catch (Exception e) {
                ConsoleUtil.printError(e.getMessage());
            }
        }

        ConsoleUtil.pressEnterToContinue();
    }
}
