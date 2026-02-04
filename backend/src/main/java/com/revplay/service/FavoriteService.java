package com.revplay.service;

import com.revplay.dao.FavoriteDAO;
import com.revplay.model.Song;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

public class FavoriteService {
    private static final Logger logger = LogManager.getLogger(FavoriteService.class);
    private final FavoriteDAO favoriteDAO;

    public FavoriteService() {
        this(new FavoriteDAO());
    }

    public FavoriteService(FavoriteDAO favoriteDAO) {
        this.favoriteDAO = favoriteDAO;
    }

    public boolean addToFavorites(int userId, int songId) throws SQLException {
        if (favoriteDAO.isFavorite(userId, songId)) {
            logger.info("Song {} already in favorites for user {}", songId, userId);
            return false;
        }
        return favoriteDAO.add(userId, songId);
    }

    public boolean removeFromFavorites(int userId, int songId) throws SQLException {
        return favoriteDAO.remove(userId, songId);
    }

    public boolean toggleFavorite(int userId, int songId) throws SQLException {
        if (favoriteDAO.isFavorite(userId, songId)) {
            return favoriteDAO.remove(userId, songId);
        } else {
            return favoriteDAO.add(userId, songId);
        }
    }

    public boolean isFavorite(int userId, int songId) throws SQLException {
        return favoriteDAO.isFavorite(userId, songId);
    }

    public List<Song> getFavoriteSongs(int userId) throws SQLException {
        return favoriteDAO.getFavoriteSongs(userId);
    }

    public int getFavoriteCount(int userId) throws SQLException {
        return favoriteDAO.getFavoriteCount(userId);
    }

    public List<Integer> getUsersWhoFavorited(int songId) throws SQLException {
        return favoriteDAO.getUsersWhoFavorited(songId);
    }
}
