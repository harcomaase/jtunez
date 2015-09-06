package de.jtunez.control.config;

import de.jtunez.entity.WebradioStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO: (maybe) persist in db, make editable via webinterface
public final class WebradioStreamRegistry {

  private WebradioStreamRegistry() {
  }

  private static final List<WebradioStream> STREAMS;

  static {
    STREAMS = new LinkedList<>();

    STREAMS.add(new WebradioStream("YOU_FM", "you fm", "http://metafiles.gl-systemhaus.de/hr/youfm_2.m3u"));
    STREAMS.add(new WebradioStream("HR3", "hr3", "http://metafiles.gl-systemhaus.de/hr/hr3_2.m3u"));
    STREAMS.add(new WebradioStream("HR_INFO", "hr-info", "http://www.metafilegenerator.de/HR/hrinfo/mp3/webm.m3u"));
    STREAMS.add(new WebradioStream("HR1", "hr1", "http://metafiles.gl-systemhaus.de/hr/hr1_2.m3u"));
    STREAMS.add(new WebradioStream("HR2", "hr2-kultur", "http://metafiles.gl-systemhaus.de/hr/hr2_2.m3u"));
    STREAMS.add(new WebradioStream("HR4", "hr4", "http://metafiles.gl-systemhaus.de/hr/hr4_2.m3u"));
  }

  public static List<WebradioStream> getStreams() {
    return STREAMS;
  }

  public static WebradioStream fallback() {
    return getStreams().get(0);
  }

  public static WebradioStream getStreamById(String streamId) {
    if (streamId == null) {
      return fallback();
    }
    Optional<WebradioStream> stream = getStreams()
            .stream()
            .filter(s -> streamId.equals(s.getId()))
            .findFirst();

    if (stream.isPresent()) {
      return stream.get();
    } else {
      Logger.getLogger(WebradioStreamRegistry.class.getName()).log(Level.WARNING, "stream ''{0}'' not found. falling back", streamId);
      return fallback();
    }
  }
}
