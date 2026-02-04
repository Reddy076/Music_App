package com.revplay.dao;

import com.revplay.model.User;
import com.revplay.model.User.UserRole;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.Optional;

// User table CRUD operations and authentication queries
public class UserDAO extends BaseDAO {
    private static final Logger logger = LogManager.getLogger(UserDAO.class);

    public UserDAO() {
        super();
    }

    public int create(User user) throws SQLException {
        String sql = "INSERT INTO users (email, password, username, role, security_question, security_answer, password_hint) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        return executeInsert(sql,
                user.getEmail(),
                user.getPassword(),
                user.getUsername(),
                user.getRole().name(),
                user.getSecurityQuestion(),
                user.getSecurityAnswer(),
                user.getPasswordHint());
    }

    public Optional<User> findById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by ID {}: {}", userId, e.getMessage());
            throw e;
        }
        return Optional.empty();
    }

    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by email {}: {}", email, e.getMessage());
            throw e;
        }
        return Optional.empty();
    }

    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by username {}: {}", username, e.getMessage());
            throw e;
        }
        return Optional.empty();
    }

    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users WHERE is_active = TRUE ORDER BY created_at DESC";
        return executeQueryForList(sql, this::mapResultSetToUser);
    }

    public List<User> findByRole(UserRole role) throws SQLException {
        String sql = "SELECT * FROM users WHERE role = ? AND is_active = TRUE ORDER BY created_at DESC";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role.name());
            return executeQueryForList(stmt, this::mapResultSetToUser);
        }
    }

    public boolean update(User user) throws SQLException {
        String sql = "UPDATE users SET email = ?, username = ?, role = ?, " +
                "security_question = ?, security_answer = ?, password_hint = ? " +
                "WHERE user_id = ?";
        return executeUpdate(sql,
                user.getEmail(),
                user.getUsername(),
                user.getRole().name(),
                user.getSecurityQuestion(),
                user.getSecurityAnswer(),
                user.getPasswordHint(),
                user.getUserId()) > 0;
    }

    public boolean updatePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        return executeUpdate(sql, newPassword, userId) > 0;
    }

    public boolean deactivate(int userId) throws SQLException {
        String sql = "UPDATE users SET is_active = FALSE WHERE user_id = ?";
        return executeUpdate(sql, userId) > 0;
    }

    public boolean delete(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        return executeUpdate(sql, userId) > 0;
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking email existence: {}", e.getMessage());
            throw e;
        }
        return false;
    }

    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking username existence: {}", e.getMessage());
            throw e;
        }
        return false;
    }

    public boolean verifySecurityAnswer(String email, String answer) throws SQLException {
        String sql = "SELECT security_answer FROM users WHERE email = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedAnswer = rs.getString("security_answer");
                    return storedAnswer != null && storedAnswer.equalsIgnoreCase(answer);
                }
            }
        } catch (SQLException e) {
            logger.error("Error verifying security answer: {}", e.getMessage());
            throw e;
        }
        return false;
    }

    public Optional<String> getPasswordHint(String email) throws SQLException {
        String sql = "SELECT password_hint FROM users WHERE email = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rs.getString("password_hint"));
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting password hint: {}", e.getMessage());
            throw e;
        }
        return Optional.empty();
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setUsername(rs.getString("username"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setSecurityQuestion(rs.getString("security_question"));
        user.setSecurityAnswer(rs.getString("security_answer"));
        user.setPasswordHint(rs.getString("password_hint"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        user.setActive(rs.getBoolean("is_active"));
        return user;
    }
}
