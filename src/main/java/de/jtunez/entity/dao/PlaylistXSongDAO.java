package de.jtunez.entity.dao;

import de.jtunez.entity.PlaylistXSong;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlaylistXSongDAO extends AbstractFacade {

  public PlaylistXSongDAO() throws IOException, SQLException {
    super();
  }

  public void create(PlaylistXSong x) throws SQLException {
    try (PreparedStatement preparedStatement = mapIntoPreparedStatement("INSERT INTO playlist_x_song VALUES (?,?)", x)) {
      int updatedRows = preparedStatement.executeUpdate();
      if (updatedRows != 1) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "x not properly created. updatedRows={1}", new Object[]{updatedRows});
      }
    }
  }

  public void delete(PlaylistXSong x) throws SQLException {
    String sql = "DELETE FROM song WHERE playlist_id=? AND song_id=?";
    try (PreparedStatement preparedStatement = mapIntoPreparedStatement(sql, x)) {

      int updatedRows = preparedStatement.executeUpdate();
      if (updatedRows != 1) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "x ''{0}'' / ''{1}'' not properly deleted. updatedRows={2}", new Object[]{x.getPlaylistId(), x.getSongId(), updatedRows});
      }
    }
  }

  private PreparedStatement mapIntoPreparedStatement(String sql, PlaylistXSong x) throws SQLException {
    PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
    preparedStatement.setLong(1, x.getPlaylistId());
    preparedStatement.setLong(2, x.getSongId());
    return preparedStatement;
  }

  public List<PlaylistXSong> findByPlaylistId(long playlistId) throws SQLException {
    List<PlaylistXSong> result = new LinkedList<>();
    try (PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM playlist_x_song WHERE playlist_id = ?")) {
      preparedStatement.setLong(1, playlistId);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          result.add(map(resultSet));
        }
      }
    }
    return result;
  }

  private PlaylistXSong map(ResultSet resultSet) throws SQLException {
    PlaylistXSong playlist = new PlaylistXSong();
    playlist.setPlaylistId(resultSet.getLong("playlist_id"));
    playlist.setSongId(resultSet.getLong("song_id"));
    return playlist;
  }
}
