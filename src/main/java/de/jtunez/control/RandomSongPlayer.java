package de.jtunez.control;

import de.jtunez.entity.Song;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RandomSongPlayer implements Callable<Object> {

  private ExperimentalPlayer experimentalPlayer;

  @Override
  public Object call() throws Exception {
    Logger.getLogger(this.getClass().getName()).info("starting random play");
    if (experimentalPlayer != null) {
      stopCurrentSong();
    }

    playRandom();

    while (experimentalPlayer != null) {
      stopCurrentSong();
      playRandom();
    }

    Logger.getLogger(this.getClass().getName()).info("thread ended!!");
    return null;
  }

  private void playRandom() {
    Song randomSong = new SongBO().getRandomSong();
    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "next random song: {0} - {1}", new Object[]{randomSong.getArtist(), randomSong.getTitle()});
    try {
      experimentalPlayer = new ExperimentalPlayer(Files.newInputStream(randomSong.getFilename()));
      experimentalPlayer.play();
    } catch (IOException ex) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void stopCurrentSong() {
    Logger.getLogger(this.getClass().getName()).info("stopping random song");
    if (experimentalPlayer != null) {
      experimentalPlayer.stop();
      experimentalPlayer = null;
    }
  }

}
