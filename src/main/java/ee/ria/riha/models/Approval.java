package ee.ria.riha.models;

import lombok.Getter;

@Getter
public class Approval {
  String id;
  String timestamp;

  public Approval(String id, String timestamp) {
    this.id = id;
    this.timestamp = timestamp;
  }
}
