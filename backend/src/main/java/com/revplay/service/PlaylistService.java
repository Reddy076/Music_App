package com.revplay.service;

import com.revplay.dao.PlaylistDAO;
import com.revplay.model.Playlist;
import com.revplay.model.Song;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.revplay.util.ValidationUtil;

public class PlaylistService {

    private final PlaylistDAO playlistDAO;

    public PlaylistService() {
        this(new PlaylistDAO());
    }

    public PlaylistService(PlaylistDAO playlistDAO) {
        this.playlistDAO = playlistDAO;
    }

    public int createPlaylist(int userId, String name, String description, boolean isPublic) throws SQLException {
        if (!ValidationUtil.isNotEmpty(name)) {
            throw new IllegalArgumentException("Playlist name is required");
        }
        Playlist playlist = new Playlist(userId, name.trim(), isPublic);
        playlist.setDescription(description);
        return playlistDAO.create(playlist);
    }

    public int createPlaylist(Playlist playlist) throws SQLException {
        if (!ValidationUtil.isNotEmpty(playlist.getName())) {
            throw new IllegalArgumentException("Playlist name is required");
        }
        return playlistDAO.create(playlist);
    }

    public boolean updatePlaylist(Playlist playlist) throws SQLException {
        return playlistDAO.update(playlist);
    }

    public Optional<Playlist> getPlaylistById(int playlistId) throws SQLException {
        return playlistDAO.findById(playlistId);
    }

    public List<Playlist> getUserPlaylists(int userId) throws SQLException {
        return playlistDAO.findByUser(userId);
    }

    public List<Playlist> getPublicPlaylists() throws SQLException {
        return playlistDAO.findPublicPlaylists();
    }

    public List<Playlist> searchPlaylists(String keyword) throws SQLException {
        return playlistDAO.search(keyword);
    }

    public boolean updatePlaylist(Playlist playlist, int userId) throws SQLException {
        Optional<Playlist> existing = playlistDAO.findById(playlist.getPlaylistId());
        if (existing.isEmpty() || existing.get().getUserId() != userId) {
            throw new IllegalArgumentException("Playlist not found or access denied");
        }
        return playlistDAO.update(playlist);
    }

    public boolean deletePlaylist(int playlistId, int userId) throws SQLException {
        Optional<Playlist> existing = playlistDAO.findById(playlistId);
        if (existing.isEmpty() || existing.get().getUserId() != userId) {
            throw new IllegalArgumentException("Playlist not found or access denied");
        }
        return playlistDAO.delete(playlistId);
    }

    public boolean addSongToPlaylist(int playlistId, int songId, int userId) throws SQLException {
        Optional<Playlist> existing = playlistDAO.findById(playlistId);
        if (existing.isEmpty() || existing.get().getUserId() != userId) {
            throw new IllegalArgumentException("Playlist not found or access denied");
        }
        if (playlistDAO.containsSong(playlistId, songId)) {
            throw new IllegalArgumentException("Song already in playlist");
        }
        return playlistDAO.addSong(playlistId, songId);
    }

    public boolean removeSongFromPlaylist(int playlistId, int songId, int userId) throws SQLException {
        Optional<Playlist> existing = playlistDAO.findById(playlistId);
        if (existing.isEmpty() || existing.get().getUserId() != userId) {
            throw new IllegalArgumentException("Playlist not found or access denied");
        }
        return playlistDAO.removeSong(playlistId, songId);
    }

    public List<Song> getPlaylistSongs(int playlistId) throws SQLException {
        return playlistDAO.getSongs(playlistId);
    }

    public Optional<Playlist> getPlaylistWithSongs(int playlistId) throws SQLException {
        Optional<Playlist> playlistOpt = playlistDAO.findById(playlistId);
        if (playlistOpt.isPresent()) {
            Playlist playlist = playlistOpt.get();
            playlist.setSongs(playlistDAO.getSongs(playlistId));
            return Optional.of(playlist);
        }
        return Optional.empty();
    }
}
