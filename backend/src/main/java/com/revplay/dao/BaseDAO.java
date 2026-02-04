package com.revplay.dao;

import com.revplay.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDAO {
  protected final DatabaseConfig dbConfig;

  protected BaseDAO() {
    this.dbConfig = DatabaseConfig.getInstance();
  }

  protected interface RowMapper<T> {
    T map(ResultSet rs) throws SQLException;
  }

  protected <T> List<T> executeQueryForList(String sql, RowMapper<T> mapper) throws SQLException {
    try (Connection conn = dbConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      return executeQueryForList(stmt, mapper);
    }
  }

  protected <T> List<T> executeQueryForList(PreparedStatement stmt, RowMapper<T> mapper) throws SQLException {
    List<T> list = new ArrayList<>();
    try (ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        list.add(mapper.map(rs));
      }
    }
    return list;
  }

  protected int executeUpdate(String sql, Object... params) throws SQLException {
    try (Connection conn = dbConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      setParameters(stmt, params);
      return stmt.executeUpdate();
    }
  }

  protected int executeInsert(String sql, Object... params) throws SQLException {
    try (Connection conn = dbConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
      setParameters(stmt, params);
      int affectedRows = stmt.executeUpdate();
      if (affectedRows == 0) {
        throw new SQLException("Creating record failed, no rows affected.");
      }
      try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          return generatedKeys.getInt(1);
        } else {
          throw new SQLException("Creating record failed, no ID obtained.");
        }
      }
    }
  }

  private void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
    for (int i = 0; i < params.length; i++) {
      stmt.setObject(i + 1, params[i]);
    }
  }
}
