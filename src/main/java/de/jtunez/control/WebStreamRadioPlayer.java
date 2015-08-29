package de.jtunez.control;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebStreamRadioPlayer implements Callable<Object> {

  private String webstreamUrl;

  TunezPlayer tunezPlayer;

  public WebStreamRadioPlayer(String url) {
    try {
      URLConnection connection = new URL(url).openConnection();
      connection.connect();
      StringBuilder response = new StringBuilder();
      try (InputStream inputStream = connection.getInputStream()) {
        byte[] buf = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buf)) > -1) {
          response.append(new String(buf, 0, bytesRead));
        }
      }

      Logger.getLogger(this.getClass().getName()).log(Level.INFO, "got response: {0}", response.toString());

      webstreamUrl = response.toString();

    } catch (MalformedURLException ex) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public Object call() throws Exception {

    InputStream inputStream = new URL(webstreamUrl).openStream();
    tunezPlayer = new TunezPlayer(inputStream);
    tunezPlayer.call();

    return null;
  }

  public void stop() {
    tunezPlayer.stop();
  }
}
