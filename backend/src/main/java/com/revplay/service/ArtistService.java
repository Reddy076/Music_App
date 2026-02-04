package com.revplay.service;

import com.revplay.dao.ArtistDAO;
import com.revplay.dao.AlbumDAO;
import com.revplay.dao.SongDAO;
import com.revplay.dao.FavoriteDAO;
import com.revplay.model.Artist;
import com.revplay.model.Album;
import com.revplay.model.Song;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.revplay.util.ValidationUtil;

// Artist profile, album, and song content management
public class ArtistService {

    private final ArtistDAO artistDAO;
    private final AlbumDAO albumDAO;
    private final SongDAO songDAO;
    private final FavoriteDAO favoriteDAO;

    public ArtistService() {
        this(new ArtistDAO(), new AlbumDAO(), new SongDAO(), new FavoriteDAO());
    }

    public ArtistService(ArtistDAO artistDAO, AlbumDAO albumDAO, SongDAO songDAO,
            FavoriteDAO favoriteDAO) {
        this.artistDAO = artistDAO;
        this.albumDAO = albumDAO;
        this.songDAO = songDAO;
        this.favoriteDAO = favoriteDAO;
    }

    public Optional<Artist> getArtistById(int artistId) throws SQLException {
        return artistDAO.findById(artistId);
    }

    public Optional<Artist> getArtistByUserId(int userId) throws SQLException {
        return artistDAO.findByUserId(userId);
    }

    public List<Artist> getAllArtists() throws SQLException {
        return artistDAO.findAll();
    }

    public List<Artist> getArtistsByGenre(String genre) throws SQLException {
        return artistDAO.findByGenre(genre);
    }

    public List<Artist> searchArtists(String keyword) throws SQLException {
        return artistDAO.search(keyword);
    }

    public boolean updateArtist(Artist artist) throws SQLException {
        return artistDAO.update(artist);
    }

    public List<Album> getAlbumsByArtist(int artistId) throws SQLException {
        return albumDAO.findByArtist(artistId);
    }

    public Map<String, Object> getArtistStats(int artistId) throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPlays", artistDAO.getTotalPlayCount(artistId));
        stats.put("totalFavorites", artistDAO.getTotalFavorites(artistId));

        List<Song> songs = songDAO.findByArtist(artistId);
        stats.put("totalSongs", songs.size());

        List<Album> albums = albumDAO.findByArtist(artistId);
        stats.put("totalAlbums", albums.size());

        return stats;
    }

    public Map<String, Object> getFullArtistProfile(int artistId) throws SQLException {
        Map<String, Object> profile = new HashMap<>();

        Optional<Artist> artistOpt = artistDAO.findById(artistId);
        if (artistOpt.isEmpty()) {
            return profile;
        }

        profile.put("artist", artistOpt.get());
        profile.put("songs", songDAO.findByArtist(artistId));
        profile.put("albums", albumDAO.findByArtist(artistId));
        profile.put("stats", getArtistStats(artistId));

        return profile;
    }

    public List<Map<String, String>> getFavoritesForArtist(int artistId) throws SQLException {
        return favoriteDAO.getFavoritesForArtist(artistId);
    }

    public int createAlbum(Album album) throws SQLException {
        if (!ValidationUtil.isNotEmpty(album.getTitle())) {
            throw new IllegalArgumentException("Album title is required");
        }
        return albumDAO.create(album);
    }

    public Optional<Album> getAlbumById(int albumId) throws SQLException {
        return albumDAO.findById(albumId);
    }

    public boolean updateAlbum(Album album) throws SQLException {
        return albumDAO.update(album);
    }

    public boolean deleteAlbum(int albumId) throws SQLException {
        return albumDAO.delete(albumId);
    }

    public int uploadSong(Song song, int userId) throws SQLException {
        Optional<Artist> artist = artistDAO.findByUserId(userId);
        if (artist.isEmpty()) {
            throw new IllegalArgumentException("Artist profile not found");
        }
        song.setArtistId(artist.get().getArtistId());
        return songDAO.create(song);
    }

    public boolean updateSong(Song song, int userId) throws SQLException {
        Optional<Artist> artist = artistDAO.findByUserId(userId);
        if (artist.isEmpty() || song.getArtistId() != artist.get().getArtistId()) {
            throw new IllegalArgumentException("Access denied");
        }
        return songDAO.update(song);
    }

    public boolean deleteSong(int songId, int userId) throws SQLException {
        Optional<Artist> artist = artistDAO.findByUserId(userId);
        Optional<Song> song = songDAO.findById(songId);

        if (artist.isEmpty() || song.isEmpty() || song.get().getArtistId() != artist.get().getArtistId()) {
            throw new IllegalArgumentException("Access denied");
        }
        return songDAO.delete(songId);
    }
}
