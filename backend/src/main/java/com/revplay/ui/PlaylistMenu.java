package com.revplay.ui;

import com.revplay.model.Playlist;
import com.revplay.model.Song;
import com.revplay.service.PlaylistService;
import com.revplay.service.SongService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class PlaylistMenu extends BaseMenu {
    private static final Logger logger = LogManager.getLogger(PlaylistMenu.class);
    private final PlaylistService playlistService;
    private final SongService songService;

    public PlaylistMenu() {
        super();
        this.playlistService = new PlaylistService();
        this.songService = new SongService();
    }

    public void display() {
        boolean running = true;
        while (running) {
            ConsoleUtil.printSectionHeader("My Playlists");

            String[] options = {
                    "View My Playlists",
                    "Create New Playlist",
                    "Edit Playlist",
                    "Delete Playlist",
                    "Add Song to Playlist",
                    "Remove Song from Playlist",
                    "Back to Main Menu"
            };
            ConsoleUtil.printMenu(options);

            int choice = ConsoleUtil.readInt("Enter your choice: ", 1, 7);

            switch (choice) {
                case 1 -> viewMyPlaylists();
                case 2 -> createPlaylist();
                case 3 -> editPlaylist();
                case 4 -> deletePlaylist();
                case 5 -> addSongToPlaylist();
                case 6 -> removeSongFromPlaylist();
                case 7 -> running = false;
            }
        }
    }

    private void viewMyPlaylists() {
        try {
            List<Playlist> playlists = playlistService.getUserPlaylists(session.getCurrentUserId());
            ConsoleUtil.printSectionHeader("My Playlists (" + playlists.size() + ")");

            if (playlists.isEmpty()) {
                ConsoleUtil.printInfo("You don't have any playlists yet. Create one!");
                ConsoleUtil.pressEnterToContinue();
                return;
            }

            displayPlaylists(playlists);

            if (ConsoleUtil.readConfirmation("View a playlist's songs?")) {
                int playlistIndex = ConsoleUtil.readInt("Enter playlist number: ", 1, playlists.size());
                Playlist selected = playlists.get(playlistIndex - 1);
                viewPlaylistSongs(selected);
            }

        } catch (Exception e) {
            logger.error("Error loading playlists", e);
            ConsoleUtil.printError("Failed to load playlists: " + e.getMessage());
            ConsoleUtil.pressEnterToContinue();
        }
    }

    private void viewPlaylistSongs(Playlist playlist) {
        try {
            List<Song> songs = playlistService.getPlaylistSongs(playlist.getPlaylistId());

            ConsoleUtil.printSectionHeader("Playlist: " + playlist.getName());
            System.out.println("  " + (playlist.isPublic() ? "Public" : "Private") + " playlist");
            if (playlist.getDescription() != null && !playlist.getDescription().isEmpty()) {
                System.out.println("  Description: " + playlist.getDescription());
            }
            System.out.println("  Total songs: " + songs.size());
            System.out.println();

            if (songs.isEmpty()) {
                ConsoleUtil.printInfo("This playlist is empty. Add some songs!");
            } else {
                displaySongs(songs);

                if (ConsoleUtil.readConfirmation("Would you like to play a song?")) {
                    int songIndex = ConsoleUtil.readInt("Enter song number: ", 1, songs.size());
                    Song selectedSong = songs.get(songIndex - 1);
                    new PlayerMenu().playSong(selectedSong);
                    return;
                }
            }

        } catch (Exception e) {
            logger.error("Error loading playlist songs", e);
            ConsoleUtil.printError("Failed to load playlist songs: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void createPlaylist() {
        ConsoleUtil.printSectionHeader("Create New Playlist");

        String name = ConsoleUtil.readNonEmptyString("Playlist name: ");
        String description = ConsoleUtil.readOptionalString("Description (optional): ");
        boolean isPublic = ConsoleUtil.readConfirmation("Make this playlist public?");

        try {
            Playlist playlist = new Playlist(session.getCurrentUserId(), name, isPublic);
            playlist.setDescription(description);

            playlistService.createPlaylist(playlist);
            logger.info("Playlist created: {} by user {}", name, session.getCurrentUserId());
            ConsoleUtil.printSuccess("Playlist '" + name + "' created successfully!");

        } catch (Exception e) {
            logger.error("Error creating playlist", e);
            ConsoleUtil.printError("Failed to create playlist: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void editPlaylist() {
        try {
            List<Playlist> playlists = playlistService.getUserPlaylists(session.getCurrentUserId());
            if (playlists.isEmpty()) {
                ConsoleUtil.printInfo("You don't have any playlists to edit.");
                ConsoleUtil.pressEnterToContinue();
                return;
            }

            ConsoleUtil.printSectionHeader("Edit Playlist");
            displayPlaylists(playlists);

            int playlistIndex = ConsoleUtil.readInt("Select playlist to edit: ", 1, playlists.size());
            Playlist playlist = playlists.get(playlistIndex - 1);

            System.out.println("\nCurrent details:");
            System.out.println("  Name: " + playlist.getName());
            System.out.println(
                    "  Description: " + (playlist.getDescription() != null ? playlist.getDescription() : "None"));
            System.out.println("  Public: " + (playlist.isPublic() ? "Yes" : "No"));

            String newName = ConsoleUtil.readOptionalString("New name (press Enter to keep current): ");
            if (!newName.isEmpty()) {
                playlist.setName(newName);
            }

            String newDesc = ConsoleUtil.readOptionalString("New description (press Enter to keep current): ");
            if (!newDesc.isEmpty()) {
                playlist.setDescription(newDesc);
            }

            if (ConsoleUtil.readConfirmation("Change privacy setting?")) {
                playlist.setPublic(!playlist.isPublic());
            }

            boolean success = playlistService.updatePlaylist(playlist);
            if (success) {
                logger.info("Playlist updated: {}", playlist.getName());
                ConsoleUtil.printSuccess("Playlist updated successfully!");
            } else {
                ConsoleUtil.printError("Failed to update playlist.");
            }

        } catch (Exception e) {
            logger.error("Error editing playlist", e);
            ConsoleUtil.printError("Failed to edit playlist: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void deletePlaylist() {
        try {
            List<Playlist> playlists = playlistService.getUserPlaylists(session.getCurrentUserId());
            if (playlists.isEmpty()) {
                ConsoleUtil.printInfo("You don't have any playlists to delete.");
                ConsoleUtil.pressEnterToContinue();
                return;
            }

            ConsoleUtil.printSectionHeader("Delete Playlist");
            displayPlaylists(playlists);

            int playlistIndex = ConsoleUtil.readInt("Select playlist to delete: ", 1, playlists.size());
            Playlist playlist = playlists.get(playlistIndex - 1);

            if (ConsoleUtil.readConfirmation("Are you sure you want to delete '" + playlist.getName() + "'?")) {
                boolean success = playlistService.deletePlaylist(playlist.getPlaylistId(), session.getCurrentUserId());
                if (success) {
                    logger.info("Playlist deleted: {}", playlist.getName());
                    ConsoleUtil.printSuccess("Playlist deleted successfully!");
                } else {
                    ConsoleUtil.printError("Failed to delete playlist.");
                }
            }

        } catch (Exception e) {
            logger.error("Error deleting playlist", e);
            ConsoleUtil.printError("Failed to delete playlist: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void addSongToPlaylist() {
        try {
            List<Playlist> playlists = playlistService.getUserPlaylists(session.getCurrentUserId());
            if (playlists.isEmpty()) {
                ConsoleUtil.printInfo("You need to create a playlist first.");
                ConsoleUtil.pressEnterToContinue();
                return;
            }

            ConsoleUtil.printSectionHeader("Add Song to Playlist");

            String keyword = ConsoleUtil.readNonEmptyString("Search for a song: ");
            List<Song> songs = songService.searchSongs(keyword);

            if (songs.isEmpty()) {
                ConsoleUtil.printInfo("No songs found matching '" + keyword + "'");
                ConsoleUtil.pressEnterToContinue();
                return;
            }

            displaySongs(songs);
            int songIndex = ConsoleUtil.readInt("Select song: ", 1, songs.size());
            Song selectedSong = songs.get(songIndex - 1);

            System.out.println("\nYour playlists:");
            displayPlaylists(playlists);
            int playlistIndex = ConsoleUtil.readInt("Select playlist: ", 1, playlists.size());
            Playlist selectedPlaylist = playlists.get(playlistIndex - 1);

            boolean success = playlistService.addSongToPlaylist(selectedPlaylist.getPlaylistId(),
                    selectedSong.getSongId(),
                    session.getCurrentUserId());
            if (success) {
                logger.info("Song added to playlist: {} -> {}", selectedSong.getTitle(), selectedPlaylist.getName());
                ConsoleUtil.printSuccess(
                        "'" + selectedSong.getTitle() + "' added to '" + selectedPlaylist.getName() + "'!");
            } else {
                ConsoleUtil.printError("Song might already be in the playlist.");
            }

        } catch (Exception e) {
            logger.error("Error adding song to playlist", e);
            ConsoleUtil.printError("Failed to add song: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void removeSongFromPlaylist() {
        try {
            List<Playlist> playlists = playlistService.getUserPlaylists(session.getCurrentUserId());
            if (playlists.isEmpty()) {
                ConsoleUtil.printInfo("You don't have any playlists.");
                ConsoleUtil.pressEnterToContinue();
                return;
            }

            ConsoleUtil.printSectionHeader("Remove Song from Playlist");
            displayPlaylists(playlists);

            int playlistIndex = ConsoleUtil.readInt("Select playlist: ", 1, playlists.size());
            Playlist playlist = playlists.get(playlistIndex - 1);

            List<Song> songs = playlistService.getPlaylistSongs(playlist.getPlaylistId());
            if (songs.isEmpty()) {
                ConsoleUtil.printInfo("This playlist is empty.");
                ConsoleUtil.pressEnterToContinue();
                return;
            }

            System.out.println("\nSongs in '" + playlist.getName() + "':");
            displaySongs(songs);

            int songIndex = ConsoleUtil.readInt("Select song to remove: ", 1, songs.size());
            Song selectedSong = songs.get(songIndex - 1);

            if (ConsoleUtil.readConfirmation("Remove '" + selectedSong.getTitle() + "' from playlist?")) {
                boolean success = playlistService.removeSongFromPlaylist(playlist.getPlaylistId(),
                        selectedSong.getSongId(),
                        session.getCurrentUserId());
                if (success) {
                    logger.info("Song removed from playlist: {} <- {}", selectedSong.getTitle(), playlist.getName());
                    ConsoleUtil.printSuccess("Song removed from playlist!");
                } else {
                    ConsoleUtil.printError("Failed to remove song.");
                }
            }

        } catch (Exception e) {
            logger.error("Error removing song from playlist", e);
            ConsoleUtil.printError("Failed to remove song: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void displayPlaylists(List<Playlist> playlists) {
        DisplayHelper.displayPlaylists(playlists);
    }

    private void displaySongs(List<Song> songs) {
        DisplayHelper.displaySongs(songs);
    }

}
