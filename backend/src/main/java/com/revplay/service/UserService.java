package com.revplay.service;

import com.revplay.dao.UserDAO;
import com.revplay.dao.ArtistDAO;
import com.revplay.model.User;
import com.revplay.model.Artist;
import com.revplay.model.User.UserRole;
import com.revplay.util.PasswordUtil;
import com.revplay.util.ValidationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.Optional;

// Handles user authentication, registration, and password management
public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);
    private final UserDAO userDAO;
    private final ArtistDAO artistDAO;

    public UserService() {
        this(new UserDAO(), new ArtistDAO());
    }

    public UserService(UserDAO userDAO, ArtistDAO artistDAO) {
        this.userDAO = userDAO;
        this.artistDAO = artistDAO;
    }

    public User register(String email, String password, String username, UserRole role,
            String securityQuestion, String securityAnswer) throws Exception {
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (!ValidationUtil.isValidUsername(username)) {
            throw new IllegalArgumentException("Username must be 3-30 characters (letters, numbers, underscore)");
        }
        if (!PasswordUtil.isValidPassword(password)) {
            throw new IllegalArgumentException(PasswordUtil.getPasswordRequirements());
        }

        if (userDAO.emailExists(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (userDAO.usernameExists(username)) {
            throw new IllegalArgumentException("Username already taken");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(PasswordUtil.hashPassword(password));
        user.setUsername(username);
        user.setRole(role);
        user.setSecurityQuestion(securityQuestion);
        user.setSecurityAnswer(securityAnswer != null ? securityAnswer.toLowerCase() : null);

        int userId = userDAO.create(user);
        user.setUserId(userId);
        logger.info("User registered successfully: {}", email);
        return user;
    }

    public Artist registerArtist(String email, String password, String username,
            String artistName, String genre, String bio,
            String securityQuestion, String securityAnswer) throws Exception {
        User user = register(email, password, username, UserRole.ARTIST, securityQuestion, securityAnswer);

        Artist artist = new Artist();
        artist.setUserId(user.getUserId());
        artist.setArtistName(artistName);
        artist.setGenre(genre);
        artist.setBio(bio);

        int artistId = artistDAO.create(artist);
        artist.setArtistId(artistId);
        artist.setUser(user);

        logger.info("Artist registered successfully: {}", artistName);
        return artist;
    }

    public User login(String email, String password) throws Exception {
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        Optional<User> userOpt = userDAO.findByEmail(email);
        if (userOpt.isEmpty()) {
            logger.warn("Login attempt with non-existent email: {}", email);
            throw new IllegalArgumentException("Invalid email or password");
        }

        User user = userOpt.get();
        if (!user.isActive()) {
            throw new IllegalArgumentException("Account is deactivated");
        }

        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            logger.warn("Failed login attempt for: {}", email);
            throw new IllegalArgumentException("Invalid email or password");
        }

        logger.info("User logged in successfully: {}", email);
        return user;
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) throws Exception {
        Optional<User> userOpt = userDAO.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOpt.get();
        if (!PasswordUtil.verifyPassword(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        if (!PasswordUtil.isValidPassword(newPassword)) {
            throw new IllegalArgumentException(PasswordUtil.getPasswordRequirements());
        }

        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        return userDAO.updatePassword(userId, hashedPassword);
    }

    public boolean recoverPassword(String email, String securityAnswer, String newPassword) throws Exception {
        if (!userDAO.verifySecurityAnswer(email, securityAnswer)) {
            throw new IllegalArgumentException("Security answer is incorrect");
        }
        if (!PasswordUtil.isValidPassword(newPassword)) {
            throw new IllegalArgumentException(PasswordUtil.getPasswordRequirements());
        }

        Optional<User> userOpt = userDAO.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        return userDAO.updatePassword(userOpt.get().getUserId(), hashedPassword);
    }

    public String getPasswordHint(String email) throws SQLException {
        return userDAO.getPasswordHint(email).orElse("No hint available");
    }

    public Optional<User> getUserById(int userId) throws SQLException {
        return userDAO.findById(userId);
    }

    public Optional<User> getUserByEmail(String email) throws SQLException {
        return userDAO.findByEmail(email);
    }

    public boolean updateProfile(User user) throws SQLException {
        return userDAO.update(user);
    }

    public boolean deactivateAccount(int userId) throws SQLException {
        return userDAO.deactivate(userId);
    }
}
