package com.revplay.ui;

import com.revplay.model.Song;
import com.revplay.service.FavoriteService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class FavoritesMenu extends BaseMenu {
    private static final Logger logger = LogManager.getLogger(FavoritesMenu.class);
    private final FavoriteService favoriteService;

    public FavoritesMenu() {
        super();
        this.favoriteService = new FavoriteService();
    }

    public void display() {
        boolean running = true;
        while (running) {
            ConsoleUtil.printSectionHeader("My Favorites");

            String[] options = {
                    "View Favorite Songs",
                    "Remove from Favorites",
                    "Back to Main Menu"
            };
            ConsoleUtil.printMenu(options);

            int choice = ConsoleUtil.readInt("Enter your choice: ", 1, 3);

            switch (choice) {
                case 1 -> viewFavorites();
                case 2 -> removeFromFavorites();
                case 3 -> running = false;
            }
        }
    }

    private void viewFavorites() {
        try {
            List<Song> favorites = favoriteService.getFavoriteSongs(session.getCurrentUserId());

            ConsoleUtil.printSectionHeader("My Favorite Songs (" + favorites.size() + ")");

            if (favorites.isEmpty()) {
                ConsoleUtil.printInfo("You haven't added any songs to favorites yet.");
                ConsoleUtil.printInfo("Browse songs and mark them as favorites!");
                ConsoleUtil.pressEnterToContinue();
                return;
            }

            displaySongs(favorites);

            if (ConsoleUtil.readConfirmation("Would you like to play a song?")) {
                int songIndex = ConsoleUtil.readInt("Enter song number: ", 1, favorites.size());
                Song selectedSong = favorites.get(songIndex - 1);
                new PlayerMenu().playSong(selectedSong);
                return;
            }

        } catch (Exception e) {
            logger.error("Error loading favorites", e);
            ConsoleUtil.printError("Failed to load favorites: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void removeFromFavorites() {
        try {
            List<Song> favorites = favoriteService.getFavoriteSongs(session.getCurrentUserId());

            if (favorites.isEmpty()) {
                ConsoleUtil.printInfo("Your favorites list is empty.");
                ConsoleUtil.pressEnterToContinue();
                return;
            }

            ConsoleUtil.printSectionHeader("Remove from Favorites");
            displaySongs(favorites);

            int songIndex = ConsoleUtil.readInt("Select song to remove: ", 1, favorites.size());
            Song selectedSong = favorites.get(songIndex - 1);

            if (ConsoleUtil.readConfirmation("Remove '" + selectedSong.getTitle() + "' from favorites?")) {
                boolean success = favoriteService.removeFromFavorites(session.getCurrentUserId(),
                        selectedSong.getSongId());
                if (success) {
                    logger.info("Song removed from favorites: {}", selectedSong.getTitle());
                    ConsoleUtil.printSuccess("'" + selectedSong.getTitle() + "' removed from favorites!");
                } else {
                    ConsoleUtil.printError("Failed to remove from favorites.");
                }
            }

        } catch (Exception e) {
            logger.error("Error removing from favorites", e);
            ConsoleUtil.printError("Failed to remove from favorites: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    public void addToFavorites(Song song) {
        try {
            boolean success = favoriteService.addToFavorites(session.getCurrentUserId(), song.getSongId());
            if (success) {
                logger.info("Song added to favorites: {}", song.getTitle());
                ConsoleUtil.printSuccess("'" + song.getTitle() + "' added to favorites!");
            } else {
                ConsoleUtil.printInfo("Song is already in your favorites.");
            }
        } catch (Exception e) {
            logger.error("Error adding to favorites", e);
            ConsoleUtil.printError("Failed to add to favorites: " + e.getMessage());
        }
    }

    public boolean isFavorite(int songId) {
        try {
            return favoriteService.isFavorite(session.getCurrentUserId(), songId);
        } catch (Exception e) {
            return false;
        }
    }

    private void displaySongs(List<Song> songs) {
        DisplayHelper.displaySongs(songs);
    }

}
