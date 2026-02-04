package com.revplay.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Singleton: Manages database connections using .env configuration
public class DatabaseConfig {
    private static final Logger logger = LogManager.getLogger(DatabaseConfig.class);
    private static DatabaseConfig instance;

    private String url;
    private String username;
    private String password;
    private String driver;
    private boolean initialized = false;

    private DatabaseConfig() {
        loadConfiguration();
    }

    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    private void loadConfiguration() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            if (dotenv.get("DB_URL") == null && System.getenv("DB_URL") == null) {
                Dotenv backendDotenv = Dotenv.configure()
                        .directory("./backend")
                        .ignoreIfMissing()
                        .load();

                if (backendDotenv.get("DB_URL") != null) {
                    dotenv = backendDotenv;
                    logger.info("Loaded .env from ./backend directory");
                }
            }

            this.url = getValue(dotenv, "DB_URL");
            this.username = getValue(dotenv, "DB_USER");
            this.password = getValue(dotenv, "DB_PASSWORD");
            this.driver = getValue(dotenv, "DB_DRIVER");

            if (driver == null || driver.isEmpty()) {
                driver = "com.mysql.cj.jdbc.Driver";
            }

            if (url == null || url.isEmpty()) {
                throw new RuntimeException("Missing DB_URL configuration (check .env or environment variables)");
            }
            if (username == null || username.isEmpty()) {
                throw new RuntimeException("Missing DB_USER configuration (check .env or environment variables)");
            }
            if (password == null || password.isEmpty()) {
                throw new RuntimeException("Missing DB_PASSWORD configuration (check .env or environment variables)");
            }

            Class.forName(driver);

            initialized = true;
            logger.info("Database configuration loaded successfully");

        } catch (ClassNotFoundException e) {
            logger.error("JDBC Driver not found: {}", driver, e);
            throw new RuntimeException("JDBC Driver not found: " + driver, e);
        }
    }

    private String getValue(Dotenv dotenv, String key) {
        String value = System.getenv(key);
        if (value == null || value.isEmpty()) {
            value = dotenv.get(key);
        }
        return value;
    }

    public Connection getConnection() throws SQLException {
        if (!initialized) {
            throw new SQLException("Database configuration not initialized");
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            logger.debug("New database connection established");
            return connection;
        } catch (SQLException e) {
            logger.error("Failed to establish database connection", e);
            throw e;
        }
    }

    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            boolean valid = conn.isValid(5);
            if (valid) {
                logger.info("Database connection test successful");
            }
            return valid;
        } catch (SQLException e) {
            logger.error("Database connection test failed", e);
            return false;
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    logger.debug("Database connection closed");
                }
            } catch (SQLException e) {
                logger.warn("Error closing database connection", e);
            }
        }
    }

    public boolean isInitialized() {
        return initialized;
    }

    public String getUrl() {
        return url;
    }
}
