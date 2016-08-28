package de.jtunez.boundary;

import de.jtunez.control.App;
import de.jtunez.control.SongBO;
import de.jtunez.control.FileWalker;
import de.jtunez.control.config.WebradioStreamRegistry;
import de.jtunez.control.exception.PlayerException;
import de.jtunez.entity.WebradioStream;
import de.jtunez.entity.Song;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
  public String adjustVolume(@PathParam("volume") float volume) {
    App.getSingleton().adjustVolume(volume);
    return "set to " + volume;
  }

  @GET
  @Path("volume")
  @Produces(MediaType.APPLICATION_JSON)
  public String getVolume() {
    return Float.toString(App.getSingleton().getVolume());
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
    try {
      App.getSingleton().startWebradio();
    } catch (PlayerException ex) {
      Logger.getLogger(RestInterface.class.getName()).log(Level.SEVERE, null, ex);
      return "fail";
    }
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
  @Path("webradiostreams")
  @Produces(MediaType.APPLICATION_JSON)
  public List<WebradioStream> getWebradioStreams() {
    return WebradioStreamRegistry.getStreams();
  }

  @GET
  @Path("currentwebradiostream")
  @Produces(MediaType.APPLICATION_JSON)
  public WebradioStream getCurrentWebradioStream() {
    return App.getSingleton().getCurrentWebradioStream();
  }

  @GET
  @Path("webradiostream/{stream}")
  @Produces(MediaType.APPLICATION_JSON)
  public String switchWebradioStream(@PathParam("stream") String stream) {
    try {
      App.getSingleton().setCurrentWebradioStream(WebradioStreamRegistry.getStreamById(stream));
    } catch (PlayerException ex) {
      Logger.getLogger(RestInterface.class.getName()).log(Level.SEVERE, null, ex);
      return "fail";
    }
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
  public List<String> getPlaylists() throws Exception {
    return new SongBO().getPlaylists();
  }

  @GET
  @Path("get_songs_for_playlist/{playlist_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Song> getPlaylist(@PathParam("playlist_id") String playlist) throws Exception {
    return new SongBO().findByPlaylist(playlist);
  }
}
