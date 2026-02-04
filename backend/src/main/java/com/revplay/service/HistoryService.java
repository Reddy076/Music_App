package com.revplay.service;

import com.revplay.dao.HistoryDAO;
import com.revplay.model.ListeningHistory;
import com.revplay.model.Song;

import java.sql.SQLException;
import java.util.List;

public class HistoryService {
  private final HistoryDAO historyDAO;

  public HistoryService() {
    this(new HistoryDAO());
  }

  public HistoryService(HistoryDAO historyDAO) {
    this.historyDAO = historyDAO;
  }

  public void addToHistory(int userId, int songId, int durationPlayed) throws SQLException {
    historyDAO.addToHistory(userId, songId, durationPlayed);
  }

  public List<Song> getRecentlyPlayed(int userId, int limit) throws SQLException {
    return historyDAO.getRecentlyPlayed(userId, limit);
  }

  public List<ListeningHistory> getHistory(int userId, int limit) throws SQLException {
    return historyDAO.getHistory(userId, limit);
  }

  public int getTotalListeningTime(int userId) throws SQLException {
    return historyDAO.getTotalListeningTime(userId);
  }

  public boolean clearHistory(int userId) throws SQLException {
    return historyDAO.clearHistory(userId);
  }
}
