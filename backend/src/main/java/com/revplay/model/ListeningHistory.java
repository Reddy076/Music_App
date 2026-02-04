package com.revplay.model;

import java.sql.Timestamp;

public class ListeningHistory {
    private int historyId;
    private int userId;
    private int songId;
    private Timestamp playedAt;
    private int durationPlayed;

    private Song song;
    private User user;

    public ListeningHistory() {
    }

    public ListeningHistory(int userId, int songId, int durationPlayed) {
        this.userId = userId;
        this.songId = songId;
        this.durationPlayed = durationPlayed;
    }

    public ListeningHistory(int historyId, int userId, int songId,
            Timestamp playedAt, int durationPlayed) {
        this.historyId = historyId;
        this.userId = userId;
        this.songId = songId;
        this.playedAt = playedAt;
        this.durationPlayed = durationPlayed;
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
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

    public Timestamp getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(Timestamp playedAt) {
        this.playedAt = playedAt;
    }

    public int getDurationPlayed() {
        return durationPlayed;
    }

    public void setDurationPlayed(int durationPlayed) {
        this.durationPlayed = durationPlayed;
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
        return "ListeningHistory{" +
                "historyId=" + historyId +
                ", userId=" + userId +
                ", songId=" + songId +
                ", playedAt=" + playedAt +
                ", durationPlayed=" + durationPlayed +
                '}';
    }
}
