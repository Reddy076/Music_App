package com.revplay.model;

import java.sql.Date;

public abstract class AudioItem extends BaseModel {
  protected String title;
  protected int artistId;
  protected String genre;
  protected int duration;
  protected Date releaseDate;
  protected String filePath;
  protected String coverImage;
  protected int playCount;
  protected boolean isActive;

  protected Artist artist;
  protected String artistName;

  protected AudioItem() {
    this.playCount = 0;
    this.isActive = true;
  }

  protected AudioItem(String title, int artistId, String genre, int duration) {
    this();
    this.title = title;
    this.artistId = artistId;
    this.genre = genre;
    this.duration = duration;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getArtistId() {
    return artistId;
  }

  public void setArtistId(int artistId) {
    this.artistId = artistId;
  }

  public String getGenre() {
    return genre;
  }

  public void setGenre(String genre) {
    this.genre = genre;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public Date getReleaseDate() {
    return releaseDate;
  }

  public void setReleaseDate(Date releaseDate) {
    this.releaseDate = releaseDate;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public String getCoverImage() {
    return coverImage;
  }

  public void setCoverImage(String coverImage) {
    this.coverImage = coverImage;
  }

  public int getPlayCount() {
    return playCount;
  }

  public void setPlayCount(int playCount) {
    this.playCount = playCount;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
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

  public String getFormattedDuration() {
    int minutes = duration / 60;
    int seconds = duration % 60;
    return String.format("%d:%02d", minutes, seconds);
  }
}
