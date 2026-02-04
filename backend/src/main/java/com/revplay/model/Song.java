package com.revplay.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Song extends AudioItem {
    private int songId;
    private Integer albumId;
    private Album album;

    public Song() {
        super();
    }

    public Song(String title, int artistId, String genre, int duration) {
        super(title, artistId, genre, duration);
    }

    public Song(int songId, String title, int artistId, Integer albumId, String genre,
            int duration, Date releaseDate, String filePath, String coverImage,
            int playCount, boolean isActive, Timestamp createdAt, Timestamp updatedAt) {
        this.songId = songId;
        this.title = title;
        this.artistId = artistId;
        this.albumId = albumId;
        this.genre = genre;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.filePath = filePath;
        this.coverImage = coverImage;
        this.playCount = playCount;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public void incrementPlayCount() {
        this.playCount++;
    }

    @Override
    public String toString() {
        return "Song{" +
                "songId=" + songId +
                ", title='" + title + '\'' +
                ", artistId=" + artistId +
                ", genre='" + genre + '\'' +
                ", duration=" + getFormattedDuration() +
                ", playCount=" + playCount +
                '}';
    }
}
