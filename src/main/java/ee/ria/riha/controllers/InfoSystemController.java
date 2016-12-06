package ee.ria.riha.controllers;

import ee.ria.riha.services.ApprovalStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.ZonedDateTime;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class InfoSystemController {

  @Autowired ApprovalStorageService approvalStorageService;

  @RequestMapping(value = "/", method = GET)
  public String index() {
    return "index";
  }

  @RequestMapping(value = "/approve/", method = POST, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public String approve(@RequestParam String id){
    //todo use correct datetime format in result JSON

    //todo use GSON or better spring to convert object e.g. hashmap
    ZonedDateTime approvalTimestamp = now();
    approvalStorageService.saveInfosystemApproval(id, approvalTimestamp);
    return "{\"approved\": \"" + approvalTimestamp + "\"}";
  }

  ZonedDateTime now() {
    return ZonedDateTime.now();
  }
}