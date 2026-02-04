package com.revplay.service;

import com.revplay.dao.*;
import com.revplay.model.Song;
import com.revplay.model.Artist;
import com.revplay.model.Album;
import com.revplay.model.Playlist;
import com.revplay.model.Podcast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {

  @Mock
  private SongDAO songDAO;
  @Mock
  private ArtistDAO artistDAO;
  @Mock
  private AlbumDAO albumDAO;
  @Mock
  private PlaylistDAO playlistDAO;
  @Mock
  private PodcastDAO podcastDAO;

  private SearchService searchService;

  @BeforeEach
  public void setUp() {
    searchService = new SearchService(songDAO, artistDAO, albumDAO, playlistDAO, podcastDAO);
  }

  @Test
  public void testSearchAll() throws SQLException {
    String keyword = "Rock";

    when(songDAO.search(keyword)).thenReturn(Arrays.asList(new Song(), new Song()));
    when(artistDAO.search(keyword)).thenReturn(Arrays.asList(new Artist()));
    when(albumDAO.search(keyword)).thenReturn(Arrays.asList(new Album()));
    when(playlistDAO.search(keyword)).thenReturn(Arrays.asList(new Playlist()));
    when(podcastDAO.search(keyword)).thenReturn(Arrays.asList(new Podcast()));

    Map<String, Object> results = searchService.searchAll(keyword);

    assertNotNull(results);
    assertEquals(5, results.size());
    assertTrue(results.containsKey("songs"));
    assertTrue(results.containsKey("artists"));
    assertTrue(results.containsKey("albums"));
    assertTrue(results.containsKey("playlists"));
    assertTrue(results.containsKey("podcasts"));

    List<?> songs = (List<?>) results.get("songs");
    assertEquals(2, songs.size());
  }

  @Test
  public void testSearchSongs() throws SQLException {
    when(songDAO.search("test")).thenReturn(Arrays.asList(new Song()));

    List<Song> results = searchService.searchSongs("test");
    assertEquals(1, results.size());
    verify(songDAO).search("test");
  }

  @Test
  public void testSearchPodcasts() throws SQLException {
    when(podcastDAO.search("pod")).thenReturn(Arrays.asList(new Podcast(), new Podcast()));

    List<Podcast> results = searchService.searchPodcasts("pod");
    assertEquals(2, results.size());
    verify(podcastDAO).search("pod");
  }

  @Test
  public void testGetHomePageContent() throws SQLException {
    when(songDAO.getPopularSongs(10)).thenReturn(Arrays.asList(new Song()));
    when(songDAO.getRecentSongs(10)).thenReturn(Arrays.asList(new Song()));
    when(artistDAO.findAll()).thenReturn(Arrays.asList(new Artist()));
    when(songDAO.getAllGenres()).thenReturn(Arrays.asList("Rock", "Pop"));

    Map<String, Object> content = searchService.getHomePageContent();

    assertNotNull(content);
    assertTrue(content.containsKey("popularSongs"));
    assertTrue(content.containsKey("recentSongs"));
    assertTrue(content.containsKey("artists"));
    assertTrue(content.containsKey("genres"));
  }
}
