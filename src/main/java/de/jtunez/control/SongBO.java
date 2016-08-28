package de.jtunez.control;

import de.jtunez.entity.Song;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SongBO {

  private static final List<Song> SONGS = new LinkedList<>();
  private static final List<String> PLAYLISTS = new LinkedList<>();
  //
  private static final Logger LOG = Logger.getLogger(SongBO.class.getName());

  public SongBO() {
  }

  public Song getRandomSong() {
    return SONGS.get((int) (Math.random() * SONGS.size()));
  }

  public List<Song> findByPlaylist(String playlist) {
    return SONGS.stream()
            .filter(song -> song.getPlaylist().equals(playlist))
            .collect(Collectors.toList());
  }

  public Song findById(long songId) {
    List<Song> song = findByIds(Collections.singletonList(songId));
    if (song.isEmpty()) {
      return null;
    }
    return song.get(0);
  }

  private List<Song> findByIds(List<Long> ids) {
    return SONGS.stream()
            .filter(song -> ids.contains(song.getId()))
            .collect(Collectors.toList());
  }

  public void save(List<Song> songs) {
    SONGS.clear();
    PLAYLISTS.clear();
    Map<String, Path> magicPathMap = new HashMap<>();
    songs.stream()
            .forEach(song -> {
              LOG.log(Level.INFO, "persisting song ''{0} - {1}'' => {2}", new Object[]{song.getArtist(), song.getTitle(), song.getFilename().toString()});
              String playlist = createPlaylistName(song.getFilename().getParent(), magicPathMap);
              LOG.log(Level.INFO, " - assigning playlist ''{0}''", playlist);
              song.setPlaylist(playlist);
              SONGS.add(song);
            });
    PLAYLISTS.addAll(songs.stream().
            map(Song::getPlaylist)
            .distinct()
            .sorted()
            .collect(Collectors.toList()));
  }

  public List<String> getPlaylists() {
    return new LinkedList<>(PLAYLISTS);
  }

  private String createPlaylistName(Path folder, Map<String, Path> magicPathMap) {
    String folderName = folder.getFileName().toString();
    if (tryName(magicPathMap, folderName, folder)) {
      return folderName;
    }
    Path parentFolder = folder.getParent();
    String parentFolderName = parentFolder.getFileName().toString() + "-" + folderName;
    if (tryName(magicPathMap, parentFolderName, folder)) {
      return parentFolderName;
    }
    for (int i = 1; i < 11; i += 1) {
      String numericalFolderName = parentFolderName + "_" + i;
      if (tryName(magicPathMap, numericalFolderName, folder)) {
        return numericalFolderName;
      }
    }
    return parentFolderName; //meh
  }

  private boolean tryName(Map<String, Path> magicPathMap, String parentFolderName, Path folder) {
    Path parentFolderPath = magicPathMap.get(parentFolderName);
    if (parentFolderPath == null) {
      magicPathMap.put(parentFolderName, folder);
      return true;
    }
    return folder.equals(parentFolderPath);
  }

}
