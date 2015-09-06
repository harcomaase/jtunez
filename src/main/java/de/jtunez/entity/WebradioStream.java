package de.jtunez.entity;

public class WebradioStream {

  public WebradioStream() {
  }

  public WebradioStream(String id, String label, String streamUrl) {
    this.id = id;
    this.label = label;
    this.streamUrl = streamUrl;
  }
  private String id;
  private String label;
  private String streamUrl;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getStreamUrl() {
    return streamUrl;
  }

  public void setStreamUrl(String streamUrl) {
    this.streamUrl = streamUrl;
  }
}
