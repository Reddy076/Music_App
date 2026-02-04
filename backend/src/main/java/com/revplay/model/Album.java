package com.revplay.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Album extends SongCollection {
    private int albumId;
    private int artistId;
    private String title;
    private String description;
    private String coverImage;
    private Date releaseDate;
    private String genre;

    private Artist artist;
    private String artistName;

    public Album() {
        super();
    }

    public Album(int artistId, String title, String genre) {
        this.artistId = artistId;
        this.title = title;
        this.genre = genre;

    }

    public Album(int albumId, int artistId, String title, String description,
            String coverImage, Date releaseDate, String genre,
            Timestamp createdAt, Timestamp updatedAt) {
        this.albumId = albumId;
        this.artistId = artistId;
        this.title = title;
        this.description = description;
        this.coverImage = coverImage;
        this.releaseDate = releaseDate;
        this.genre = genre;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    @Override
    public String toString() {
        return "Album{" +
                "albumId=" + albumId +
                ", title='" + title + '\'' +
                ", artistId=" + artistId +
                ", genre='" + genre + '\'' +
                ", releaseDate=" + releaseDate +
                ", songCount=" + getSongCount() +
                '}';
    }
}
