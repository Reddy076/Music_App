package com.revplay.dao;

import com.revplay.model.ListeningHistory;
import com.revplay.model.Song;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.List;

public class HistoryDAO extends BaseDAO {

    public HistoryDAO() {
        super();
    }

    public int addToHistory(int userId, int songId, int durationPlayed) throws SQLException {
        String sql = "INSERT INTO listening_history (user_id, song_id, duration_played) VALUES (?, ?, ?)";
        return executeInsert(sql, userId, songId, durationPlayed);
    }

    public List<Song> getRecentlyPlayed(int userId, int limit) throws SQLException {
        String sql = "SELECT DISTINCT s.*, a.artist_name, h.played_at FROM songs s " +
                "JOIN artists a ON s.artist_id = a.artist_id " +
                "JOIN listening_history h ON s.song_id = h.song_id " +
                "WHERE h.user_id = ? ORDER BY h.played_at DESC LIMIT ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            return executeQueryForList(stmt, SongDAO::mapResultSetToSong);
        }
    }

    public List<ListeningHistory> getHistory(int userId, int limit) throws SQLException {
        String sql = "SELECT h.*, s.title, a.artist_name FROM listening_history h " +
                "JOIN songs s ON h.song_id = s.song_id " +
                "JOIN artists a ON s.artist_id = a.artist_id " +
                "WHERE h.user_id = ? ORDER BY h.played_at DESC LIMIT ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            return executeQueryForList(stmt, this::mapResultSetToHistory);
        }
    }

    public List<ListeningHistory> getHistoryByDateRange(int userId, Timestamp start, Timestamp end)
            throws SQLException {
        String sql = "SELECT h.*, s.title FROM listening_history h " +
                "JOIN songs s ON h.song_id = s.song_id " +
                "WHERE h.user_id = ? AND h.played_at BETWEEN ? AND ? ORDER BY h.played_at DESC";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setTimestamp(2, start);
            stmt.setTimestamp(3, end);
            return executeQueryForList(stmt, this::mapResultSetToHistory);
        }
    }

    public int getTotalListeningTime(int userId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(duration_played), 0) as total FROM listening_history WHERE user_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("total") : 0;
            }
        }
    }

    public boolean clearHistory(int userId) throws SQLException {
        String sql = "DELETE FROM listening_history WHERE user_id = ?";
        return executeUpdate(sql, userId) > 0;
    }

    private ListeningHistory mapResultSetToHistory(ResultSet rs) throws SQLException {
        ListeningHistory history = new ListeningHistory();
        history.setHistoryId(rs.getInt("history_id"));
        history.setUserId(rs.getInt("user_id"));
        history.setSongId(rs.getInt("song_id"));
        history.setPlayedAt(rs.getTimestamp("played_at"));
        history.setDurationPlayed(rs.getInt("duration_played"));
        return history;
    }

}
