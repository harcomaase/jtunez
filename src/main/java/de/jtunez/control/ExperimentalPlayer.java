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
  private final InputStream sourceStream;
  private boolean playing = false;

  public ExperimentalPlayer(InputStream inputStream) {
    this.sourceStream = inputStream;
  }

  public void play() {

    try (AudioInputStream audioSourceStream = AudioSystem.getAudioInputStream(sourceStream)) {

      AudioFormat sourceFormat = audioSourceStream.getFormat();
      AudioFormat decodedFormat = createDecodedAudioFormat(sourceFormat);

      try (AudioInputStream decodedStream = AudioSystem.getAudioInputStream(decodedFormat, audioSourceStream)) {

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
        try (SourceDataLine outputLine = (SourceDataLine) AudioSystem.getLine(info)) {

          outputLine.open(decodedFormat);
          outputLine.start();

          playing = true;
          writeStreamToLine(decodedStream, outputLine);

          outputLine.drain();
        }
      }

    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void writeStreamToLine(final AudioInputStream decodedStream, final SourceDataLine sourceLine) throws IOException {
    int bytesRead;
    byte[] buffer = new byte[BUFFER_SIZE];
    while (playing && (bytesRead = decodedStream.read(buffer, 0, buffer.length)) > -1) {
      if (bytesRead >= 0) {
        adjustValuesForVolume(buffer, bytesRead);
        sourceLine.write(buffer, 0, bytesRead);
      }
    }
  }

  private AudioFormat createDecodedAudioFormat(AudioFormat sourceFormat) {
    AudioFormat decodedFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            sourceFormat.getSampleRate(),
            16,
            sourceFormat.getChannels(),
            sourceFormat.getChannels() * 2,
            sourceFormat.getSampleRate(),
            false);
    return decodedFormat;
  }

  public void stop() {
    playing = false;
    if (sourceStream != null) {
      try {
        sourceStream.close();
      } catch (IOException ex) {
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  private void adjustValuesForVolume(byte[] buffer, int bytesRead) {
    float volume = App.getSingleton().getVolume();
    for (int n = 0; n < bytesRead; n++) {
      buffer[n] *= volume;
    }
  }
}
