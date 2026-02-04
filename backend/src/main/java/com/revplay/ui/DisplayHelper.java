package com.revplay.ui;

import com.revplay.model.Album;
import com.revplay.model.Artist;
import com.revplay.model.Playlist;
import com.revplay.model.Song;

import java.util.List;

public class DisplayHelper {

  public static void displaySongs(List<Song> songs) {
    if (songs.isEmpty()) {
      ConsoleUtil.printInfo("No songs found.");
      return;
    }
    System.out.printf("  %-4s %-30s %-20s %-10s %-8s%n", "#", "Title", "Artist", "Genre", "Duration");
    ConsoleUtil.printDivider();
    for (int i = 0; i < songs.size(); i++) {
      Song s = songs.get(i);
      System.out.printf("  %-4d %-30s %-20s %-10s %-8s%n",
          i + 1,
          truncate(s.getTitle(), 28),
          truncate(s.getArtistName() != null ? s.getArtistName() : "Unknown", 18),
          truncate(s.getGenre() != null ? s.getGenre() : "N/A", 8),
          s.getFormattedDuration());
    }
  }

  public static void displaySongsWithPlayCount(List<Song> songs) {
    if (songs.isEmpty()) {
      ConsoleUtil.printInfo("No songs found.");
      return;
    }
    System.out.printf("  %-4s %-28s %-18s %-10s %-8s %-8s%n", "#", "Title", "Artist", "Genre", "Duration", "Plays");
    ConsoleUtil.printDivider();
    for (int i = 0; i < songs.size(); i++) {
      Song s = songs.get(i);
      System.out.printf("  %-4d %-28s %-18s %-10s %-8s %-8d%n",
          i + 1,
          truncate(s.getTitle(), 26),
          truncate(s.getArtistName() != null ? s.getArtistName() : "Unknown", 16),
          truncate(s.getGenre() != null ? s.getGenre() : "N/A", 8),
          s.getFormattedDuration(),
          s.getPlayCount());
    }
  }

  public static void displayArtists(List<Artist> artists) {
    if (artists.isEmpty()) {
      ConsoleUtil.printInfo("No artists found.");
      return;
    }
    System.out.printf("  %-4s %-25s %-15s%n", "#", "Artist Name", "Genre");
    ConsoleUtil.printDivider();
    for (int i = 0; i < artists.size(); i++) {
      Artist a = artists.get(i);
      System.out.printf("  %-4d %-25s %-15s%n",
          i + 1,
          truncate(a.getArtistName(), 23),
          truncate(a.getGenre() != null ? a.getGenre() : "N/A", 13));
    }
  }

  public static void displayAlbums(List<Album> albums) {
    if (albums.isEmpty()) {
      ConsoleUtil.printInfo("No albums found.");
      return;
    }
    System.out.printf("  %-4s %-25s %-20s %-12s%n", "#", "Album Title", "Artist", "Release Date");
    ConsoleUtil.printDivider();
    for (int i = 0; i < albums.size(); i++) {
      Album a = albums.get(i);
      System.out.printf("  %-4d %-25s %-20s %-12s%n",
          i + 1,
          truncate(a.getTitle(), 23),
          truncate(a.getArtistName() != null ? a.getArtistName() : "Unknown", 18),
          a.getReleaseDate() != null ? a.getReleaseDate().toString() : "N/A");
    }
  }

  public static void displayPlaylists(List<Playlist> playlists) {
    if (playlists.isEmpty()) {
      ConsoleUtil.printInfo("No playlists found.");
      return;
    }
    System.out.printf("  %-4s %-25s %-15s %-8s%n", "#", "Playlist Name", "Created By", "Songs");
    ConsoleUtil.printDivider();
    for (int i = 0; i < playlists.size(); i++) {
      Playlist p = playlists.get(i);
      System.out.printf("  %-4d %-25s %-15s %-8d%n",
          i + 1,
          truncate(p.getName(), 23),
          truncate(p.getUsername() != null ? p.getUsername() : "Unknown", 13),
          p.getSongCount());
    }
  }

  public static String truncate(String str, int maxLength) {
    if (str == null)
      return "";
    return str.length() > maxLength ? str.substring(0, maxLength - 2) + ".." : str;
  }
}
