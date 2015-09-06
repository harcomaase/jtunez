package de.jtunez.control;

import de.jtunez.control.config.WebradioStreamRegistry;
import de.jtunez.control.exception.PlayerException;
import de.jtunez.entity.WebradioStream;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class App {

  private static App instance;
  //
  private boolean running;
  private Instant lastTick;
  private Duration elapsedSinceLastTick;
  //
  private final ExecutorService playerExecutor;
  private RandomSongPlayer randomSongPlayer;
  private WebradioStreamPlayer webradioStreamPlayer;
  //
  private float volume = 1.0f;
  private WebradioStream currentWebradioStream;
  //
  private static final Duration TICK_EVERY = Duration.ofSeconds(5);
  //
  private static final Object LOCK = new Object();

  private App() {
    running = true;
    lastTick = Instant.now();
    playerExecutor = Executors.newSingleThreadExecutor(new DaemonThreadFactory());
    currentWebradioStream = WebradioStreamRegistry.fallback();
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

      if (randomSongPlayer != null) {
        stopRandom();
      }

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
    stopRandom();

    randomSongPlayer = new RandomSongPlayer();
    playerExecutor.submit(randomSongPlayer);
  }

  public void stopRandom() {
    if (randomSongPlayer == null) {
      return;
    }
    randomSongPlayer.stopCurrentSong();
    randomSongPlayer = null;
  }

  public void startWebradio() throws PlayerException {
    stopWebradio();
    webradioStreamPlayer = new WebradioStreamPlayer(currentWebradioStream);
    playerExecutor.submit(webradioStreamPlayer);
  }

  public void stopWebradio() {
    if (webradioStreamPlayer == null) {
      return;
    }
    webradioStreamPlayer.stop();
    webradioStreamPlayer = null;
  }

  public void adjustVolume(final float volume) {
    if (volume < 0f || volume > 1f) {
      return;
    }
    this.volume = volume;
  }

  public float getVolume() {
    return volume;
  }

  public WebradioStream getCurrentWebradioStream() {
    return currentWebradioStream;
  }

  public void setCurrentWebradioStream(WebradioStream currentWebradioStream) throws PlayerException {
    boolean isPlaying = webradioStreamPlayer != null;
    stopWebradio();
    this.currentWebradioStream = currentWebradioStream;
    if (isPlaying) {
      startWebradio();
    }
  }
}
