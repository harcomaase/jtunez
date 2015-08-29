package de.jtunez.entity.dao;

import de.jtunez.entity.Song;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SongDAO extends AbstractFacade {

  public SongDAO() throws IOException, SQLException {
    super();
  }

  public void create(Song song) throws SQLException {
    try (PreparedStatement preparedStatement = mapIntoPreparedStatement("INSERT INTO song VALUES (0,?,?,?,?,?,?)", song)) {
      int updatedRows = preparedStatement.executeUpdate();
      if (updatedRows != 1) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "file ''{0}'' not properly created. updatedRows={1}", new Object[]{song.getFilename().toString(), updatedRows});
      }
    }
  }

  public void update(Song song) throws SQLException {
    String sql = "UPDATE song SET filename=?,artist=?,title=?,album=?,duration_in_seconds=?,last_change=? WHERE id=?";
    song.setLastChange(Instant.now());
    try (PreparedStatement preparedStatement = mapIntoPreparedStatement(sql, song)) {
      preparedStatement.setLong(7, song.getId());

      int updatedRows = preparedStatement.executeUpdate();
      if (updatedRows != 1) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "file ''{0}'' not properly created. updatedRows={1}", new Object[]{song.getFilename().toString(), updatedRows});
      }
    }
  }

  private PreparedStatement mapIntoPreparedStatement(String sql, Song song) throws SQLException {
    PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
    preparedStatement.setString(1, song.getFilename().toString());
    preparedStatement.setString(2, song.getArtist());
    preparedStatement.setString(3, song.getTitle());
    preparedStatement.setString(4, song.getAlbum());
    preparedStatement.setLong(5, song.getDurationInSeconds());
    preparedStatement.setLong(6, song.getLastChange().toEpochMilli());
    return preparedStatement;
  }

  public Song findByFilename(String filename) throws SQLException {
    Song song = null;
    try (PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM song WHERE filename = ?")) {
      preparedStatement.setString(1, filename);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          song = map(resultSet);
        }
      }
    }
    return song;
  }

  public List<Song> findByIds(List<Long> ids) throws SQLException {
    List<Song> result = new LinkedList<>();
    StringJoiner j = new StringJoiner(",");
    ids.stream().forEach(id -> j.add(Long.toString(id)));
    try (PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM song WHERE id IN (" + j.toString() + ")")) {
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          result.add(map(resultSet));
        }
      }
    }
    return result;
  }

  private Song map(ResultSet resultSet) throws SQLException {
    Song song = new Song();
    song.setId(resultSet.getLong("id"));
    song.setFilename(Paths.get(resultSet.getString("filename")));
    song.setArtist(resultSet.getString("artist"));
    song.setTitle(resultSet.getString("title"));
    song.setAlbum(resultSet.getString("album"));
    song.setDurationInSeconds(resultSet.getLong("duration_in_seconds"));
    song.setLastChange(Instant.ofEpochMilli(resultSet.getLong("last_change")));
    return song;
  }

  public List<Long> findAllIds() throws SQLException {
    List<Long> result = new ArrayList<>();
    try (PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT id FROM song")) {
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          result.add(resultSet.getLong("id"));
        }
      }
    }
    return result;
  }
}
