package com.revplay.dao;

import com.revplay.model.Album;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.Optional;

public class AlbumDAO extends BaseDAO {

    public AlbumDAO() {
        super();
    }

    public int create(Album album) throws SQLException {
        String sql = "INSERT INTO albums (artist_id, title, description, cover_image, release_date, genre) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        return executeInsert(sql,
                album.getArtistId(),
                album.getTitle(),
                album.getDescription(),
                album.getCoverImage(),
                album.getReleaseDate(),
                album.getGenre());
    }

    public Optional<Album> findById(int albumId) throws SQLException {
        String sql = "SELECT al.*, a.artist_name, COUNT(s.song_id) as song_count " +
                "FROM albums al JOIN artists a ON al.artist_id = a.artist_id " +
                "LEFT JOIN songs s ON al.album_id = s.album_id " +
                "WHERE al.album_id = ? GROUP BY al.album_id";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, albumId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAlbum(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Album> findAll() throws SQLException {
        String sql = "SELECT al.*, a.artist_name, COUNT(s.song_id) as song_count " +
                "FROM albums al JOIN artists a ON al.artist_id = a.artist_id " +
                "LEFT JOIN songs s ON al.album_id = s.album_id " +
                "GROUP BY al.album_id ORDER BY al.release_date DESC";
        return executeQueryForList(sql, this::mapResultSetToAlbum);
    }

    public List<Album> findByArtist(int artistId) throws SQLException {
        String sql = "SELECT al.*, a.artist_name, COUNT(s.song_id) as song_count " +
                "FROM albums al JOIN artists a ON al.artist_id = a.artist_id " +
                "LEFT JOIN songs s ON al.album_id = s.album_id " +
                "WHERE al.artist_id = ? GROUP BY al.album_id ORDER BY al.release_date DESC";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, artistId);
            return executeQueryForList(stmt, this::mapResultSetToAlbum);
        }
    }

    public List<Album> findByGenre(String genre) throws SQLException {
        String sql = "SELECT al.*, a.artist_name, COUNT(s.song_id) as song_count " +
                "FROM albums al JOIN artists a ON al.artist_id = a.artist_id " +
                "LEFT JOIN songs s ON al.album_id = s.album_id " +
                "WHERE al.genre = ? GROUP BY al.album_id ORDER BY al.release_date DESC";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, genre);
            return executeQueryForList(stmt, this::mapResultSetToAlbum);
        }
    }

    public List<Album> search(String keyword) throws SQLException {
        String sql = "SELECT al.*, a.artist_name, COUNT(s.song_id) as song_count " +
                "FROM albums al JOIN artists a ON al.artist_id = a.artist_id " +
                "LEFT JOIN songs s ON al.album_id = s.album_id " +
                "WHERE al.title LIKE ? OR a.artist_name LIKE ? " +
                "GROUP BY al.album_id ORDER BY al.release_date DESC";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            return executeQueryForList(stmt, this::mapResultSetToAlbum);
        }
    }

    public boolean update(Album album) throws SQLException {
        String sql = "UPDATE albums SET title = ?, description = ?, cover_image = ?, " +
                "release_date = ?, genre = ? WHERE album_id = ?";
        return executeUpdate(sql,
                album.getTitle(),
                album.getDescription(),
                album.getCoverImage(),
                album.getReleaseDate(),
                album.getGenre(),
                album.getAlbumId()) > 0;
    }

    public boolean delete(int albumId) throws SQLException {
        String sql = "DELETE FROM albums WHERE album_id = ?";
        return executeUpdate(sql, albumId) > 0;
    }

    private Album mapResultSetToAlbum(ResultSet rs) throws SQLException {
        Album album = new Album();
        album.setAlbumId(rs.getInt("album_id"));
        album.setArtistId(rs.getInt("artist_id"));
        album.setTitle(rs.getString("title"));
        album.setDescription(rs.getString("description"));
        album.setCoverImage(rs.getString("cover_image"));
        album.setReleaseDate(rs.getDate("release_date"));
        album.setGenre(rs.getString("genre"));
        album.setCreatedAt(rs.getTimestamp("created_at"));
        album.setUpdatedAt(rs.getTimestamp("updated_at"));
        try {
            album.setArtistName(rs.getString("artist_name"));
            album.setSongCount(rs.getInt("song_count"));
        } catch (SQLException ignored) {
        }
        return album;
    }
}
