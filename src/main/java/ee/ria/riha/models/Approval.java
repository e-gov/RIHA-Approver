package ee.ria.riha.models;

import lombok.Getter;

@Getter
public class Approval {
  String JWT;
  String uri;
  String timestamp;
  String status;

  public Approval(String JWT, String uri, String timestamp, String status) {
	this.JWT = JWT;
    this.uri = uri;
    this.timestamp = timestamp;
    this.status = status;
  }
}
