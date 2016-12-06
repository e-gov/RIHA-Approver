package ee.ria.riha.controllers;

import ee.ria.riha.models.Infosystem;
import ee.ria.riha.services.ApprovalStorageService;
import ee.ria.riha.services.DateTimeService;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Properties;

import static ee.ria.riha.services.DateTimeService.format;
import static ee.ria.riha.services.DateTimeService.toUTC;
import static org.apache.http.client.fluent.Request.Get;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class InfoSystemController {

  @Autowired ApprovalStorageService approvalStorageService;
  @Autowired DateTimeService dateTimeService;

  @RequestMapping(value = "/", method = GET)
  public String index() {
    return "index";
  }

  @RequestMapping(value = "/infosystems/", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public String infosystems() {
    Properties approvals = approvalStorageService.load();

    String harvestedData = harvestedData();
    JSONArray jsonArray = new JSONArray(harvestedData);
    for (int i = 0; i < jsonArray.length(); i++) {
      Infosystem infosystem = new Infosystem(jsonArray.getJSONObject(i));
      if (approvals.containsKey(infosystem.getId())) {
        infosystem.setApproved(approvals.getProperty(infosystem.getId()));
      }
    }
    return jsonArray.toString();
  }

  String harvestedData() {
    try {
      return Get("https://raw.githubusercontent.com/e-gov/RIHA-API/master/riha_live.json")
        .connectTimeout(5000)
        .socketTimeout(5000)
        .execute().returnContent().asString();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @RequestMapping(value = "/approve/", method = POST, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public String approve(@RequestParam String id){
    //todo use GSON or better spring to convert object e.g. hashmap
    String approvalTimestamp = format(toUTC(dateTimeService.now()));
    approvalStorageService.saveInfosystemApproval(id, approvalTimestamp);
    return "{\"approved\": \"" + approvalTimestamp + "\"}";
  }
}