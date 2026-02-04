package com.revplay.service;

import com.revplay.dao.ArtistDAO;
import com.revplay.dao.PodcastDAO;
import com.revplay.model.Artist;
import com.revplay.model.Podcast;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PodcastService {
  private static final Logger logger = LogManager.getLogger(PodcastService.class);
  private final PodcastDAO podcastDAO;
  private final ArtistDAO artistDAO;

  public PodcastService() {
    this.podcastDAO = new PodcastDAO();
    this.artistDAO = new ArtistDAO();
  }

  public PodcastService(PodcastDAO podcastDAO, ArtistDAO artistDAO) {
    this.podcastDAO = podcastDAO;
    this.artistDAO = artistDAO;
  }

  public Podcast createPodcast(Podcast podcast) throws SQLException {
    logger.info("Creating podcast: {}", podcast.getTitle());

    Optional<Artist> artist = artistDAO.findById(podcast.getArtistId());
    if (artist.isEmpty()) {
      throw new IllegalArgumentException("Artist not found with ID: " + podcast.getArtistId());
    }

    int id = podcastDAO.create(podcast);
    podcast.setPodcastId(id);
    return podcast;
  }

  public List<Podcast> searchPodcasts(String keyword) throws SQLException {
    logger.info("Searching podcasts with keyword: {}", keyword);
    return podcastDAO.search(keyword);
  }

  public List<Podcast> getPodcastsByArtist(int artistId) throws SQLException {
    return podcastDAO.findByArtist(artistId);
  }

  public void incrementPlayCount(int podcastId) throws SQLException {
    podcastDAO.incrementPlayCount(podcastId);
  }

  public Optional<Podcast> getPodcastById(int podcastId) throws SQLException {
    return podcastDAO.findById(podcastId);
  }
}
