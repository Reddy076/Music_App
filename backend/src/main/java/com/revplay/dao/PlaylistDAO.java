package com.revplay.dao;

import com.revplay.model.Playlist;
import com.revplay.model.Song;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlaylistDAO extends BaseDAO {

    public PlaylistDAO() {
        super();
    }

    public int create(Playlist playlist) throws SQLException {
        String sql = "INSERT INTO playlists (user_id, name, description, cover_image, is_public) " +
                "VALUES (?, ?, ?, ?, ?)";
        return executeInsert(sql,
                playlist.getUserId(),
                playlist.getName(),
                playlist.getDescription(),
                playlist.getCoverImage(),
                playlist.isPublic());
    }

    public Optional<Playlist> findById(int playlistId) throws SQLException {
        String sql = "SELECT p.*, u.username, COUNT(ps.song_id) as song_count " +
                "FROM playlists p JOIN users u ON p.user_id = u.user_id " +
                "LEFT JOIN playlist_songs ps ON p.playlist_id = ps.playlist_id " +
                "WHERE p.playlist_id = ? GROUP BY p.playlist_id";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, playlistId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPlaylist(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Playlist> findByUser(int userId) throws SQLException {
        String sql = "SELECT p.*, u.username, COUNT(ps.song_id) as song_count " +
                "FROM playlists p JOIN users u ON p.user_id = u.user_id " +
                "LEFT JOIN playlist_songs ps ON p.playlist_id = ps.playlist_id " +
                "WHERE p.user_id = ? GROUP BY p.playlist_id ORDER BY p.created_at DESC";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return executeQueryForList(stmt, this::mapResultSetToPlaylist);
        }
    }

    public List<Playlist> findPublicPlaylists() throws SQLException {
        String sql = "SELECT p.*, u.username, COUNT(ps.song_id) as song_count " +
                "FROM playlists p JOIN users u ON p.user_id = u.user_id " +
                "LEFT JOIN playlist_songs ps ON p.playlist_id = ps.playlist_id " +
                "WHERE p.is_public = TRUE GROUP BY p.playlist_id ORDER BY p.created_at DESC";
        return executeQueryForList(sql, this::mapResultSetToPlaylist);
    }

    public List<Playlist> search(String keyword) throws SQLException {
        String sql = "SELECT p.*, u.username, COUNT(ps.song_id) as song_count " +
                "FROM playlists p JOIN users u ON p.user_id = u.user_id " +
                "LEFT JOIN playlist_songs ps ON p.playlist_id = ps.playlist_id " +
                "WHERE p.is_public = TRUE AND (p.name LIKE ? OR p.description LIKE ?) " +
                "GROUP BY p.playlist_id ORDER BY p.created_at DESC";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            return executeQueryForList(stmt, this::mapResultSetToPlaylist);
        }
    }

    public boolean update(Playlist playlist) throws SQLException {
        String sql = "UPDATE playlists SET name = ?, description = ?, cover_image = ?, is_public = ? " +
                "WHERE playlist_id = ?";
        return executeUpdate(sql,
                playlist.getName(),
                playlist.getDescription(),
                playlist.getCoverImage(),
                playlist.isPublic(),
                playlist.getPlaylistId()) > 0;
    }

    public boolean delete(int playlistId) throws SQLException {
        String sql = "DELETE FROM playlists WHERE playlist_id = ?";
        return executeUpdate(sql, playlistId) > 0;
    }

    public boolean addSong(int playlistId, int songId) throws SQLException {
        String sql = "INSERT INTO playlist_songs (playlist_id, song_id, position) " +
                "SELECT ?, ?, COALESCE(MAX(position), 0) + 1 FROM playlist_songs WHERE playlist_id = ?";
        return executeUpdate(sql, playlistId, songId, playlistId) > 0;
    }

    public boolean removeSong(int playlistId, int songId) throws SQLException {
        String sql = "DELETE FROM playlist_songs WHERE playlist_id = ? AND song_id = ?";
        return executeUpdate(sql, playlistId, songId) > 0;
    }

    public boolean containsSong(int playlistId, int songId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM playlist_songs WHERE playlist_id = ? AND song_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, playlistId);
            stmt.setInt(2, songId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public List<Song> getSongs(int playlistId) throws SQLException {
        String sql = "SELECT s.*, a.artist_name FROM songs s " +
                "JOIN artists a ON s.artist_id = a.artist_id " +
                "JOIN playlist_songs ps ON s.song_id = ps.song_id " +
                "WHERE ps.playlist_id = ? ORDER BY ps.position";

        List<Song> songs = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, playlistId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    songs.add(SongDAO.mapResultSetToSong(rs));
                }
            }
        }
        return songs;
    }

    private Playlist mapResultSetToPlaylist(ResultSet rs) throws SQLException {
        Playlist playlist = new Playlist();
        playlist.setPlaylistId(rs.getInt("playlist_id"));
        playlist.setUserId(rs.getInt("user_id"));
        playlist.setName(rs.getString("name"));
        playlist.setDescription(rs.getString("description"));
        playlist.setCoverImage(rs.getString("cover_image"));
        playlist.setPublic(rs.getBoolean("is_public"));
        playlist.setCreatedAt(rs.getTimestamp("created_at"));
        playlist.setUpdatedAt(rs.getTimestamp("updated_at"));
        try {
            playlist.setUsername(rs.getString("username"));
            playlist.setSongCount(rs.getInt("song_count"));
        } catch (SQLException ignored) {
        }
        return playlist;
    }

}
