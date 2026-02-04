package com.revplay.model;

import java.sql.Timestamp;

public class Favorite {
    private int favoriteId;
    private int userId;
    private int songId;
    private Timestamp addedAt;

    private Song song;
    private User user;

    public Favorite() {
    }

    public Favorite(int userId, int songId) {
        this.userId = userId;
        this.songId = songId;
    }

    public Favorite(int favoriteId, int userId, int songId, Timestamp addedAt) {
        this.favoriteId = favoriteId;
        this.userId = userId;
        this.songId = songId;
        this.addedAt = addedAt;
    }

    public int getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(int favoriteId) {
        this.favoriteId = favoriteId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public Timestamp getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Timestamp addedAt) {
        this.addedAt = addedAt;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Favorite{" +
                "favoriteId=" + favoriteId +
                ", userId=" + userId +
                ", songId=" + songId +
                ", addedAt=" + addedAt +
                '}';
    }
}
