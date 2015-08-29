package de.jtunez.control;

import de.jtunez.control.exception.PlayerException;
import de.jtunez.entity.Song;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RandomSongPlayer implements Callable<Object> {

  private TunezPlayer currentSong;
  private final ExecutorService tunezPlayerExecutor;

  public RandomSongPlayer() {
    tunezPlayerExecutor = Executors.newSingleThreadExecutor(new DaemonThreadFactory());
  }

  @Override
  public Object call() throws Exception {
    if (currentSong != null) {
      stopCurrentSong();
    }

    playRandom();

    while (currentSong != null) {
      stopCurrentSong();
      playRandom();
    }

    Logger.getLogger(this.getClass().getName()).info("thread ended!!");
    return null;
  }

  private void playRandom() {
    Song randomSong = new SongBO().getRandomSong();
    try {
      currentSong = new TunezPlayer(Files.newInputStream(randomSong.getFilename()));
      Logger.getLogger(this.getClass().getName()).log(Level.INFO, "next random song: {0} - {1}", new Object[]{randomSong.getArtist(), randomSong.getTitle()});
      Future<Object> playingNow = tunezPlayerExecutor.submit(currentSong);
      awaitTermination(playingNow);
    } catch (PlayerException | IOException ex) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void awaitTermination(Future<Object> playingNow) {
    while (!playingNow.isDone()) {
      try {
        playingNow.get();
      } catch (InterruptedException | ExecutionException ex) {
        Logger.getLogger(RandomSongPlayer.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  public void stopCurrentSong() {
    if (currentSong != null) {
      currentSong.stop();
      currentSong = null;
    }
  }

}
