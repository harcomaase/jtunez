package de.jtunez.control;

import de.jtunez.control.exception.PlayerException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class TunezPlayer implements Callable<Object> {

  private final Player player;

  public TunezPlayer(Path file) throws PlayerException {
    try {
      try (InputStream inputStream = Files.newInputStream(file)) {
        player = new Player(inputStream);
      }
    } catch (JavaLayerException | IOException ex) {
      throw new PlayerException("error during initialisation of tunezPlayer", ex);
    }
  }

  @Override
  public Object call() throws PlayerException {
    try {
      player.play();
    } catch (JavaLayerException ex) {
      throw new PlayerException("error during playback", ex);
    }
    return null;
  }

  public void stop() {
    player.close();
  }
}
