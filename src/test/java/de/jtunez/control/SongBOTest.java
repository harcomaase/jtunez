package de.jtunez.control;

import de.jtunez.entity.Song;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class SongBOTest {

  @Test
  public void testPlaylistSave() {
    List<Song> songs = new LinkedList<>();
    Song song1 = new Song();
    song1.setTitle("song1");
    song1.setFilename(Paths.get("/music/band1/"));
    Song song2 = new Song();
    song2.setTitle("song2");
    song2.setFilename(Paths.get("/music/band1/"));
    Song song3 = new Song();
    song3.setTitle("song3");
    song3.setFilename(Paths.get("/music/band1/album1"));
    Song song4 = new Song();
    song4.setTitle("song3");
    song4.setFilename(Paths.get("/music/band1/album1"));
    Song song5 = new Song();
    song5.setTitle("song3");
    song5.setFilename(Paths.get("/music/band2/band1/album1"));
    Song song6 = new Song();
    song6.setTitle("song3");
    song6.setFilename(Paths.get("/music/test/band1/album1"));
    
    songs.add(song1);
    songs.add(song2);
    songs.add(song3);
    songs.add(song4);
    songs.add(song5);
    songs.add(song6);
    
    songs.forEach(song -> song.setArtist("unit test"));
    songs.forEach(song -> song.setFilename(Paths.get(song.getFilename().toString(), "song.ogg")));
    SongBO instance = new SongBO();
    instance.save(songs);
    List<String> playlists = instance.getPlaylists();
    assertEquals(4, playlists.size());
  }

}
