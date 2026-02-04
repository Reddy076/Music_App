package com.revplay.service;

import com.revplay.dao.AlbumDAO;
import com.revplay.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {

  @Mock
  private AlbumDAO albumDAO;

  private AlbumService albumService;

  @BeforeEach
  public void setUp() {
    albumService = new AlbumService(albumDAO);
  }

  @Test
  public void testGetAllAlbums() throws SQLException {
    when(albumDAO.findAll()).thenReturn(Collections.singletonList(new Album()));

    List<Album> result = albumService.getAllAlbums();
    assertEquals(1, result.size());
    verify(albumDAO).findAll();
  }

  @Test
  public void testGetAlbumById() throws SQLException {
    Album mockAlbum = new Album();
    when(albumDAO.findById(1)).thenReturn(Optional.of(mockAlbum));

    Optional<Album> result = albumService.getAlbumById(1);
    assertTrue(result.isPresent());
    verify(albumDAO).findById(1);
  }
}
