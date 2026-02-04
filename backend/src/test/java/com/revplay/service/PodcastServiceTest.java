package com.revplay.service;

import com.revplay.dao.ArtistDAO;
import com.revplay.dao.PodcastDAO;
import com.revplay.model.Artist;
import com.revplay.model.Podcast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PodcastServiceTest {

  @Mock
  private PodcastDAO podcastDAO;

  @Mock
  private ArtistDAO artistDAO;

  private PodcastService podcastService;

  @BeforeEach
  public void setUp() {
    podcastService = new PodcastService(podcastDAO, artistDAO);
  }

  @Test
  public void testCreatePodcast_Success() throws SQLException {
    Podcast podcast = new Podcast();
    podcast.setTitle("My Podcast");
    podcast.setArtistId(1);
    podcast.setGenre("Tech");

    when(artistDAO.findById(1)).thenReturn(Optional.of(new Artist()));
    when(podcastDAO.create(any(Podcast.class))).thenReturn(100);

    Podcast created = podcastService.createPodcast(podcast);

    assertNotNull(created);
    assertEquals(100, created.getPodcastId());
    verify(podcastDAO).create(podcast);
  }

  @Test
  public void testCreatePodcast_ArtistNotFound() throws SQLException {
    Podcast podcast = new Podcast();
    podcast.setArtistId(999);

    when(artistDAO.findById(999)).thenReturn(Optional.empty());

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      podcastService.createPodcast(podcast);
    });

    assertTrue(exception.getMessage().contains("Artist not found"));
    verify(podcastDAO, never()).create(any(Podcast.class));
  }

  @Test
  public void testSearchPodcasts() throws SQLException {
    String keyword = "Tech";
    List<Podcast> expected = Arrays.asList(new Podcast(), new Podcast());

    when(podcastDAO.search(keyword)).thenReturn(expected);

    List<Podcast> results = podcastService.searchPodcasts(keyword);

    assertEquals(2, results.size());
    verify(podcastDAO).search(keyword);
  }

  @Test
  public void testGetPodcastsByArtist() throws SQLException {
    int artistId = 1;
    List<Podcast> expected = Arrays.asList(new Podcast("Ep1", 1, "Talk", 600));

    when(podcastDAO.findByArtist(artistId)).thenReturn(expected);

    List<Podcast> results = podcastService.getPodcastsByArtist(artistId);

    assertEquals(1, results.size());
    assertEquals("Ep1", results.get(0).getTitle());
  }

  @Test
  public void testIncrementPlayCount() throws SQLException {
    int podcastId = 5;

    podcastService.incrementPlayCount(podcastId);

    verify(podcastDAO).incrementPlayCount(podcastId);
  }
}
