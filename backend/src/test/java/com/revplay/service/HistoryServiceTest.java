package com.revplay.service;

import com.revplay.dao.HistoryDAO;
import com.revplay.model.ListeningHistory;
import com.revplay.model.Song;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HistoryServiceTest {

  @Mock
  private HistoryDAO historyDAO;

  private HistoryService historyService;

  @BeforeEach
  public void setUp() {
    historyService = new HistoryService(historyDAO);
  }

  @Test
  public void testAddToHistory() throws SQLException {
    int userId = 1;
    int songId = 2;
    int duration = 300;

    historyService.addToHistory(userId, songId, duration);

    verify(historyDAO).addToHistory(userId, songId, duration);
  }

  @Test
  public void testGetRecentlyPlayed() throws SQLException {
    int userId = 1;
    List<Song> expected = Arrays.asList(new Song(), new Song());
    when(historyDAO.getRecentlyPlayed(eq(userId), anyInt())).thenReturn(expected);

    List<Song> result = historyService.getRecentlyPlayed(userId, 50);

    assertEquals(2, result.size());
    verify(historyDAO).getRecentlyPlayed(eq(userId), anyInt());
  }

  @Test
  public void testGetHistory() throws SQLException {
    int userId = 1;
    List<ListeningHistory> expected = Arrays.asList(new ListeningHistory(), new ListeningHistory());
    when(historyDAO.getHistory(eq(userId), anyInt())).thenReturn(expected);

    List<ListeningHistory> result = historyService.getHistory(userId, 100);

    assertEquals(2, result.size());
    verify(historyDAO).getHistory(eq(userId), anyInt());
  }

  @Test
  public void testGetTotalListeningTime() throws SQLException {
    int userId = 1;
    when(historyDAO.getTotalListeningTime(userId)).thenReturn(1200);

    int result = historyService.getTotalListeningTime(userId);

    assertEquals(1200, result);
  }

  @Test
  public void testClearHistory() throws SQLException {
    int userId = 1;
    when(historyDAO.clearHistory(userId)).thenReturn(true);

    boolean result = historyService.clearHistory(userId);

    assertTrue(result);
    verify(historyDAO).clearHistory(userId);
  }
}
