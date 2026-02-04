package com.revplay.model;

import java.sql.Timestamp;

public class Playlist extends SongCollection {
    private int playlistId;
    private int userId;
    private String name;
    private String description;
    private String coverImage;
    private boolean isPublic;

    private User user;
    private String username;

    public Playlist() {
        super();
        this.isPublic = false;
    }

    public Playlist(int userId, String name, boolean isPublic) {
        this.userId = userId;
        this.name = name;
        this.isPublic = isPublic;

    }

    public Playlist(int playlistId, int userId, String name, String description,
            String coverImage, boolean isPublic, Timestamp createdAt, Timestamp updatedAt) {
        this.playlistId = playlistId;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.coverImage = coverImage;
        this.isPublic = isPublic;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void removeSong(Song song) {
        this.songs.removeIf(s -> s.getSongId() == song.getSongId());
    }

    public boolean containsSong(int songId) {
        return songs.stream().anyMatch(s -> s.getSongId() == songId);
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "playlistId=" + playlistId +
                ", name='" + name + '\'' +
                ", userId=" + userId +
                ", isPublic=" + isPublic +
                ", songCount=" + getSongCount() +
                '}';
    }
}
