package com.revplay.dao;

import com.revplay.model.Podcast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PodcastDAO extends BaseDAO {

  public PodcastDAO() {
    super();
  }

  public int create(Podcast podcast) throws SQLException {
    String sql = "INSERT INTO podcasts (title, artist_id, description, genre, duration, release_date, file_path, cover_image) "
        +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    return executeInsert(sql,
        podcast.getTitle(),
        podcast.getArtistId(),
        podcast.getDescription(),
        podcast.getGenre(),
        podcast.getDuration(),
        podcast.getReleaseDate(),
        podcast.getFilePath(),
        podcast.getCoverImage());
  }

  public Optional<Podcast> findById(int podcastId) throws SQLException {
    String sql = "SELECT p.*, a.artist_name FROM podcasts p " +
        "JOIN artists a ON p.artist_id = a.artist_id WHERE p.podcast_id = ? AND p.is_active = TRUE";

    try (Connection conn = dbConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, podcastId);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return Optional.of(mapResultSetToPodcast(rs));
        }
      }
    }
    return Optional.empty();
  }

  public List<Podcast> findAll() throws SQLException {
    String sql = "SELECT p.*, a.artist_name FROM podcasts p " +
        "JOIN artists a ON p.artist_id = a.artist_id " +
        "WHERE p.is_active = TRUE " +
        "ORDER BY p.created_at DESC";
    return executeQueryForList(sql, this::mapResultSetToPodcast);
  }

  public List<Podcast> findByArtist(int artistId) throws SQLException {
    String sql = "SELECT p.*, a.artist_name FROM podcasts p " +
        "JOIN artists a ON p.artist_id = a.artist_id " +
        "WHERE p.artist_id = ? AND p.is_active = TRUE ORDER BY p.release_date DESC";

    try (Connection conn = dbConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, artistId);
      return executeQueryForList(stmt, this::mapResultSetToPodcast);
    }
  }

  public List<Podcast> search(String keyword) throws SQLException {
    String sql = "SELECT p.*, a.artist_name FROM podcasts p " +
        "JOIN artists a ON p.artist_id = a.artist_id " +
        "WHERE (p.title LIKE ? OR a.artist_name LIKE ? OR p.genre LIKE ? OR p.description LIKE ?) " +
        "AND p.is_active = TRUE " +
        "ORDER BY p.play_count DESC LIMIT 50";

    try (Connection conn = dbConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      String searchPattern = "%" + keyword + "%";
      stmt.setString(1, searchPattern);
      stmt.setString(2, searchPattern);
      stmt.setString(3, searchPattern);
      stmt.setString(4, searchPattern);
      return executeQueryForList(stmt, this::mapResultSetToPodcast);
    }
  }

  public boolean update(Podcast podcast) throws SQLException {
    String sql = "UPDATE podcasts SET title = ?, description = ?, genre = ?, duration = ?, " +
        "release_date = ?, file_path = ?, cover_image = ? WHERE podcast_id = ?";
    return executeUpdate(sql,
        podcast.getTitle(),
        podcast.getDescription(),
        podcast.getGenre(),
        podcast.getDuration(),
        podcast.getReleaseDate(),
        podcast.getFilePath(),
        podcast.getCoverImage(),
        podcast.getPodcastId()) > 0;
  }

  public boolean incrementPlayCount(int podcastId) throws SQLException {
    String sql = "UPDATE podcasts SET play_count = play_count + 1 WHERE podcast_id = ?";
    return executeUpdate(sql, podcastId) > 0;
  }

  public boolean delete(int podcastId) throws SQLException {
    String sql = "UPDATE podcasts SET is_active = FALSE WHERE podcast_id = ?";
    return executeUpdate(sql, podcastId) > 0;
  }

  private Podcast mapResultSetToPodcast(ResultSet rs) throws SQLException {
    Podcast podcast = new Podcast();
    podcast.setPodcastId(rs.getInt("podcast_id"));
    podcast.setTitle(rs.getString("title"));
    podcast.setArtistId(rs.getInt("artist_id"));
    podcast.setDescription(rs.getString("description"));
    podcast.setGenre(rs.getString("genre"));
    podcast.setDuration(rs.getInt("duration"));
    podcast.setReleaseDate(rs.getDate("release_date"));
    podcast.setFilePath(rs.getString("file_path"));
    podcast.setCoverImage(rs.getString("cover_image"));
    podcast.setPlayCount(rs.getInt("play_count"));
    podcast.setActive(rs.getBoolean("is_active"));
    podcast.setCreatedAt(rs.getTimestamp("created_at"));
    try {
      podcast.setArtistName(rs.getString("artist_name"));
    } catch (SQLException ignored) {
    }
    return podcast;
  }
}
