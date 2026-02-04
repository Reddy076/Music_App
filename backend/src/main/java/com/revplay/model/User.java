package com.revplay.model;

import java.sql.Timestamp;

public class User extends BaseModel {
    private int userId;
    private String email;
    private String password;
    private String username;
    private UserRole role;
    private String securityQuestion;
    private String securityAnswer;
    private String passwordHint;
    private boolean isActive;

    public enum UserRole {
        USER, ARTIST
    }

    public User() {
        this.role = UserRole.USER;
        this.isActive = true;
    }

    public User(String email, String password, String username, UserRole role) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = role;
        this.isActive = true;
    }

    public User(int userId, String email, String password, String username, UserRole role,
            String securityQuestion, String securityAnswer, String passwordHint,
            Timestamp createdAt, Timestamp updatedAt, boolean isActive) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = role;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.passwordHint = passwordHint;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isActive = isActive;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public String getPasswordHint() {
        return passwordHint;
    }

    public void setPasswordHint(String passwordHint) {
        this.passwordHint = passwordHint;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isArtist() {
        return this.role == UserRole.ARTIST;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}
