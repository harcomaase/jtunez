package de.jtunez.control;

import de.jtunez.control.exception.PlayerException;
import de.jtunez.entity.Song;
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
    tunezPlayerExecutor = Executors.newSingleThreadExecutor();
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

    return null;
  }

  private void playRandom() {
    Song randomSong = new SongBO().getRandomSong();
    try {
      currentSong = new TunezPlayer(randomSong.getFilename());
      Logger.getLogger(App.class.getName()).log(Level.INFO, "next random song: {0} - {1}", new Object[]{randomSong.getArtist(), randomSong.getTitle()});
      Future<Object> playingNow = tunezPlayerExecutor.submit(currentSong);
      while (!playingNow.isDone()) {
        playingNow.get();
      }
    } catch (PlayerException | InterruptedException | ExecutionException ex) {
      Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void stopCurrentSong() {
    if (currentSong != null) {
      currentSong.stop();
      currentSong = null;
    }
  }

}
