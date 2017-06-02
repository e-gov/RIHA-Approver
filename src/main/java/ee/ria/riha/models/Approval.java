package ee.ria.riha.models;

import lombok.Getter;

@Getter
public class Approval {
  String uri;
  String timestamp;
  String status;
  String token;
  String comment;

  public Approval(String uri, String timestamp, String status, String token, String comment) {
    this.uri = uri;
    this.timestamp = timestamp;
    this.status = status;
    this.token = token;
    this.comment = comment;
  }
}
