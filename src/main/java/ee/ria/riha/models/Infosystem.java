package ee.ria.riha.models;

import org.json.JSONObject;

public class Infosystem {

  private final JSONObject json;

  public Infosystem(JSONObject json) {
    this.json = json;
  }

  public String getId() {
    return json.getJSONObject("meta").getString("URI");
  }

  public void setApproved(String timestamp) {
    json.put("approved", timestamp);
  }
}
