package com.revplay.service;

import com.revplay.dao.HistoryDAO;
import com.revplay.dao.SongDAO;
import com.revplay.model.Song;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SongServiceTest {

  @Mock
  private SongDAO songDAO;

  @Mock
  private HistoryDAO historyDAO;

  private SongService songService;

  @BeforeEach
  public void setUp() {
    songService = new SongService(songDAO, historyDAO);
  }

  @Test
  public void testCreateSongSuccess() throws Exception {
    Song song = new Song();
    song.setTitle("New Song");
    song.setDuration(300);

    when(songDAO.create(song)).thenReturn(1);

    int result = songService.createSong(song);
    assertEquals(1, result);
    verify(songDAO).create(song);
  }

  @Test
  public void testCreateSongFailure_InvalidInput() {
    Song song = new Song();
    // Missing title
    assertThrows(IllegalArgumentException.class, () -> songService.createSong(song));
  }

  @Test
  public void testSearchSongs() throws Exception {
    Song s1 = new Song();
    s1.setTitle("Love Song");
    Song s2 = new Song();
    s2.setTitle("Love Story");
    List<Song> mockResults = Arrays.asList(s1, s2);

    when(songDAO.search("love")).thenReturn(mockResults);

    List<Song> results = songService.searchSongs("love");
    assertEquals(2, results.size());
    verify(songDAO).search("love");
  }

  @Test
  public void testSearchSongs_EmptyKeyword() throws Exception {
    when(songDAO.findAll()).thenReturn(Collections.emptyList());

    songService.searchSongs("");
    // Should call findAll for empty keyword
    verify(songDAO).findAll();
    verify(songDAO, never()).search(any());
  }

  @Test
  public void testGetSongsByGenre() throws Exception {
    when(songDAO.findByGenre("Pop")).thenReturn(Collections.singletonList(new Song()));

    List<Song> results = songService.getSongsByGenre("Pop");
    assertFalse(results.isEmpty());
    verify(songDAO).findByGenre("Pop");
  }
}
