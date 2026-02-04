package com.revplay.ui;

import com.revplay.model.ListeningHistory;
import com.revplay.model.Song;
import com.revplay.service.HistoryService;
import com.revplay.service.SongService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class HistoryMenu extends BaseMenu {
    private static final Logger logger = LogManager.getLogger(HistoryMenu.class);
    private final HistoryService historyService;
    private final SongService songService;

    public HistoryMenu() {
        super();
        this.historyService = new HistoryService();
        this.songService = new SongService();
    }

    public void display() {
        boolean running = true;
        while (running) {
            ConsoleUtil.printSectionHeader("Listening History");

            String[] options = {
                    "View Recently Played",
                    "View Full History",
                    "View Listening Stats",
                    "Clear History",
                    "Back to Main Menu"
            };
            ConsoleUtil.printMenu(options);

            int choice = ConsoleUtil.readInt("Enter your choice: ", 1, 5);

            switch (choice) {
                case 1 -> viewRecentlyPlayed();
                case 2 -> viewFullHistory();
                case 3 -> viewListeningStats();
                case 4 -> clearHistory();
                case 5 -> running = false;
            }
        }
    }

    private void viewRecentlyPlayed() {
        try {
            List<Song> recentSongs = songService.getRecentlyPlayed(session.getCurrentUserId(), 20);

            ConsoleUtil.printSectionHeader("Recently Played Songs");

            if (recentSongs.isEmpty()) {
                ConsoleUtil.printInfo("No listening history yet. Start playing some music!");
                ConsoleUtil.pressEnterToContinue();
                return;
            }

            displaySongs(recentSongs);

            if (ConsoleUtil.readConfirmation("Would you like to play a song again?")) {
                int songIndex = ConsoleUtil.readInt("Enter song number: ", 1, recentSongs.size());
                Song selectedSong = recentSongs.get(songIndex - 1);
                new PlayerMenu().playSong(selectedSong);
                return;
            }

        } catch (Exception e) {
            logger.error("Error loading recently played", e);
            ConsoleUtil.printError("Failed to load history: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void viewFullHistory() {
        try {
            List<ListeningHistory> history = historyService.getHistory(session.getCurrentUserId(), 50);

            ConsoleUtil.printSectionHeader("Full Listening History");

            if (history.isEmpty()) {
                ConsoleUtil.printInfo("No listening history yet.");
                ConsoleUtil.pressEnterToContinue();
                return;
            }

            System.out.printf("  %-4s %-30s %-20s %-15s%n", "#", "Song", "Artist", "Played At");
            ConsoleUtil.printDivider();

            for (int i = 0; i < history.size(); i++) {
                ListeningHistory h = history.get(i);
                try {
                    var songOpt = songService.getSongById(h.getSongId());
                    if (songOpt.isPresent()) {
                        Song s = songOpt.get();
                        System.out.printf("  %-4d %-30s %-20s %-15s%n",
                                i + 1,
                                DisplayHelper.truncate(s.getTitle(), 28),
                                DisplayHelper.truncate(s.getArtistName() != null ? s.getArtistName() : "Unknown", 18),
                                formatTimestamp(h.getPlayedAt()));
                    }
                } catch (Exception ignored) {
                }
            }

        } catch (Exception e) {
            logger.error("Error loading full history", e);
            ConsoleUtil.printError("Failed to load history: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void viewListeningStats() {
        try {
            ConsoleUtil.printSectionHeader("Your Listening Stats");

            int totalTime = historyService.getTotalListeningTime(session.getCurrentUserId());
            List<Song> recentSongs = songService.getRecentlyPlayed(session.getCurrentUserId(), 100);

            System.out.println("  Total listening time: " + formatDuration(totalTime));
            System.out.println("  Songs played: " + recentSongs.size());

            if (!recentSongs.isEmpty()) {
                java.util.Map<String, Integer> genreCount = new java.util.HashMap<>();
                for (Song s : recentSongs) {
                    if (s.getGenre() != null) {
                        genreCount.merge(s.getGenre(), 1, Integer::sum);
                    }
                }

                if (!genreCount.isEmpty()) {
                    String topGenre = genreCount.entrySet().stream()
                            .max(java.util.Map.Entry.comparingByValue())
                            .map(java.util.Map.Entry::getKey)
                            .orElse("Unknown");
                    System.out.println("  Favorite genre: " + topGenre);
                }
            }

        } catch (Exception e) {
            logger.error("Error loading stats", e);
            ConsoleUtil.printError("Failed to load stats: " + e.getMessage());
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void clearHistory() {
        if (ConsoleUtil.readConfirmation("Are you sure you want to clear your listening history?")) {
            try {
                boolean success = historyService.clearHistory(session.getCurrentUserId());
                if (success) {
                    logger.info("History cleared for user: {}", session.getCurrentUserId());
                    ConsoleUtil.printSuccess("Listening history cleared!");
                } else {
                    ConsoleUtil.printInfo("History is already empty.");
                }
            } catch (Exception e) {
                logger.error("Error clearing history", e);
                ConsoleUtil.printError("Failed to clear history: " + e.getMessage());
            }
        }

        ConsoleUtil.pressEnterToContinue();
    }

    private void displaySongs(List<Song> songs) {
        DisplayHelper.displaySongs(songs);
    }

    private String formatTimestamp(java.sql.Timestamp ts) {
        if (ts == null)
            return "Unknown";
        return new java.text.SimpleDateFormat("MM/dd HH:mm").format(ts);
    }

    private String formatDuration(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        if (hours > 0) {
            return hours + " hr " + minutes + " min";
        }
        return minutes + " min";
    }
}
