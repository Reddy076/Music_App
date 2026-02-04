package com.revplay.ui;

import com.revplay.dao.FavoriteDAO;
import com.revplay.dao.UserDAO;
import com.revplay.model.Album;
import com.revplay.model.Artist;
import com.revplay.model.Song;
import com.revplay.model.User;
import com.revplay.service.ArtistService;
import com.revplay.service.SongService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public class ArtistMenu extends BaseMenu {
    private static final Logger logger = LogManager.getLogger(ArtistMenu.class);
    private final ArtistService artistService;
    private final SongService songService;
    private final FavoriteDAO favoriteDAO;
    private final UserDAO userDAO;

    public ArtistMenu() {
        super();
        this.artistService = new ArtistService();
        this.songService = new SongService();
        this.favoriteDAO = new FavoriteDAO();
        this.userDAO = new UserDAO();
    }

    public void display() {
        if (!session.isArtist()) {
            ConsoleUtil.printError("You must be registered as an artist to access this menu.");
            ConsoleUtil.pressEnterToContinue();
            return;
        }

        boolean running = true;
        while (running) {
            ConsoleUtil.printSectionHeader("Artist Dashboard");

            String[] options = {
                    "View My Profile",
                    "Edit My Profile",
                    "Upload New Song",
                    "View My Songs",
                    "Edit Song Details",
                    "Delete Song",
                    "Create Album",
                    "View My Albums",
                    "Edit Album Details",
                    "Delete Album",
                    "View My Statistics",
                    "See Who Favorited My Songs",
                    "Back to Main Menu"
            };
            ConsoleUtil.printMenu(options);

            int choice = ConsoleUtil.readInt("Enter your choice: ", 1, 13);

            switch (choice) {
                case 1 -> viewProfile();
                case 2 -> editProfile();
                case 3 -> uploadSong();
                case 4 -> viewMySongs();
                case 5 -> editSong();
                case 6 -> deleteSong();
                case 7 -> createAlbum();
                case 8 -> viewMyAlbums();
                case 9 -> editAlbum();
                case 10 -> deleteAlbum();
                case 11 -> viewStatistics();
                case 12 -> seeWhoFavoritedMySongs();
                case 13 -> running = false;
            }
        }
    }

    private void viewProfile() {
        Artist artist = session.getCurrentArtist();
        if (artist == null) {
            ConsoleUtil.printError("Artist profile not loaded.");
            ConsoleUtil.pressEnterToContinue();
            return;
        }

        ConsoleUtil.printSectionHeader("Artist Profile");
        System.out.println("  Artist Name: " + artist.getArtistName());
        System.out.println("  Genre: " + (artist.getGenre() != null ? artist.getGenre() : "Not set"));
        System.out.println("  Bio: " + (artist.getBio() != null ? artist.getBio() : "Not set"));
        System.out.println();
        System.out.println("  Social Media Links:");
        System.out
                .println("    Facebook: " + (artist.getFacebookLink() != null ? artist.getFacebookLink() : "Not set"));
        System.out.println("    Twitter: " + (artist.getTwitterLink() != null ? artist.getTwitterLink() : "Not set"));
        System.out.println(
                "    Instagram: " + (artist.getInstagramLink() != null ? artist.getInstagramLink() : "Not set"));
        System.out.println("    Spotify: " + (artist.getSpotifyLink() != null ? artist.getSpotifyLink() : "Not set"));

        ConsoleUtil.pressEnterToContinue();
    }

    private void editProfile() {
        Artist artist = session.getCurrentArtist();
        if (artist == null) {
            ConsoleUtil.printError("Artist profile not loaded.");
            ConsoleUtil.pressEnterToContinue();
            return;
        }

        ConsoleUtil.printSectionHeader("Edit Artist Profile");
        System.out.println("(Press Enter to keep current value)\n");

        String newName = ConsoleUtil.readOptionalString("Artist Name [" + artist.getArtistName() + "]: ");
        if (!newName.isEmpty())
            artist.setArtistName(newName);

        String newGenre = ConsoleUtil
                .readOptionalString("Genre [" + (artist.getGenre() != null ? artist.getGenre() : "") + "]: ");
        if (!newGenre.isEmpty())
            artist.setGenre(newGenre);

        String newBio = ConsoleUtil.readOptionalString("Bio: ");
        if (!newBio.isEmpty())
            artist.setBio(newBio);

        System.out.println("\nSocial Media Links (press Enter to skip):");
        String fb = ConsoleUtil.readOptionalString("Facebook URL: ");
        if (!fb.isEmpty())
            artist.setFacebookLink(fb);

        String tw = ConsoleUtil.readOptionalString("Twitter URL: ");
        if (!tw.isEmpty())
            artist.setTwitterLink(tw);

        String ig = ConsoleUtil.readOptionalString("Instagram URL: ");
        if (!ig.isEmpty())
            artist.setInstagramLink(ig);

        String sp = ConsoleUtil.readOptionalString("Spotify URL: ");
        if (!sp.isEmpty())
            artist.setSpotifyLink(sp);

        try {
            boolean success = artistService.updateArtist(artist);
            if (success) {
                logger.info("Artist profile updated: {}", artist.getArtistName());
                ConsoleUtil.printSuccess("Profile updated successfully!");
            } else {
                ConsoleUtil.printError("Failed to update profile.");
            }
        } catch (Exception e) {
            ConsoleUtil.printError("Error: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void uploadSong() {
        ConsoleUtil.printSectionHeader("Upload New Song");

        String title = ConsoleUtil.readNonEmptyString("Song Title: ");
        String genre = ConsoleUtil.readNonEmptyString("Genre: ");

        int durationMin = ConsoleUtil.readInt("Duration (minutes): ", 0, 60);
        int durationSec = ConsoleUtil.readInt("Duration (seconds): ", 0, 59);
        int duration = durationMin * 60 + durationSec;

        String releaseDateStr = ConsoleUtil.readOptionalString("Release Date (YYYY-MM-DD) [today]: ");
        Date releaseDate;
        if (releaseDateStr.isEmpty()) {
            releaseDate = new Date(System.currentTimeMillis());
        } else {
            try {
                releaseDate = Date.valueOf(releaseDateStr);
            } catch (Exception e) {
                releaseDate = new Date(System.currentTimeMillis());
            }
        }

        Integer albumId = null;
        try {
            List<Album> albums = artistService.getAlbumsByArtist(session.getCurrentArtistId());
            if (!albums.isEmpty() && ConsoleUtil.readConfirmation("Add to an album?")) {
                displayAlbums(albums);
                int albumIndex = ConsoleUtil.readInt("Select album: ", 1, albums.size());
                albumId = albums.get(albumIndex - 1).getAlbumId();
            }
        } catch (Exception ignored) {
        }

        String filePath = ConsoleUtil.readOptionalString("File path (for simulation): ");

        try {
            Song song = new Song(title, session.getCurrentArtistId(), genre, duration);
            song.setReleaseDate(releaseDate);
            song.setAlbumId(albumId);
            song.setFilePath(
                    filePath.isEmpty() ? "/music/" + title.toLowerCase().replace(" ", "_") + ".mp3" : filePath);

            int songId = songService.createSong(song);
            logger.info("Song uploaded: {} by artist {}", title, session.getCurrentArtistId());
            ConsoleUtil.printSuccess("Song '" + title + "' uploaded successfully! (ID: " + songId + ")");

        } catch (Exception e) {
            logger.error("Error uploading song", e);
            ConsoleUtil.printError("Failed to upload song: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void viewMySongs() {
        try {
            List<Song> songs = songService.getSongsByArtist(session.getCurrentArtistId());
            ConsoleUtil.printSectionHeader("My Songs (" + songs.size() + ")");

            if (songs.isEmpty()) {
                ConsoleUtil.printInfo("You haven't uploaded any songs yet.");
                ConsoleUtil.pressEnterToContinue();
                return;
            }

            displaySongsWithStats(songs);

        } catch (Exception e) {
            ConsoleUtil.printError("Error: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void editSong() {
        try {
            List<Song> songs = songService.getSongsByArtist(session.getCurrentArtistId());
            if (songs.isEmpty()) {
                ConsoleUtil.printInfo("No songs to edit.");
                ConsoleUtil.pressEnterToContinue();
                return;
            }

            ConsoleUtil.printSectionHeader("Edit Song");
            displaySongs(songs);

            int songIndex = ConsoleUtil.readInt("Select song to edit: ", 1, songs.size());
            Song song = songs.get(songIndex - 1);

            System.out.println("\n(Press Enter to keep current value)\n");

            String newTitle = ConsoleUtil.readOptionalString("Title [" + song.getTitle() + "]: ");
            if (!newTitle.isEmpty())
                song.setTitle(newTitle);

            String newGenre = ConsoleUtil.readOptionalString("Genre [" + song.getGenre() + "]: ");
            if (!newGenre.isEmpty())
                song.setGenre(newGenre);

            if (ConsoleUtil.readConfirmation("Change album assignment?")) {
                List<Album> albums = artistService.getAlbumsByArtist(session.getCurrentArtistId());
                if (!albums.isEmpty()) {
                    System.out.println("  [0] No album (single)");
                    displayAlbums(albums);
                    int albumChoice = ConsoleUtil.readInt("Select album (0 for none): ", 0, albums.size());
                    if (albumChoice == 0) {
                        song.setAlbumId(null);
                    } else {
                        song.setAlbumId(albums.get(albumChoice - 1).getAlbumId());
                    }
                }
            }

            boolean success = songService.updateSong(song);
            if (success) {
                logger.info("Song updated: {}", song.getTitle());
                ConsoleUtil.printSuccess("Song updated successfully!");
            } else {
                ConsoleUtil.printError("Failed to update song.");
            }

        } catch (Exception e) {
            ConsoleUtil.printError("Error: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void deleteSong() {
        try {
            List<Song> songs = songService.getSongsByArtist(session.getCurrentArtistId());
            if (songs.isEmpty()) {
                ConsoleUtil.printInfo("No songs to delete.");
                ConsoleUtil.pressEnterToContinue();
                return;
            }

            ConsoleUtil.printSectionHeader("Delete Song");
            displaySongs(songs);

            int songIndex = ConsoleUtil.readInt("Select song to delete: ", 1, songs.size());
            Song song = songs.get(songIndex - 1);

            if (ConsoleUtil.readConfirmation("Delete '" + song.getTitle() + "'? This cannot be undone.")) {
                boolean success = songService.deleteSong(song.getSongId());
                if (success) {
                    logger.info("Song deleted: {}", song.getTitle());
                    ConsoleUtil.printSuccess("Song deleted successfully!");
                } else {
                    ConsoleUtil.printError("Failed to delete song.");
                }
            }

        } catch (Exception e) {
            ConsoleUtil.printError("Error: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void createAlbum() {
        ConsoleUtil.printSectionHeader("Create New Album");

        String title = ConsoleUtil.readNonEmptyString("Album Title: ");
        String description = ConsoleUtil.readOptionalString("Description: ");
        String genre = ConsoleUtil.readOptionalString("Genre: ");

        String releaseDateStr = ConsoleUtil.readOptionalString("Release Date (YYYY-MM-DD) [today]: ");
        Date releaseDate;
        if (releaseDateStr.isEmpty()) {
            releaseDate = new Date(System.currentTimeMillis());
        } else {
            try {
                releaseDate = Date.valueOf(releaseDateStr);
            } catch (Exception e) {
                releaseDate = new Date(System.currentTimeMillis());
            }
        }

        try {
            Album album = new Album();
            album.setArtistId(session.getCurrentArtistId());
            album.setTitle(title);
            album.setDescription(description);
            album.setGenre(genre);
            album.setReleaseDate(releaseDate);

            int albumId = artistService.createAlbum(album);
            logger.info("Album created: {} by artist {}", title, session.getCurrentArtistId());
            ConsoleUtil.printSuccess("Album '" + title + "' created! (ID: " + albumId + ")");

        } catch (Exception e) {
            ConsoleUtil.printError("Error: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void viewMyAlbums() {
        try {
            List<Album> albums = artistService.getAlbumsByArtist(session.getCurrentArtistId());
            ConsoleUtil.printSectionHeader("My Albums (" + albums.size() + ")");

            if (albums.isEmpty()) {
                ConsoleUtil.printInfo("You haven't created any albums yet.");
                ConsoleUtil.pressEnterToContinue();
                return;
            }

            displayAlbums(albums);

            if (ConsoleUtil.readConfirmation("View album songs?")) {
                int idx = ConsoleUtil.readInt("Select album: ", 1, albums.size());
                Album album = albums.get(idx - 1);
                List<Song> songs = songService.getSongsByAlbum(album.getAlbumId());

                ConsoleUtil.printSectionHeader("Songs in '" + album.getTitle() + "'");
                if (songs.isEmpty()) {
                    ConsoleUtil.printInfo("No songs in this album yet.");
                } else {
                    displaySongs(songs);
                }
            }

        } catch (Exception e) {
            ConsoleUtil.printError("Error: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void editAlbum() {
        try {
            List<Album> albums = artistService.getAlbumsByArtist(session.getCurrentArtistId());
            if (albums.isEmpty()) {
                ConsoleUtil.printInfo("No albums to edit.");
                ConsoleUtil.pressEnterToContinue();
                return;
            }

            ConsoleUtil.printSectionHeader("Edit Album");
            displayAlbums(albums);

            int albumIndex = ConsoleUtil.readInt("Select album to edit: ", 1, albums.size());
            Album album = albums.get(albumIndex - 1);

            System.out.println("\nCurrent details:");
            System.out.println("  Title: " + album.getTitle());
            System.out.println("  Description: " + (album.getDescription() != null ? album.getDescription() : "None"));
            System.out.println("  Genre: " + (album.getGenre() != null ? album.getGenre() : "None"));
            System.out.println("  Release Date: " + (album.getReleaseDate() != null ? album.getReleaseDate() : "N/A"));

            System.out.println("\n(Press Enter to keep current value)\n");

            String newTitle = ConsoleUtil.readOptionalString("Title [" + album.getTitle() + "]: ");
            if (!newTitle.isEmpty())
                album.setTitle(newTitle);

            String newDesc = ConsoleUtil.readOptionalString("Description: ");
            if (!newDesc.isEmpty())
                album.setDescription(newDesc);

            String newGenre = ConsoleUtil.readOptionalString("Genre: ");
            if (!newGenre.isEmpty())
                album.setGenre(newGenre);

            String newDateStr = ConsoleUtil.readOptionalString("Release Date (YYYY-MM-DD): ");
            if (!newDateStr.isEmpty()) {
                try {
                    album.setReleaseDate(Date.valueOf(newDateStr));
                } catch (Exception ignored) {
                }
            }

            boolean success = artistService.updateAlbum(album);
            if (success) {
                logger.info("Album updated: {}", album.getTitle());
                ConsoleUtil.printSuccess("Album updated successfully!");
            } else {
                ConsoleUtil.printError("Failed to update album.");
            }

        } catch (Exception e) {
            ConsoleUtil.printError("Error: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void deleteAlbum() {
        try {
            List<Album> albums = artistService.getAlbumsByArtist(session.getCurrentArtistId());
            if (albums.isEmpty()) {
                ConsoleUtil.printInfo("No albums to delete.");
                ConsoleUtil.pressEnterToContinue();
                return;
            }

            ConsoleUtil.printSectionHeader("Delete Album");
            displayAlbums(albums);

            int albumIndex = ConsoleUtil.readInt("Select album to delete: ", 1, albums.size());
            Album album = albums.get(albumIndex - 1);

            ConsoleUtil.printWarning("Deleting an album will NOT delete its songs (they become singles).");
            if (ConsoleUtil.readConfirmation("Delete album '" + album.getTitle() + "'?")) {
                boolean success = artistService.deleteAlbum(album.getAlbumId());
                if (success) {
                    logger.info("Album deleted: {}", album.getTitle());
                    ConsoleUtil.printSuccess("Album deleted successfully!");
                } else {
                    ConsoleUtil.printError("Failed to delete album.");
                }
            }

        } catch (Exception e) {
            ConsoleUtil.printError("Error: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void viewStatistics() {
        try {
            ConsoleUtil.printSectionHeader("My Statistics");

            List<Song> songs = songService.getSongsByArtist(session.getCurrentArtistId());
            List<Album> albums = artistService.getAlbumsByArtist(session.getCurrentArtistId());

            int totalPlays = songs.stream().mapToInt(Song::getPlayCount).sum();
            int totalSongs = songs.size();
            int totalAlbums = albums.size();

            int totalFavorites = 0;
            for (Song song : songs) {
                totalFavorites += favoriteDAO.getUsersWhoFavorited(song.getSongId()).size();
            }

            System.out.println("  Total Songs: " + totalSongs);
            System.out.println("  Total Albums: " + totalAlbums);
            System.out.println("  Total Plays: " + totalPlays);
            System.out.println("  Total Favorites: " + totalFavorites);

            if (!songs.isEmpty()) {
                Song topSong = songs.stream()
                        .max((a, b) -> Integer.compare(a.getPlayCount(), b.getPlayCount()))
                        .orElse(null);
                if (topSong != null && topSong.getPlayCount() > 0) {
                    System.out.println("\n  Most Popular Song:");
                    System.out.println("    " + topSong.getTitle() + " (" + topSong.getPlayCount() + " plays)");
                }

                System.out.println("\n  Song Statistics:");
                displaySongsWithStats(songs);
            }

        } catch (Exception e) {
            ConsoleUtil.printError("Error: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void seeWhoFavoritedMySongs() {
        try {
            List<Song> songs = songService.getSongsByArtist(session.getCurrentArtistId());
            if (songs.isEmpty()) {
                ConsoleUtil.printInfo("You don't have any songs yet.");
                ConsoleUtil.pressEnterToContinue();
                return;
            }

            ConsoleUtil.printSectionHeader("Who Favorited My Songs");
            displaySongs(songs);

            int songIndex = ConsoleUtil.readInt("Select a song to see who favorited it: ", 1, songs.size());
            Song selectedSong = songs.get(songIndex - 1);

            List<Integer> userIds = favoriteDAO.getUsersWhoFavorited(selectedSong.getSongId());

            ConsoleUtil.printSectionHeader("Users who favorited '" + selectedSong.getTitle() + "'");

            if (userIds.isEmpty()) {
                ConsoleUtil.printInfo("No users have favorited this song yet.");
            } else {
                System.out.println("  Total: " + userIds.size() + " user(s)\n");
                System.out.printf("  %-4s %-25s %-30s%n", "#", "Username", "Email");
                ConsoleUtil.printDivider();

                int count = 0;
                for (Integer userId : userIds) {
                    try {
                        Optional<User> userOpt = userDAO.findById(userId);
                        if (userOpt.isPresent()) {
                            User user = userOpt.get();
                            count++;
                            System.out.printf("  %-4d %-25s %-30s%n",
                                    count,
                                    DisplayHelper.truncate(user.getUsername(), 23),
                                    DisplayHelper.truncate(user.getEmail(), 28));
                        }
                    } catch (Exception ignored) {
                    }
                }
            }

        } catch (Exception e) {
            ConsoleUtil.printError("Error: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void displaySongs(List<Song> songs) {
        DisplayHelper.displaySongs(songs);
    }

    private void displaySongsWithStats(List<Song> songs) {
        System.out.printf("  %-4s %-26s %-12s %-8s %-8s %-8s%n", "#", "Title", "Genre", "Duration", "Plays", "Favs");
        ConsoleUtil.printDivider();
        for (int i = 0; i < songs.size(); i++) {
            Song s = songs.get(i);
            int favCount = 0;
            try {
                favCount = favoriteDAO.getUsersWhoFavorited(s.getSongId()).size();
            } catch (Exception ignored) {
            }
            System.out.printf("  %-4d %-26s %-12s %-8s %-8d %-8d%n",
                    i + 1, DisplayHelper.truncate(s.getTitle(), 24),
                    DisplayHelper.truncate(s.getGenre(), 10), s.getFormattedDuration(), s.getPlayCount(), favCount);
        }
    }

    private void displayAlbums(List<Album> albums) {
        DisplayHelper.displayAlbums(albums);
    }

}
