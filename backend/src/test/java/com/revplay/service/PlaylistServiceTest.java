package com.revplay.service;

import com.revplay.dao.PlaylistDAO;
import com.revplay.model.Playlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlaylistServiceTest {

  @Mock
  private PlaylistDAO playlistDAO;

  private PlaylistService playlistService;

  @BeforeEach
  public void setUp() {
    playlistService = new PlaylistService(playlistDAO);
  }

  @Test
  public void testCreatePlaylistSuccess() throws SQLException {
    int userId = 1;
    String name = "My Playlist";
    String description = "Chill songs";
    boolean isPublic = true;

    when(playlistDAO.create(any(Playlist.class))).thenReturn(10);

    int playlistId = playlistService.createPlaylist(userId, name, description, isPublic);

    assertEquals(10, playlistId);
    verify(playlistDAO).create(any(Playlist.class));
  }

  @Test
  public void testCreatePlaylistFailure_MissingName() {
    assertThrows(IllegalArgumentException.class, () -> playlistService.createPlaylist(1, "", "Desc", true));
  }

  @Test
  public void testAddSongToPlaylistSuccess() throws SQLException {
    int playlistId = 10;
    int songId = 5;
    int userId = 1;

    Playlist mockPlaylist = new Playlist(userId, "My Playlist", true);
    mockPlaylist.setPlaylistId(playlistId);

    when(playlistDAO.findById(playlistId)).thenReturn(Optional.of(mockPlaylist));
    when(playlistDAO.containsSong(playlistId, songId)).thenReturn(false);
    when(playlistDAO.addSong(playlistId, songId)).thenReturn(true);

    boolean result = playlistService.addSongToPlaylist(playlistId, songId, userId);

    assertTrue(result);
    verify(playlistDAO).addSong(playlistId, songId);
  }

  @Test
  public void testAddSongToPlaylistFailure_AccessDenied() throws SQLException {
    int playlistId = 10;
    int userId = 1;
    int otherUserId = 2;

    Playlist mockPlaylist = new Playlist(otherUserId, "Others Playlist", true);

    when(playlistDAO.findById(playlistId)).thenReturn(Optional.of(mockPlaylist));

    assertThrows(IllegalArgumentException.class, () -> playlistService.addSongToPlaylist(playlistId, 5, userId));
  }
}
