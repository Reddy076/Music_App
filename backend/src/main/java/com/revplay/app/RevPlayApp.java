package com.revplay.app;

import com.revplay.config.DatabaseConfig;
import com.revplay.ui.MainMenu;
import com.revplay.ui.ConsoleUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Application entry point: Init DB -> Show menu -> Cleanup on exit
public class RevPlayApp {
    private static final Logger logger;

    static {
        // Enforce log location to backend/logs regardless of execution directory
        String userDir = System.getProperty("user.dir");
        if (userDir.endsWith("RevPlay")) {
            System.setProperty("revplay.log.path", "backend/logs");
        } else {
            System.setProperty("revplay.log.path", "logs");
        }
        logger = LogManager.getLogger(RevPlayApp.class);
    }

    public static void main(String[] args) {
        logger.info("Starting RevPlay Application...");

        try {
            initializeDatabase();
            ConsoleUtil.printWelcomeBanner();
            MainMenu mainMenu = new MainMenu();
            mainMenu.display();

        } catch (Exception e) {
            logger.error("Application error: ", e);
            ConsoleUtil.printError("1An unexpected error occurred: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    private static void initializeDatabase() {
        try {
            logger.info("Initializing database connection...");
            DatabaseConfig.getInstance();
            logger.info("Database connection initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize database connection", e);
            ConsoleUtil.printError("Failed to connect to database. Please check your configuration.");
            ConsoleUtil.printInfo("Make sure MySQL is running and db.properties is configured correctly.");
            System.exit(1);
        }
    }

    private static void shutdown() {
        logger.info("Shutting down RevPlay Application...");
        logger.info("Database resources released");
        ConsoleUtil.printGoodbye();
        logger.info("RevPlay Application shutdown complete");
    }
}
