package ee.ria.riha.controllers;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class InfoSystemControllerTest {

  @Test
  public void updateStatus() {

    String result = new InfoSystemController().updateStatus();

    assertEquals("{\"approved\": \"2016-05-12\"}", result);
    fail();
  }
}