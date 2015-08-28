package de.jtunez.control;

import de.jtunez.entity.Song;
import de.jtunez.entity.PlaylistXSong;
import de.jtunez.entity.dao.SongDAO;
import de.jtunez.entity.dao.PlaylistXSongDAO;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SongBO {

  private final SongDAO songDAO;

  public SongBO() {
    try {
      this.songDAO = new SongDAO();
    } catch (IOException | SQLException ex) {
      Logger.getLogger(SongBO.class.getName()).log(Level.SEVERE, null, ex);
      throw new IllegalStateException(ex);
    }
  }

  public Song getRandomSong() {
    try {
      List<Long> ids = songDAO.findAllIds();
      return findById(ids.get((int) (Math.random() * ids.size())));
    } catch (SQLException ex) {
      Logger.getLogger(SongBO.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  public List<Song> findByPlaylistId(long playlistId) {
    List<Song> result = new LinkedList<>();
    try {
      List<PlaylistXSong> xs = new PlaylistXSongDAO().findByPlaylistId(playlistId);
      result.addAll(findByIds(xs.stream().map(PlaylistXSong::getSongId).collect(Collectors.toList())));
    } catch (IOException | SQLException ex) {
      Logger.getLogger(SongBO.class.getName()).log(Level.SEVERE, null, ex);
    }
    return result;
  }

  public Song findById(long songId) {
    List<Song> song = findByIds(Collections.singletonList(songId));
    if (song.isEmpty()) {
      return null;
    }
    return song.get(0);
  }

  private List<Song> findByIds(List<Long> ids) {
    try {
      if (ids.size() > 0) {
        return songDAO.findByIds(ids);
      }
    } catch (SQLException ex) {
      Logger.getLogger(SongBO.class.getName()).log(Level.SEVERE, null, ex);
    }
    return new LinkedList<>();
  }

  public void createOrUpdate(Song song) {
    Song fromDB = findSong(song);
    if (fromDB == null) {
      create(song);
      return;
    }
    song.setId(fromDB.getId());
    if (alreadyPresent(fromDB, song)) {
      //do nothing
      return;
    }
    update(song);
  }

  private Song findSong(Song song) {
    try {
      return songDAO.findByFilename(song.getFilename().toString());
    } catch (SQLException ex) {
      Logger.getLogger(SongBO.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  private void update(Song song) {
    try {
      songDAO.update(song);
    } catch (SQLException ex) {
      Logger.getLogger(SongBO.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void create(Song song) {
    try {
      songDAO.create(song);
    } catch (SQLException ex) {
      Logger.getLogger(SongBO.class.getName()).log(Level.SEVERE, "song could not be created", ex);
    }
  }

  private boolean alreadyPresent(Song fromDB, Song song) {
    for (Field field : Song.class.getDeclaredFields()) {
      try {
        Method getter = Song.class.getDeclaredMethod("get" + capitalise(field.getName()));
        if (!Objects.equals(getter.invoke(fromDB), getter.invoke(song))) {
          return false;
        }
      } catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
        Logger.getLogger(SongBO.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return true;
  }

  private String capitalise(String name) {
    return name.substring(0, 1).toUpperCase() + name.substring(1);
  }
}
