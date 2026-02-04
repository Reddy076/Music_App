package com.revplay.dao;

import com.revplay.model.Artist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.Optional;

public class ArtistDAO extends BaseDAO {

    public ArtistDAO() {
        super();
    }

    public int create(Artist artist) throws SQLException {
        String sql = "INSERT INTO artists (user_id, artist_name, bio, genre, profile_image, " +
                "facebook_link, twitter_link, instagram_link, spotify_link) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return executeInsert(sql,
                artist.getUserId(),
                artist.getArtistName(),
                artist.getBio(),
                artist.getGenre(),
                artist.getProfileImage(),
                artist.getFacebookLink(),
                artist.getTwitterLink(),
                artist.getInstagramLink(),
                artist.getSpotifyLink());
    }

    public Optional<Artist> findById(int artistId) throws SQLException {
        String sql = "SELECT * FROM artists WHERE artist_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, artistId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToArtist(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Artist> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM artists WHERE user_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToArtist(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Artist> findAll() throws SQLException {
        String sql = "SELECT * FROM artists ORDER BY artist_name";
        return executeQueryForList(sql, this::mapResultSetToArtist);
    }

    public List<Artist> findByGenre(String genre) throws SQLException {
        String sql = "SELECT * FROM artists WHERE genre = ? ORDER BY artist_name";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, genre);
            return executeQueryForList(stmt, this::mapResultSetToArtist);
        }
    }

    public List<Artist> search(String keyword) throws SQLException {
        String sql = "SELECT * FROM artists WHERE artist_name LIKE ? OR genre LIKE ? ORDER BY artist_name";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            return executeQueryForList(stmt, this::mapResultSetToArtist);
        }
    }

    public boolean update(Artist artist) throws SQLException {
        String sql = "UPDATE artists SET artist_name = ?, bio = ?, genre = ?, profile_image = ?, " +
                "facebook_link = ?, twitter_link = ?, instagram_link = ?, spotify_link = ? WHERE artist_id = ?";

        return executeUpdate(sql,
                artist.getArtistName(),
                artist.getBio(),
                artist.getGenre(),
                artist.getProfileImage(),
                artist.getFacebookLink(),
                artist.getTwitterLink(),
                artist.getInstagramLink(),
                artist.getSpotifyLink(),
                artist.getArtistId()) > 0;
    }

    public boolean delete(int artistId) throws SQLException {
        String sql = "DELETE FROM artists WHERE artist_id = ?";
        return executeUpdate(sql, artistId) > 0;
    }

    public int getTotalPlayCount(int artistId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(play_count), 0) as total FROM songs WHERE artist_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, artistId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }

    public int getTotalFavorites(int artistId) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM favorites f " +
                "JOIN songs s ON f.song_id = s.song_id WHERE s.artist_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, artistId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }

    private Artist mapResultSetToArtist(ResultSet rs) throws SQLException {
        Artist artist = new Artist();
        artist.setArtistId(rs.getInt("artist_id"));
        artist.setUserId(rs.getInt("user_id"));
        artist.setArtistName(rs.getString("artist_name"));
        artist.setBio(rs.getString("bio"));
        artist.setGenre(rs.getString("genre"));
        artist.setProfileImage(rs.getString("profile_image"));
        artist.setFacebookLink(rs.getString("facebook_link"));
        artist.setTwitterLink(rs.getString("twitter_link"));
        artist.setInstagramLink(rs.getString("instagram_link"));
        artist.setSpotifyLink(rs.getString("spotify_link"));
        artist.setCreatedAt(rs.getTimestamp("created_at"));
        artist.setUpdatedAt(rs.getTimestamp("updated_at"));
        return artist;
    }
}
