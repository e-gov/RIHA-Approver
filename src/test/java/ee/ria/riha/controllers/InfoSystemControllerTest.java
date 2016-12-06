package ee.ria.riha.controllers;

import ee.ria.riha.services.ApprovalStorageService;
import ee.ria.riha.services.DateTimeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InfoSystemControllerTest {

  @Mock ApprovalStorageService storageService;
  @Mock DateTimeService dateTimeService;

  @Spy @InjectMocks
  InfoSystemController controller = new InfoSystemController();

  @Test
  public void updateStatus() {
    doNothing().when(storageService).saveInfosystemApproval(anyString(), anyString());
    ZonedDateTime approvalTimestamp = ZonedDateTime.of(2016, 12, 12, 10, 10, 10, 0, ZoneId.of("Europe/Tallinn"));
    doReturn(approvalTimestamp).when(dateTimeService).now();

    String result = controller.approve("owner|infosystem");

    assertEquals("{\"approved\": \"2016-12-12T08:10:10\"}", result);
    verify(storageService).saveInfosystemApproval("owner|infosystem", "2016-12-12T08:10:10");
  }

  @Test
  public void infosystems() {
    doReturn("{}").when(controller).harvestedData();

    String result = controller.infosystems();

    assertEquals("{}", result);
  }
}