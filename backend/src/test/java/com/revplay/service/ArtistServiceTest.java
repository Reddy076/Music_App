package com.revplay.service;

import com.revplay.dao.AlbumDAO;
import com.revplay.dao.ArtistDAO;
import com.revplay.dao.SongDAO;
import com.revplay.model.Artist;
import com.revplay.model.Song;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArtistServiceTest {

  @Mock
  private ArtistDAO artistDAO;
  @Mock
  private AlbumDAO albumDAO;
  @Mock
  private SongDAO songDAO;
  @Mock
  private com.revplay.dao.FavoriteDAO favoriteDAO;

  private ArtistService artistService;

  @BeforeEach
  public void setUp() {
    artistService = new ArtistService(artistDAO, albumDAO, songDAO, favoriteDAO);
  }

  @Test
  public void testGetArtistById() throws SQLException {
    Artist mockArtist = new Artist();
    mockArtist.setArtistId(1);
    when(artistDAO.findById(1)).thenReturn(Optional.of(mockArtist));

    Optional<Artist> result = artistService.getArtistById(1);
    assertTrue(result.isPresent());
    assertEquals(1, result.get().getArtistId());
  }

  @Test
  public void testUploadSongSuccess() throws SQLException {
    int userId = 100;
    Artist mockArtist = new Artist();
    mockArtist.setArtistId(1);
    mockArtist.setUserId(userId);

    Song song = new Song();
    song.setTitle("New Single");

    when(artistDAO.findByUserId(userId)).thenReturn(Optional.of(mockArtist));
    when(songDAO.create(any(Song.class))).thenReturn(50);

    int songId = artistService.uploadSong(song, userId);

    assertEquals(50, songId);
    assertEquals(1, song.getArtistId()); // Helper sets artist ID
    verify(songDAO).create(song);
  }

  @Test
  public void testUploadSongFailure_NotAnArtist() throws SQLException {
    int userId = 999;
    when(artistDAO.findByUserId(userId)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> artistService.uploadSong(new Song(), userId));
  }

  @Test
  public void testGetFavoritesForArtist() throws SQLException {
    int artistId = 1;
    List<Map<String, String>> mockFavorites = Collections.singletonList(
        Map.of("username", "fan1", "songTitle", "Hit Song"));

    when(favoriteDAO.getFavoritesForArtist(artistId)).thenReturn(mockFavorites);

    List<Map<String, String>> result = artistService.getFavoritesForArtist(artistId);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("fan1", result.get(0).get("username"));
    verify(favoriteDAO).getFavoritesForArtist(artistId);
  }
}
