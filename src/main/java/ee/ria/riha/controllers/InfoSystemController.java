package ee.ria.riha.controllers;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.ZonedDateTime;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class InfoSystemController {

  @RequestMapping(value = "/", method = GET)
  public String index() {
    return "index";
  }

  @RequestMapping(value = "/save/", method = POST, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public String updateStatus(){
    //todo persist change (under lock - load, change, save)

    //todo use correct datetime format in result JSON

    //todo use GSON or better spring to convert object e.g. hashmap
    return "{\"approved\": \"" + ZonedDateTime.now() + "\"}";
  }
}