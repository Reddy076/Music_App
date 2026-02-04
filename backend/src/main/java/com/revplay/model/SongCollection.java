package com.revplay.model;

import java.util.ArrayList;
import java.util.List;

public abstract class SongCollection extends BaseModel {
  protected List<Song> songs;
  protected int songCount;

  protected SongCollection() {
    this.songs = new ArrayList<>();
  }

  public List<Song> getSongs() {
    return songs;
  }

  public void setSongs(List<Song> songs) {
    this.songs = songs;
  }

  public void addSong(Song song) {
    this.songs.add(song);
  }

  public int getSongCount() {
    return songCount > 0 ? songCount : songs.size();
  }

  public void setSongCount(int songCount) {
    this.songCount = songCount;
  }

  public int getTotalDuration() {
    return songs.stream().mapToInt(Song::getDuration).sum();
  }

  public String getFormattedTotalDuration() {
    int totalSeconds = getTotalDuration();
    int hours = totalSeconds / 3600;
    int minutes = (totalSeconds % 3600) / 60;
    int seconds = totalSeconds % 60;

    if (hours > 0) {
      return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }
    return String.format("%d:%02d", minutes, seconds);
  }
}
