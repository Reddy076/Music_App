package com.revplay.model;

import java.sql.Timestamp;

public class Artist extends BaseModel {
    private int artistId;
    private int userId;
    private String artistName;
    private String bio;
    private String genre;
    private String profileImage;
    private String facebookLink;
    private String twitterLink;
    private String instagramLink;
    private String spotifyLink;

    private User user;

    public Artist() {
    }

    public Artist(int userId, String artistName, String genre) {
        this.userId = userId;
        this.artistName = artistName;
        this.genre = genre;
    }

    public Artist(int artistId, int userId, String artistName, String bio, String genre,
            String profileImage, String facebookLink, String twitterLink,
            String instagramLink, String spotifyLink, Timestamp createdAt, Timestamp updatedAt) {
        this.artistId = artistId;
        this.userId = userId;
        this.artistName = artistName;
        this.bio = bio;
        this.genre = genre;
        this.profileImage = profileImage;
        this.facebookLink = facebookLink;
        this.twitterLink = twitterLink;
        this.instagramLink = instagramLink;
        this.spotifyLink = spotifyLink;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public String getTwitterLink() {
        return twitterLink;
    }

    public void setTwitterLink(String twitterLink) {
        this.twitterLink = twitterLink;
    }

    public String getInstagramLink() {
        return instagramLink;
    }

    public void setInstagramLink(String instagramLink) {
        this.instagramLink = instagramLink;
    }

    public String getSpotifyLink() {
        return spotifyLink;
    }

    public void setSpotifyLink(String spotifyLink) {
        this.spotifyLink = spotifyLink;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "artistId=" + artistId +
                ", artistName='" + artistName + '\'' +
                ", genre='" + genre + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
