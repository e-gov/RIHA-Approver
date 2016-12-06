package ee.ria.riha.controllers;

import ee.ria.riha.services.ApprovalStorageService;
import ee.ria.riha.services.DateTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static ee.ria.riha.services.DateTimeService.format;
import static ee.ria.riha.services.DateTimeService.toUTC;
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

  @RequestMapping(value = "/approve/", method = POST, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public String approve(@RequestParam String id){
    //todo use GSON or better spring to convert object e.g. hashmap
    String approvalTimestamp = format(toUTC(dateTimeService.now()));
    approvalStorageService.saveInfosystemApproval(id, approvalTimestamp);
    return "{\"approved\": \"" + approvalTimestamp + "\"}";
  }
}