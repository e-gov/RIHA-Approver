package ee.ria.riha.models;

import lombok.Getter;

@Getter
public class Approval {
  String id;
  String timestamp;
  String status;

  public Approval(String id, String timestamp, String status) {
    this.id = id;
    this.timestamp = timestamp;
    this.status = status;
  }
}
