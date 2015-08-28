package de.jtunez.control;

import de.jtunez.control.exception.PlayerException;
import de.jtunez.entity.Song;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class App {

  private static App instance;
  //
  private boolean running;
  private Instant lastTick;
  private Duration elapsedSinceLastTick;
  private TunezPlayer currentSong;
  private final ExecutorService tunezPlayerExecutor;
  //
  private static final Duration TICK_EVERY = Duration.ofSeconds(5);
  //
  private static final Object LOCK = new Object();

  private App() {
    running = true;
    lastTick = Instant.now();
    tunezPlayerExecutor = Executors.newSingleThreadExecutor();
  }

  public static void init() {
    synchronized (LOCK) {
      if (instance != null) {
        throw new IllegalStateException("app already initialized");
      }

      instance = new App();
    }
  }

  public static App getSingleton() {
    if (instance == null) {
      throw new IllegalStateException("app not initialized");
    }
    return instance;
  }

  public void stop() {
    synchronized (LOCK) {
      if (instance == null) {
        throw new IllegalStateException("app not initialized");
      }

      stopCurrentSong();

      instance = null;
    }
  }

  public void interruptMainLoop() {
    running = false;
  }

  public void enterMainLoop() {
    mainLoop();
  }

  private void mainLoop() {
    while (running) {
      Instant tickStart = Instant.now();
      elapsedSinceLastTick = Duration.between(lastTick, tickStart);
      lastTick = tickStart;

      sleepForRestOfTick(tickStart);
    }
  }

  private void sleepForRestOfTick(Instant tickStart) {
    Duration tickDuration = Duration.between(tickStart, Instant.now());
    long sleepTime = TICK_EVERY.minus(tickDuration).toMillis();
    sleep(sleepTime);
  }

  private void sleep(long sleepTime) {
    try {
      TimeUnit.MILLISECONDS.sleep(sleepTime);
    } catch (InterruptedException ex) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void startRandom() {
    if (currentSong != null) {
      stopCurrentSong();
    }

    playRandom();

    while (currentSong != null) {
      stopCurrentSong();
      playRandom();
    }
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

  public void stopRandom() {
    stopCurrentSong();
  }

  private void stopCurrentSong() {
    if (currentSong != null) {
      currentSong.stop();
      currentSong = null;
    }
  }
}
