package com.revplay.service;

import com.revplay.dao.FavoriteDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FavoriteServiceTest {

  @Mock
  private FavoriteDAO favoriteDAO;

  private FavoriteService favoriteService;

  @BeforeEach
  public void setUp() {
    favoriteService = new FavoriteService(favoriteDAO);
  }

  @Test
  public void testAddToFavorites_New() throws SQLException {
    int userId = 1;
    int songId = 10;

    when(favoriteDAO.isFavorite(userId, songId)).thenReturn(false);
    when(favoriteDAO.add(userId, songId)).thenReturn(true);

    boolean result = favoriteService.addToFavorites(userId, songId);

    assertTrue(result);
    verify(favoriteDAO).add(userId, songId);
  }

  @Test
  public void testAddToFavorites_AlreadyExists() throws SQLException {
    int userId = 1;
    int songId = 10;

    when(favoriteDAO.isFavorite(userId, songId)).thenReturn(true);

    boolean result = favoriteService.addToFavorites(userId, songId);

    assertFalse(result); // Should return false as it wasn't added again
    verify(favoriteDAO, never()).add(userId, songId);
  }

  @Test
  public void testToggleFavorite() throws SQLException {
    int userId = 1;
    int songId = 10;

    // First call: is favorite -> remove
    when(favoriteDAO.isFavorite(userId, songId)).thenReturn(true);
    when(favoriteDAO.remove(userId, songId)).thenReturn(true);

    assertTrue(favoriteService.toggleFavorite(userId, songId));
    verify(favoriteDAO).remove(userId, songId);
  }
}
