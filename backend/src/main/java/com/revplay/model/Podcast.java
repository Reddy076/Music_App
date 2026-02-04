package com.revplay.model;

public class Podcast extends AudioItem {
    private int podcastId;
    private String description;

    public Podcast() {
        super();
    }

    public Podcast(String title, int artistId, String genre, int duration) {
        super(title, artistId, genre, duration);
    }

    public int getPodcastId() {
        return podcastId;
    }

    public void setPodcastId(int podcastId) {
        this.podcastId = podcastId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Podcast{" +
                "podcastId=" + podcastId +
                ", title='" + title + '\'' +
                ", artistId=" + artistId +
                ", duration=" + getFormattedDuration() +
                '}';
    }
}
