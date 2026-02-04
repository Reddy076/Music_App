package com.revplay.ui;

import com.revplay.model.Album;
import com.revplay.model.Artist;
import com.revplay.model.Playlist;
import com.revplay.model.Song;

import com.revplay.service.ArtistService;
import com.revplay.service.PlaylistService;
import com.revplay.service.SongService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class SearchMenu extends BaseMenu {
    private static final Logger logger = LogManager.getLogger(SearchMenu.class);
    private final SongService songService;
    private final ArtistService artistService;
    private final PlaylistService playlistService;
    private final com.revplay.service.AlbumService albumService;
    private final com.revplay.service.PodcastService podcastService;

    public SearchMenu() {
        super();
        this.songService = new SongService();
        this.artistService = new ArtistService();
        this.playlistService = new PlaylistService();
        this.albumService = new com.revplay.service.AlbumService();
        this.podcastService = new com.revplay.service.PodcastService();
    }

    public void display() {
        boolean running = true;
        while (running) {
            ConsoleUtil.printSectionHeader("Browse & Search Music");

            String[] options = {
                    "Search All (Songs, Artists, Playlists, Albums, Podcasts)",
                    "Browse by Genre",
                    "Browse by Artist",
                    "Browse by Album",
                    "View Popular Songs",
                    "View Recent Songs",
                    "View All Artists",
                    "View All Albums",
                    "View Public Playlists",
                    "Back to Main Menu"
            };
            ConsoleUtil.printMenu(options);

            int choice = ConsoleUtil.readInt("Enter your choice: ", 1, 10);

            switch (choice) {
                case 1 -> searchAll();
                case 2 -> browseByGenre();
                case 3 -> browseByArtist();
                case 4 -> browseByAlbum();
                case 5 -> viewPopularSongs();
                case 6 -> viewRecentSongs();
                case 7 -> viewAllArtists();
                case 8 -> viewAllAlbums();
                case 9 -> viewPublicPlaylists();
                case 10 -> running = false;
            }
        }
    }

    private void searchAll() {
        String keyword = ConsoleUtil.readNonEmptyString("Enter search keyword: ");

        try {
            List<Song> songs = songService.searchSongs(keyword);
            ConsoleUtil.printSectionHeader("Songs (" + songs.size() + " results)");
            DisplayHelper.displaySongs(songs);

            List<Artist> artists = artistService.searchArtists(keyword);
            ConsoleUtil.printSectionHeader("Artists (" + artists.size() + " results)");
            DisplayHelper.displayArtists(artists);

            List<Playlist> playlists = playlistService.searchPlaylists(keyword);
            ConsoleUtil.printSectionHeader("Playlists (" + playlists.size() + " results)");
            DisplayHelper.displayPlaylists(playlists);

            List<Album> albums = albumService.searchAlbums(keyword);
            ConsoleUtil.printSectionHeader("Albums (" + albums.size() + " results)");
            DisplayHelper.displayAlbums(albums);

            List<com.revplay.model.Podcast> podcasts = podcastService.searchPodcasts(keyword);
            ConsoleUtil.printSectionHeader("Podcasts (" + podcasts.size() + " results)");
            if (podcasts.isEmpty()) {
                ConsoleUtil.printInfo("No podcasts found matching '" + keyword + "'");
            } else {
                for (int i = 0; i < podcasts.size(); i++) {
                    System.out.printf("  [%d] %s%n", i + 1, podcasts.get(i).getTitle());
                }
            }

            if (!songs.isEmpty()) {
                if (ConsoleUtil.readConfirmation("Would you like to play a song?")) {
                    int songIndex = ConsoleUtil.readInt("Enter song number: ", 1, songs.size());
                    Song selectedSong = songs.get(songIndex - 1);
                    new PlayerMenu().playSong(selectedSong);
                    return;
                }
            }

        } catch (Exception e) {
            logger.error("Search error", e);
            ConsoleUtil.printError("Search failed: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void browseByGenre() {
        try {
            List<String> genres = songService.getAllGenres();
            if (genres.isEmpty()) {
                ConsoleUtil.printInfo("No genres found.");
                ConsoleUtil.pressEnterToContinue();
                return;
            }

            ConsoleUtil.printSectionHeader("Available Genres");
            for (int i = 0; i < genres.size(); i++) {
                System.out.printf("  [%d] %s%n", i + 1, genres.get(i));
            }
            System.out.printf("  [%d] Back%n", genres.size() + 1);

            int choice = ConsoleUtil.readInt("Select a genre: ", 1, genres.size() + 1);
            if (choice == genres.size() + 1) {
                return;
            }

            String selectedGenre = genres.get(choice - 1);
            List<Song> songs = songService.getSongsByGenre(selectedGenre);

            ConsoleUtil.printSectionHeader("Songs in " + selectedGenre);
            DisplayHelper.displaySongs(songs);

            if (!songs.isEmpty() && ConsoleUtil.readConfirmation("Would you like to play a song?")) {
                int songIndex = ConsoleUtil.readInt("Enter song number: ", 1, songs.size());
                Song selectedSong = songs.get(songIndex - 1);
                new PlayerMenu().playSong(selectedSong);
                return;
            }

        } catch (Exception e) {
            logger.error("Error browsing genres", e);
            ConsoleUtil.printError("Failed to load genres: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void browseByArtist() {
        try {
            List<Artist> artists = artistService.getAllArtists();
            if (artists.isEmpty()) {
                ConsoleUtil.printInfo("No artists found.");
                ConsoleUtil.pressEnterToContinue();
                return;
            }

            ConsoleUtil.printSectionHeader("Browse by Artist");
            DisplayHelper.displayArtists(artists);
            System.out.printf("  [%d] Back%n", artists.size() + 1);

            int choice = ConsoleUtil.readInt("Select an artist: ", 1, artists.size() + 1);
            if (choice == artists.size() + 1) {
                return;
            }

            Artist selectedArtist = artists.get(choice - 1);
            viewArtistContent(selectedArtist);

        } catch (Exception e) {
            logger.error("Error browsing artists", e);
            ConsoleUtil.printError("Failed to load artists: " + e.getMessage());
        }
    }

    private void viewArtistContent(Artist artist) {
        try {
            ConsoleUtil.printSectionHeader("Artist: " + artist.getArtistName());
            System.out.println("  Genre: " + (artist.getGenre() != null ? artist.getGenre() : "N/A"));
            if (artist.getBio() != null && !artist.getBio().isEmpty()) {
                System.out.println("  Bio: " + artist.getBio());
            }
            System.out.println();

            List<Song> songs = songService.getSongsByArtist(artist.getArtistId());
            System.out.println("  Songs (" + songs.size() + "):");
            DisplayHelper.displaySongs(songs);

            List<Album> albums = artistService.getAlbumsByArtist(artist.getArtistId());
            if (!albums.isEmpty()) {
                System.out.println("\n  Albums (" + albums.size() + "):");
                DisplayHelper.displayAlbums(albums);
            }

            if (!songs.isEmpty() && ConsoleUtil.readConfirmation("Would you like to play a song?")) {
                int songIndex = ConsoleUtil.readInt("Enter song number: ", 1, songs.size());
                Song selectedSong = songs.get(songIndex - 1);
                new PlayerMenu().playSong(selectedSong);
                return;
            }

        } catch (Exception e) {
            logger.error("Error loading artist content", e);
            ConsoleUtil.printError("Failed to load artist content: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void browseByAlbum() {
        try {
            List<Album> albums = albumService.getAllAlbums();

            if (albums.isEmpty()) {
                ConsoleUtil.printInfo("No albums found.");
                return;
            }

            DisplayHelper.displayAlbums(albums);
            System.out.printf("  [%d] Back%n", albums.size() + 1);

            int choice = ConsoleUtil.readInt("Select an album: ", 1, albums.size() + 1);
            if (choice == albums.size() + 1) {
                return;
            }

            Album selectedAlbum = albums.get(choice - 1);
            viewAlbumContent(selectedAlbum);

        } catch (Exception e) {
            logger.error("Error browsing albums", e);
            ConsoleUtil.printError("Failed to load albums: " + e.getMessage());
        }
    }

    private void viewAlbumContent(Album album) {
        try {
            ConsoleUtil.printSectionHeader("Album: " + album.getTitle());
            System.out.println("  Artist: " + (album.getArtistName() != null ? album.getArtistName() : "Unknown"));
            System.out.println("  Genre: " + (album.getGenre() != null ? album.getGenre() : "N/A"));
            System.out.println("  Release Date: " + (album.getReleaseDate() != null ? album.getReleaseDate() : "N/A"));
            if (album.getDescription() != null && !album.getDescription().isEmpty()) {
                System.out.println("  Description: " + album.getDescription());
            }
            System.out.println();

            List<Song> songs = songService.getSongsByAlbum(album.getAlbumId());
            System.out.println("  Songs (" + songs.size() + "):");
            DisplayHelper.displaySongs(songs);

            if (!songs.isEmpty() && ConsoleUtil.readConfirmation("Would you like to play a song?")) {
                int songIndex = ConsoleUtil.readInt("Enter song number: ", 1, songs.size());
                Song selectedSong = songs.get(songIndex - 1);
                new PlayerMenu().playSong(selectedSong);
                return;
            }

        } catch (Exception e) {
            logger.error("Error loading album content", e);
            ConsoleUtil.printError("Failed to load album content: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void viewPopularSongs() {
        try {
            List<Song> songs = songService.getPopularSongs(20);
            ConsoleUtil.printSectionHeader("Popular Songs (Top 20)");
            DisplayHelper.displaySongsWithPlayCount(songs);

            if (!songs.isEmpty() && ConsoleUtil.readConfirmation("Would you like to play a song?")) {
                int songIndex = ConsoleUtil.readInt("Enter song number: ", 1, songs.size());
                Song selectedSong = songs.get(songIndex - 1);
                new PlayerMenu().playSong(selectedSong);
                return;
            }

        } catch (Exception e) {
            logger.error("Error loading popular songs", e);
            ConsoleUtil.printError("Failed to load popular songs: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void viewRecentSongs() {
        try {
            List<Song> songs = songService.getRecentSongs(20);
            ConsoleUtil.printSectionHeader("Recently Added Songs");
            DisplayHelper.displaySongs(songs);

            if (!songs.isEmpty() && ConsoleUtil.readConfirmation("Would you like to play a song?")) {
                int songIndex = ConsoleUtil.readInt("Enter song number: ", 1, songs.size());
                Song selectedSong = songs.get(songIndex - 1);
                new PlayerMenu().playSong(selectedSong);
                return;
            }

        } catch (Exception e) {
            logger.error("Error loading recent songs", e);
            ConsoleUtil.printError("Failed to load recent songs: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void viewAllArtists() {
        try {
            List<Artist> artists = artistService.getAllArtists();
            ConsoleUtil.printSectionHeader("All Artists");
            DisplayHelper.displayArtists(artists);

            if (!artists.isEmpty()) {
                if (ConsoleUtil.readConfirmation("View an artist's content?")) {
                    int artistIndex = ConsoleUtil.readInt("Enter artist number: ", 1, artists.size());
                    Artist selectedArtist = artists.get(artistIndex - 1);
                    viewArtistContent(selectedArtist);
                    return;
                }
            }

        } catch (Exception e) {
            logger.error("Error loading artists", e);
            ConsoleUtil.printError("Failed to load artists: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void viewAllAlbums() {
        try {
            List<Album> albums = albumService.getAllAlbums();
            ConsoleUtil.printSectionHeader("All Albums");
            DisplayHelper.displayAlbums(albums);

            if (!albums.isEmpty()) {
                if (ConsoleUtil.readConfirmation("View an album's songs?")) {
                    int albumIndex = ConsoleUtil.readInt("Enter album number: ", 1, albums.size());
                    Album selectedAlbum = albums.get(albumIndex - 1);
                    viewAlbumContent(selectedAlbum);
                    return;
                }
            }

        } catch (Exception e) {
            logger.error("Error loading albums", e);
            ConsoleUtil.printError("Failed to load albums: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void viewPublicPlaylists() {
        try {
            List<Playlist> playlists = playlistService.getPublicPlaylists();
            ConsoleUtil.printSectionHeader("Public Playlists");
            DisplayHelper.displayPlaylists(playlists);

            if (!playlists.isEmpty()) {
                if (ConsoleUtil.readConfirmation("View a playlist?")) {
                    int playlistIndex = ConsoleUtil.readInt("Enter playlist number: ", 1, playlists.size());
                    Playlist selectedPlaylist = playlists.get(playlistIndex - 1);
                    viewPlaylistSongs(selectedPlaylist);
                    return;
                }
            }

        } catch (Exception e) {
            logger.error("Error loading public playlists", e);
            ConsoleUtil.printError("Failed to load playlists: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void viewPlaylistSongs(Playlist playlist) {
        try {
            List<Song> songs = playlistService.getPlaylistSongs(playlist.getPlaylistId());
            ConsoleUtil.printSectionHeader("Playlist: " + playlist.getName());
            System.out.println("  By: " + playlist.getUsername());
            if (playlist.getDescription() != null) {
                System.out.println("  Description: " + playlist.getDescription());
            }
            System.out.println("  Total songs: " + songs.size());
            System.out.println();
            DisplayHelper.displaySongs(songs);

            if (!songs.isEmpty() && ConsoleUtil.readConfirmation("Would you like to play a song?")) {
                int songIndex = ConsoleUtil.readInt("Enter song number: ", 1, songs.size());
                Song selectedSong = songs.get(songIndex - 1);
                new PlayerMenu().playSong(selectedSong);
                return;
            }

        } catch (Exception e) {
            logger.error("Error loading playlist songs", e);
            ConsoleUtil.printError("Failed to load playlist: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }
}
