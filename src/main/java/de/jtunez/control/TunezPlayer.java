package de.jtunez.control;

import de.jtunez.control.exception.PlayerException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

@Deprecated
public class TunezPlayer implements Callable<Object> {

  private final Player player;
  private final InputStream inputStream;

  public TunezPlayer(InputStream inputStream) throws PlayerException {
    try {
      this.inputStream = inputStream;
      player = new Player(inputStream);
    } catch (JavaLayerException ex) {
      throw new PlayerException("error during initialisation of tunezPlayer", ex);
    }
  }

  @Override
  public Object call() throws PlayerException {
    try {
      player.play();
    } catch (JavaLayerException ex) {
      throw new PlayerException("error during playback", ex);
    } finally {
      try {
        inputStream.close();
      } catch (IOException ex) {
        Logger.getLogger(this.getClass().getName()).info("player thread ended due to exception!!");
        throw new PlayerException("error during playback stopping", ex);
      }
    }
    Logger.getLogger(this.getClass().getName()).info("player thread ended!!");
    return null;
  }

  public void stop() {
    player.close();
    try {
      inputStream.close();
    } catch (IOException ex) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
    }
  }
}
