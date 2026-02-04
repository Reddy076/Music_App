package com.revplay.dao;

import com.revplay.model.Song;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Song table CRUD operations and search queries
public class SongDAO extends BaseDAO {

    public SongDAO() {
        super();
    }

    public int create(Song song) throws SQLException {
        String sql = "INSERT INTO songs (title, artist_id, album_id, genre, duration, release_date, file_path, cover_image) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return executeInsert(sql,
                song.getTitle(),
                song.getArtistId(),
                song.getAlbumId(),
                song.getGenre(),
                song.getDuration(),
                song.getReleaseDate(),
                song.getFilePath(),
                song.getCoverImage());
    }

    public Optional<Song> findById(int songId) throws SQLException {
        String sql = "SELECT s.*, a.artist_name FROM songs s " +
                "JOIN artists a ON s.artist_id = a.artist_id WHERE s.song_id = ? AND s.is_active = TRUE";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, songId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSong(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Song> findAll() throws SQLException {
        String sql = "SELECT s.*, a.artist_name FROM songs s " +
                "JOIN artists a ON s.artist_id = a.artist_id " +
                "WHERE s.is_active = TRUE " +
                "ORDER BY s.created_at DESC";
        return executeQueryForList(sql, SongDAO::mapResultSetToSong);
    }

    public List<Song> findByArtist(int artistId) throws SQLException {
        String sql = "SELECT s.*, a.artist_name FROM songs s " +
                "JOIN artists a ON s.artist_id = a.artist_id " +
                "WHERE s.artist_id = ? AND s.is_active = TRUE ORDER BY s.release_date DESC";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, artistId);
            return executeQueryForList(stmt, SongDAO::mapResultSetToSong);
        }
    }

    public List<Song> findByAlbum(int albumId) throws SQLException {
        String sql = "SELECT s.*, a.artist_name FROM songs s " +
                "JOIN artists a ON s.artist_id = a.artist_id " +
                "WHERE s.album_id = ? AND s.is_active = TRUE ORDER BY s.song_id";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, albumId);
            return executeQueryForList(stmt, SongDAO::mapResultSetToSong);
        }
    }

    public List<Song> findByGenre(String genre) throws SQLException {
        String sql = "SELECT s.*, a.artist_name FROM songs s " +
                "JOIN artists a ON s.artist_id = a.artist_id " +
                "WHERE s.genre = ? AND s.is_active = TRUE ORDER BY s.play_count DESC";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, genre);
            return executeQueryForList(stmt, SongDAO::mapResultSetToSong);
        }
    }

    public List<Song> search(String keyword) throws SQLException {
        String sql = "SELECT s.*, a.artist_name FROM songs s " +
                "JOIN artists a ON s.artist_id = a.artist_id " +
                "WHERE (s.title LIKE ? OR a.artist_name LIKE ? OR s.genre LIKE ?) " +
                "AND s.is_active = TRUE " +
                "ORDER BY s.play_count DESC LIMIT 50";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            return executeQueryForList(stmt, SongDAO::mapResultSetToSong);
        }
    }

    public List<Song> getPopularSongs(int limit) throws SQLException {
        String sql = "SELECT s.*, a.artist_name FROM songs s " +
                "JOIN artists a ON s.artist_id = a.artist_id " +
                "WHERE s.is_active = TRUE " +
                "ORDER BY s.play_count DESC LIMIT ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            return executeQueryForList(stmt, SongDAO::mapResultSetToSong);
        }
    }

    public List<Song> getRecentSongs(int limit) throws SQLException {
        String sql = "SELECT s.*, a.artist_name FROM songs s " +
                "JOIN artists a ON s.artist_id = a.artist_id " +
                "WHERE s.is_active = TRUE " +
                "ORDER BY s.release_date DESC LIMIT ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            return executeQueryForList(stmt, SongDAO::mapResultSetToSong);
        }
    }

    public boolean update(Song song) throws SQLException {
        String sql = "UPDATE songs SET title = ?, album_id = ?, genre = ?, duration = ?, " +
                "release_date = ?, file_path = ?, cover_image = ? WHERE song_id = ?";
        return executeUpdate(sql,
                song.getTitle(),
                song.getAlbumId(),
                song.getGenre(),
                song.getDuration(),
                song.getReleaseDate(),
                song.getFilePath(),
                song.getCoverImage(),
                song.getSongId()) > 0;
    }

    public boolean incrementPlayCount(int songId) throws SQLException {
        String sql = "UPDATE songs SET play_count = play_count + 1 WHERE song_id = ?";
        return executeUpdate(sql, songId) > 0;
    }

    public boolean delete(int songId) throws SQLException {
        String sql = "UPDATE songs SET is_active = FALSE WHERE song_id = ?";
        return executeUpdate(sql, songId) > 0;
    }

    public List<String> getAllGenres() throws SQLException {
        String sql = "SELECT DISTINCT genre FROM songs WHERE genre IS NOT NULL AND is_active = TRUE ORDER BY genre";
        List<String> genres = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                genres.add(rs.getString("genre"));
            }
        }
        return genres;
    }

    public static Song mapResultSetToSong(ResultSet rs) throws SQLException {
        Song song = new Song();
        song.setSongId(rs.getInt("song_id"));
        song.setTitle(rs.getString("title"));
        song.setArtistId(rs.getInt("artist_id"));
        int albumId = rs.getInt("album_id");
        song.setAlbumId(rs.wasNull() ? null : albumId);
        song.setGenre(rs.getString("genre"));
        song.setDuration(rs.getInt("duration"));
        song.setReleaseDate(rs.getDate("release_date"));
        song.setFilePath(rs.getString("file_path"));
        song.setCoverImage(rs.getString("cover_image"));
        song.setPlayCount(rs.getInt("play_count"));
        song.setActive(rs.getBoolean("is_active"));
        song.setCreatedAt(rs.getTimestamp("created_at"));
        song.setUpdatedAt(rs.getTimestamp("updated_at"));
        try {
            song.setArtistName(rs.getString("artist_name"));
        } catch (SQLException ignored) {
        }
        return song;
    }
}
