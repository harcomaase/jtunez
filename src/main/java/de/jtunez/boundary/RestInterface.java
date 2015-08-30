package de.jtunez.boundary;

import de.jtunez.control.App;
import de.jtunez.control.SongBO;
import de.jtunez.control.FileWalker;
import de.jtunez.entity.Song;
import de.jtunez.entity.Playlist;
import de.jtunez.entity.dao.PlaylistDAO;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class RestInterface {

  @GET
  @Path("hello")
  @Produces(MediaType.APPLICATION_JSON)
  public String hello() {
    return "hi";
  }

  @GET
  @Path("stop")
  @Produces(MediaType.APPLICATION_JSON)
  public String stop() {
    App.getSingleton().interruptMainLoop();
    return "stopping now";
  }

  @GET
  @Path("volume/{volume}")
  @Produces(MediaType.APPLICATION_JSON)
  public String volume(@PathParam("volume") float volume) {
    App.getSingleton().adjustVolume(volume);
    return "set to " + volume;
  }

  @GET
  @Path("start_random")
  @Produces(MediaType.APPLICATION_JSON)
  public String startRandom() {
    App.getSingleton().startRandom();
    return "ok";
  }

  @GET
  @Path("stop_random")
  @Produces(MediaType.APPLICATION_JSON)
  public String stopRandom() {
    App.getSingleton().stopRandom();
    return "ok";
  }

  @GET
  @Path("start_webradio")
  @Produces(MediaType.APPLICATION_JSON)
  public String startWebradio() {
    App.getSingleton().startWebradio();
    return "ok";
  }

  @GET
  @Path("stop_webradio")
  @Produces(MediaType.APPLICATION_JSON)
  public String stopWebradio() {
    App.getSingleton().stopWebradio();
    return "ok";
  }

  @GET
  @Path("scan")
  @Produces(MediaType.APPLICATION_JSON)
  public String scan() throws IOException {
    FileWalker fileWalker = new FileWalker(Paths.get("/data/music/"));
    fileWalker.walk();
    fileWalker.persist();
    return "done";
  }

  @GET
  @Path("get_playlists")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Playlist> getPlaylists() throws Exception {
    return new PlaylistDAO().findAll();
  }

  @GET
  @Path("get_songs_for_playlist/{playlist_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Song> getPlaylists(@PathParam("playlist_id") long playlistId) throws Exception {
    return new SongBO().findByPlaylistId(playlistId);
  }

}
