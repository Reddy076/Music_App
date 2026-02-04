package com.revplay.service;

import com.revplay.dao.*;
import com.revplay.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchService {
    private static final Logger logger = LogManager.getLogger(SearchService.class);
    private final SongDAO songDAO;
    private final ArtistDAO artistDAO;
    private final AlbumDAO albumDAO;
    private final PlaylistDAO playlistDAO;
    private final PodcastDAO podcastDAO;

    public SearchService() {
        this(new SongDAO(), new ArtistDAO(), new AlbumDAO(), new PlaylistDAO(), new PodcastDAO());
    }

    public SearchService(SongDAO songDAO, ArtistDAO artistDAO, AlbumDAO albumDAO, PlaylistDAO playlistDAO,
            PodcastDAO podcastDAO) {
        this.songDAO = songDAO;
        this.artistDAO = artistDAO;
        this.albumDAO = albumDAO;
        this.playlistDAO = playlistDAO;
        this.podcastDAO = podcastDAO;
    }

    public Map<String, Object> searchAll(String keyword) throws SQLException {
        Map<String, Object> results = new HashMap<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return results;
        }

        String searchTerm = keyword.trim();
        logger.info("Performing unified search for: {}", searchTerm);

        results.put("songs", songDAO.search(searchTerm));
        results.put("artists", artistDAO.search(searchTerm));
        results.put("albums", albumDAO.search(searchTerm));
        results.put("playlists", playlistDAO.search(searchTerm));
        results.put("podcasts", podcastDAO.search(searchTerm));

        return results;
    }

    public List<Song> searchSongs(String keyword) throws SQLException {
        return songDAO.search(keyword);
    }

    public List<Artist> searchArtists(String keyword) throws SQLException {
        return artistDAO.search(keyword);
    }

    public List<Album> searchAlbums(String keyword) throws SQLException {
        return albumDAO.search(keyword);
    }

    public List<Podcast> searchPodcasts(String keyword) throws SQLException {
        return podcastDAO.search(keyword);
    }

    public Map<String, Object> browseByGenre(String genre) throws SQLException {
        Map<String, Object> results = new HashMap<>();
        results.put("songs", songDAO.findByGenre(genre));
        results.put("artists", artistDAO.findByGenre(genre));
        results.put("albums", albumDAO.findByGenre(genre));
        return results;
    }

    public List<String> getAllGenres() throws SQLException {
        return songDAO.getAllGenres();
    }

    public Map<String, Object> getHomePageContent() throws SQLException {
        Map<String, Object> content = new HashMap<>();
        content.put("popularSongs", songDAO.getPopularSongs(10));
        content.put("recentSongs", songDAO.getRecentSongs(10));
        content.put("artists", artistDAO.findAll());
        content.put("genres", songDAO.getAllGenres());
        return content;
    }
}
