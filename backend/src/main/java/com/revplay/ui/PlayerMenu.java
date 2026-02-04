package com.revplay.ui;

import com.revplay.model.Song;
import com.revplay.service.FavoriteService;
import com.revplay.service.SongService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerMenu extends BaseMenu {
  private static final Logger logger = LogManager.getLogger(PlayerMenu.class);
  private final SongService songService;
  private final FavoriteService favoriteService;

  private List<Song> queue = new ArrayList<>();
  private int currentIndex = 0;
  private boolean isPlaying = false;
  private boolean isShuffled = false;
  private RepeatMode repeatMode = RepeatMode.OFF;
  private int currentPosition = 0;

  private enum RepeatMode {
    OFF, ONE, ALL
  }

  public PlayerMenu() {
    super();
    this.songService = new SongService();
    this.favoriteService = new FavoriteService();
  }

  public void display() {
    boolean running = true;
    while (running) {
      displayPlayerStatus();

      String[] options = {
          isPlaying ? "[||] Pause" : "[>] Play",
          "[<<] Previous",
          "[>>] Next",
          "[~] Shuffle: " + (isShuffled ? "ON" : "OFF"),
          "[R] Repeat: " + repeatMode,
          "[+] Add to Favorites",
          "[<-] Back to Main Menu"
      };
      ConsoleUtil.printMenu(options);

      int choice = ConsoleUtil.readInt("Enter your choice: ", 1, 7);

      switch (choice) {
        case 1 -> togglePlayPause();
        case 2 -> previousSong();
        case 3 -> nextSong();
        case 4 -> toggleShuffle();
        case 5 -> toggleRepeat();
        case 6 -> addCurrentToFavorites();
        case 7 -> {
          isPlaying = false;
          running = false;
        }
      }
    }
  }

  public void playSong(Song song) {
    queue.add(0, song);
    currentIndex = 0;
    currentPosition = 0;
    isPlaying = true;

    try {
      songService.playSong(session.getCurrentUserId(), song.getSongId(), song.getDuration());
    } catch (Exception e) {
      logger.warn("Failed to record play: {}", e.getMessage());
    }

    ConsoleUtil.printSuccess("Now playing: "
        + song.getTitle() + " by " + (song.getArtistName() != null ? song.getArtistName() : "Unknown"));
    display();
  }

  public void playQueue(List<Song> songs) {
    if (songs.isEmpty()) {
      ConsoleUtil.printInfo("No songs to play.");
      return;
    }
    queue = new ArrayList<>(songs);
    currentIndex = 0;
    currentPosition = 0;
    isPlaying = true;

    Song currentSong = queue.get(currentIndex);
    try {
      songService.playSong(session.getCurrentUserId(), currentSong.getSongId(), currentSong.getDuration());
    } catch (Exception e) {
      logger.warn("Failed to record play: {}", e.getMessage());
    }

    ConsoleUtil.printSuccess("Now playing: " + currentSong.getTitle());
    display();
  }

  private void displayPlayerStatus() {
    ConsoleUtil.printSectionHeader("Music Player ");

    if (queue.isEmpty() || currentIndex >= queue.size()) {
      System.out.println("\n  [X] No song playing");
      System.out.println("  Add songs to your queue to start listening!\n");
      return;
    }

    Song current = queue.get(currentIndex);
    String status = isPlaying ? "[>] Playing" : "[||] Paused";

    System.out.println();
    System.out.println("  " + status + " ");
    System.out.println("  ---------------------------------------------");
    System.out.println("  Song:   " + current.getTitle());
    System.out.println("  Artist: "
        + (current.getArtistName() != null ? current.getArtistName() : "Unknown Artist"));
    System.out.println("  Genre:  "
        + (current.getGenre() != null ? current.getGenre() : "Unknown Genre"));
    System.out.println();

    String progressBar = ConsoleUtil.progressBar(currentPosition, current.getDuration(), 40);
    System.out.println("  [" + progressBar + "]");
    System.out.println("  " + formatTime(currentPosition) + " / " + current.getFormattedDuration());
    System.out.println();
    System.out.println("  Queue: " + (currentIndex + 1) + " of " + queue.size() + " songs");

    try {
      boolean isFav = favoriteService.isFavorite(session.getCurrentUserId(), current.getSongId());
      if (isFav) {
        System.out.println("  [*] In your favorites");
      }
    } catch (Exception ignored) {
    }

    System.out.println();
  }

  private void togglePlayPause() {
    if (queue.isEmpty()) {
      ConsoleUtil.printInfo("No songs in queue. Add some songs first!");
      ConsoleUtil.pressEnterToContinue();
      return;
    }

    isPlaying = !isPlaying;
    if (isPlaying) {
      currentPosition = Math.min(currentPosition + 30, queue.get(currentIndex).getDuration());
      ConsoleUtil.printSuccess("Playing: " + queue.get(currentIndex).getTitle());
    } else {
      ConsoleUtil.printInfo("Paused");
    }
  }

  private void previousSong() {
    if (queue.isEmpty()) {
      ConsoleUtil.printInfo("No songs in queue.");
      ConsoleUtil.pressEnterToContinue();
      return;
    }

    if (currentPosition > 5) {
      currentPosition = 0;
      ConsoleUtil.printInfo("Restarting: " + queue.get(currentIndex).getTitle());
    } else {
      if (currentIndex > 0) {
        currentIndex--;
      } else if (repeatMode == RepeatMode.ALL) {
        currentIndex = queue.size() - 1;
      }
      currentPosition = 0;
      isPlaying = true;
      Song song = queue.get(currentIndex);
      recordPlay();
      ConsoleUtil.printSuccess("Previous: " + song.getTitle());
    }
  }

  private void nextSong() {
    if (queue.isEmpty()) {
      ConsoleUtil.printInfo("No songs in queue.");
      ConsoleUtil.pressEnterToContinue();
      return;
    }

    if (repeatMode == RepeatMode.ONE) {
      currentPosition = 0;
      ConsoleUtil.printInfo("Repeating: " + queue.get(currentIndex).getTitle());
    } else if (currentIndex < queue.size() - 1) {
      currentIndex++;
      currentPosition = 0;
      isPlaying = true;
      Song song = queue.get(currentIndex);
      recordPlay();
      ConsoleUtil.printSuccess("Next: " + song.getTitle());
    } else if (repeatMode == RepeatMode.ALL) {
      currentIndex = 0;
      currentPosition = 0;
      isPlaying = true;
      Song song = queue.get(currentIndex);
      recordPlay();
      ConsoleUtil.printSuccess("Starting over: " + song.getTitle());
    } else {
      ConsoleUtil.printInfo("End of queue reached.");
      isPlaying = false;
    }
  }

  private void toggleShuffle() {
    isShuffled = !isShuffled;
    if (isShuffled && queue.size() > 1) {
      Song current = queue.get(currentIndex);
      Collections.shuffle(queue);
      queue.remove(current);
      queue.add(0, current);
      currentIndex = 0;
      ConsoleUtil.printSuccess("Shuffle ON - Queue shuffled!");
    } else {
      ConsoleUtil.printInfo("Shuffle " + (isShuffled ? "ON" : "OFF"));
    }
  }

  private void toggleRepeat() {
    repeatMode = switch (repeatMode) {
      case OFF -> RepeatMode.ALL;
      case ALL -> RepeatMode.ONE;
      case ONE -> RepeatMode.OFF;
    };

    String message = switch (repeatMode) {
      case OFF -> "Repeat OFF";
      case ALL -> "Repeat ALL - Queue will loop";
      case ONE -> "Repeat ONE - Current song will loop";
    };
    ConsoleUtil.printInfo(message);
  }

  private void addCurrentToFavorites() {
    if (queue.isEmpty() || currentIndex >= queue.size()) {
      ConsoleUtil.printInfo("No song playing.");
      ConsoleUtil.pressEnterToContinue();
      return;
    }

    Song current = queue.get(currentIndex);
    try {
      boolean success = favoriteService.addToFavorites(session.getCurrentUserId(), current.getSongId());
      if (success) {
        ConsoleUtil.printSuccess("Added '" + current.getTitle() + "' to favorites!");
      } else {
        ConsoleUtil.printInfo("Song is already in your favorites.");
      }
    } catch (Exception e) {
      ConsoleUtil.printError("Failed to add to favorites: " + e.getMessage());
    }
    ConsoleUtil.pressEnterToContinue();
  }

  private void recordPlay() {
    if (!queue.isEmpty() && currentIndex < queue.size()) {
      Song current = queue.get(currentIndex);
      try {
        songService.playSong(session.getCurrentUserId(), current.getSongId(), current.getDuration());
      } catch (Exception e) {
        logger.warn("Failed to record play", e);
      }
    }
  }

  private String formatTime(int seconds) {
    int min = seconds / 60;
    int sec = seconds % 60;
    return String.format("%d:%02d", min, sec);
  }

}
