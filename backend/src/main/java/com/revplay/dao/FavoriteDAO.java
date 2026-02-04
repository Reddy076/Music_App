package com.revplay.dao;

import com.revplay.model.Song;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

public class FavoriteDAO extends BaseDAO {
    private static final Logger logger = LogManager.getLogger(FavoriteDAO.class);

    public FavoriteDAO() {
        super();
    }

    public boolean add(int userId, int songId) throws SQLException {
        String sql = "INSERT INTO favorites (user_id, song_id) VALUES (?, ?)";
        try {
            executeUpdate(sql, userId, songId);
            logger.info("Song {} added to favorites for user {}", songId, userId);
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            logger.warn("Song {} already in favorites for user {}", songId, userId);
            return false;
        }
    }

    public boolean remove(int userId, int songId) throws SQLException {
        String sql = "DELETE FROM favorites WHERE user_id = ? AND song_id = ?";
        return executeUpdate(sql, userId, songId) > 0;
    }

    public boolean isFavorite(int userId, int songId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM favorites WHERE user_id = ? AND song_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, songId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public List<Song> getFavoriteSongs(int userId) throws SQLException {
        String sql = "SELECT s.*, a.artist_name FROM songs s " +
                "JOIN artists a ON s.artist_id = a.artist_id " +
                "JOIN favorites f ON s.song_id = f.song_id " +
                "WHERE f.user_id = ? ORDER BY f.added_at DESC";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return executeQueryForList(stmt, SongDAO::mapResultSetToSong);
        }
    }

    public int getFavoriteCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM favorites WHERE user_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public List<Integer> getUsersWhoFavorited(int songId) throws SQLException {
        String sql = "SELECT user_id FROM favorites WHERE song_id = ?";
        List<Integer> userIds = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, songId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    userIds.add(rs.getInt("user_id"));
                }
            }
        }
        return userIds;
    }

    public List<java.util.Map<String, String>> getFavoritesForArtist(int artistId) throws SQLException {
        String sql = "SELECT f.user_id, u.username, s.title FROM favorites f " +
                "JOIN songs s ON f.song_id = s.song_id " +
                "JOIN users u ON f.user_id = u.user_id " +
                "WHERE s.artist_id = ? ORDER BY s.title, u.username";

        List<java.util.Map<String, String>> results = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, artistId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, String> row = new java.util.HashMap<>();
                    row.put("userId", String.valueOf(rs.getInt("user_id")));
                    row.put("username", rs.getString("username"));
                    row.put("songTitle", rs.getString("title"));
                    results.add(row);
                }
            }
        }
        return results;
    }

}
