package com.revplay.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

// Password security: SHA-256 hashing with random salts
public class PasswordUtil {
    private static final Logger logger = LogManager.getLogger(PasswordUtil.class);
    private static final int SALT_LENGTH = 16;
    private static final String HASH_ALGORITHM = "SHA-256";

    // Returns format: "base64_salt:base64_hash"
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            byte[] hash = hashWithSalt(plainPassword, salt);

            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);

            logger.debug("Password hashed successfully");
            return saltBase64 + ":" + hashBase64;

        } catch (NoSuchAlgorithmException e) {
            logger.error("Hashing algorithm not found", e);
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    // Uses constant-time comparison to prevent timing attacks
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }

        try {
            String[] parts = hashedPassword.split(":");
            if (parts.length != 2) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedHash = Base64.getDecoder().decode(parts[1]);

            byte[] computedHash = hashWithSalt(plainPassword, salt);

            boolean matches = MessageDigest.isEqual(storedHash, computedHash);

            if (!matches) {
                logger.warn("Password mismatch details:");
                logger.warn("Stored hash length: {}", storedHash.length);
                logger.warn("Computed hash length: {}", computedHash.length);
                logger.warn("Salt length: {}", salt.length);
                logger.warn("Stored hash (Base64): {}", parts[1]);
                logger.warn("Computed hash (Base64): {}", Base64.getEncoder().encodeToString(computedHash));
            }

            return matches;

        } catch (NoSuchAlgorithmException | IllegalArgumentException e) {
            logger.error("Password verification failed", e);
            return false;
        }
    }

    private static byte[] hashWithSalt(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        digest.update(salt);
        return digest.digest(password.getBytes(StandardCharsets.UTF_8));
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUpper = false, hasLower = false, hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c))
                hasUpper = true;
            if (Character.isLowerCase(c))
                hasLower = true;
            if (Character.isDigit(c))
                hasDigit = true;
        }
        return hasUpper && hasLower && hasDigit;
    }

    public static String getPasswordRequirements() {
        return "Password must be at least 8 characters with uppercase, lowercase, and a number";
    }
}
