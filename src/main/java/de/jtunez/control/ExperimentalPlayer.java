package de.jtunez.control;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class ExperimentalPlayer {

  private final int BUFFER_SIZE = 128000;

  public void play(final InputStream sourceStream) {

    try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(sourceStream)) {

      AudioFormat audioFormat = audioInputStream.getFormat();

      DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
      try (SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(info)) {
        sourceLine.open(audioFormat);

        sourceLine.start();

        int bytesRead;
        byte[] buffer = new byte[BUFFER_SIZE];
        while ((bytesRead = audioInputStream.read(buffer, 0, buffer.length)) > -1) {
          if (bytesRead >= 0) {
            adjustValuesForVolume(buffer, bytesRead);
            sourceLine.write(buffer, 0, bytesRead);
          }
        }

        sourceLine.drain();
      }

    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
      Logger.getLogger(ExperimentalPlayer.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void adjustValuesForVolume(byte[] buffer, int bytesRead) {
    float volume = App.getSingleton().getVolume();
    for (int n = 0; n < bytesRead; n++) {
      buffer[n] *= volume;
    }
  }
}
