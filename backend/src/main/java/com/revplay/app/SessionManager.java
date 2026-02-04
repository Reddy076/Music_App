package com.revplay.app;

import com.revplay.model.Artist;
import com.revplay.model.User;

// Singleton: Holds current user session state (logged-in user and artist profile)
public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private Artist currentArtist;

    private SessionManager() {
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(User user) {
        this.currentUser = user;
    }

    public void setArtistProfile(Artist artist) {
        this.currentArtist = artist;
    }

    public void logout() {
        this.currentUser = null;
        this.currentArtist = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : -1;
    }

    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : "Guest";
    }

    public boolean isArtist() {
        return currentUser != null && currentUser.isArtist();
    }

    public Artist getCurrentArtist() {
        return currentArtist;
    }

    public int getCurrentArtistId() {
        return currentArtist != null ? currentArtist.getArtistId() : -1;
    }
}
