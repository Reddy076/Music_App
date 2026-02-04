package com.revplay.service;

import com.revplay.dao.AlbumDAO;
import com.revplay.model.Album;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AlbumService {
  private final AlbumDAO albumDAO;

  public AlbumService() {
    this(new AlbumDAO());
  }

  public AlbumService(AlbumDAO albumDAO) {
    this.albumDAO = albumDAO;
  }

  public List<Album> getAllAlbums() throws SQLException {
    return albumDAO.findAll();
  }

  public List<Album> getAlbumsByGenre(String genre) throws SQLException {
    return albumDAO.findByGenre(genre);
  }

  public List<Album> getAlbumsByArtist(int artistId) throws SQLException {
    return albumDAO.findByArtist(artistId);
  }

  public List<Album> searchAlbums(String keyword) throws SQLException {
    return albumDAO.search(keyword);
  }

  public Optional<Album> getAlbumById(int albumId) throws SQLException {
    return albumDAO.findById(albumId);
  }

  public int createAlbum(Album album) throws SQLException {
    return albumDAO.create(album);
  }
}
