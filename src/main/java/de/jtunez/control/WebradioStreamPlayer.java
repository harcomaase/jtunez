package de.jtunez.control;

import de.jtunez.control.exception.PlayerException;
import de.jtunez.entity.WebradioStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebradioStreamPlayer implements Callable<Object> {

  private final String webstreamUrl;
  private ExperimentalPlayer experimentalPlayer;

  public WebradioStreamPlayer(final WebradioStream webradioStream) throws PlayerException {
    try {
      final URLConnection connection = new URL(webradioStream.getStreamUrl()).openConnection();
      connection.connect();
      final StringBuilder response = new StringBuilder();
      try (InputStream inputStream = connection.getInputStream()) {
        final byte[] buf = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buf)) > -1) {
          response.append(new String(buf, 0, bytesRead));
        }
      }

      Logger.getLogger(this.getClass().getName()).log(Level.INFO, "got response: {0}", response.toString());

      webstreamUrl = response.toString();

    } catch (IOException ex) {
      throw new PlayerException("error during webstream initialisation", ex);
    }
  }

  @Override
  public Object call() throws Exception {

    final InputStream inputStream = new URL(webstreamUrl).openStream();
    experimentalPlayer = new ExperimentalPlayer(inputStream);
    experimentalPlayer.play();

    return null;
  }

  public void stop() {
    if (experimentalPlayer != null) {
      experimentalPlayer.stop();
      experimentalPlayer = null;
    }
  }
}
