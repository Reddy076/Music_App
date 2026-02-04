package com.revplay.service;

import com.revplay.dao.SongDAO;
import com.revplay.dao.HistoryDAO;
import com.revplay.model.Song;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.revplay.util.ValidationUtil;

// Song playback, search, and library management
public class SongService {
    private static final Logger logger = LogManager.getLogger(SongService.class);
    private final SongDAO songDAO;
    private final HistoryDAO historyDAO;

    public SongService() {
        this(new SongDAO(), new HistoryDAO());
    }

    public SongService(SongDAO songDAO, HistoryDAO historyDAO) {
        this.songDAO = songDAO;
        this.historyDAO = historyDAO;
    }

    public int createSong(Song song) throws SQLException {
        if (!ValidationUtil.isNotEmpty(song.getTitle())) {
            throw new IllegalArgumentException("Song title is required");
        }
        if (song.getDuration() <= 0) {
            throw new IllegalArgumentException("Invalid song duration");
        }
        return songDAO.create(song);
    }

    public Optional<Song> getSongById(int songId) throws SQLException {
        return songDAO.findById(songId);
    }

    public List<Song> getAllSongs() throws SQLException {
        return songDAO.findAll();
    }

    public List<Song> getSongsByArtist(int artistId) throws SQLException {
        return songDAO.findByArtist(artistId);
    }

    public List<Song> getSongsByAlbum(int albumId) throws SQLException {
        return songDAO.findByAlbum(albumId);
    }

    public List<Song> getSongsByGenre(String genre) throws SQLException {
        return songDAO.findByGenre(genre);
    }

    public List<Song> searchSongs(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllSongs();
        }
        return songDAO.search(keyword.trim());
    }

    public List<Song> getPopularSongs(int limit) throws SQLException {
        return songDAO.getPopularSongs(limit > 0 ? limit : 10);
    }

    public List<Song> getRecentSongs(int limit) throws SQLException {
        return songDAO.getRecentSongs(limit > 0 ? limit : 10);
    }

    public List<String> getAllGenres() throws SQLException {
        return songDAO.getAllGenres();
    }

    public boolean updateSong(Song song) throws SQLException {
        return songDAO.update(song);
    }

    public boolean deleteSong(int songId) throws SQLException {
        return songDAO.delete(songId);
    }

    public void playSong(int userId, int songId, int durationPlayed) throws SQLException {
        songDAO.incrementPlayCount(songId);
        historyDAO.addToHistory(userId, songId, durationPlayed);
        logger.info("User {} played song {}", userId, songId);
    }

    public List<Song> getRecentlyPlayed(int userId, int limit) throws SQLException {
        return historyDAO.getRecentlyPlayed(userId, limit > 0 ? limit : 20);
    }
}
