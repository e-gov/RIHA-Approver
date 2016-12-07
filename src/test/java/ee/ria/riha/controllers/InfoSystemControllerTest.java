package ee.ria.riha.controllers;

import ee.ria.riha.models.Approval;
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
import java.util.List;

import static java.util.Arrays.asList;
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
  public void approvals() {
    List<Approval> approvals = asList(new Approval("/owner/shortname1", "2016-01-01T10:00:00"), new Approval("/owner/shortname2", "2015-10-10T01:10:10"));
    doReturn(approvals).when(storageService).load();

    String result = controller.approvals();

    assertEquals("[{\"id\":\"/owner/shortname1\",\"timestamp\":\"2016-01-01T10:00:00\"},{\"id\":\"/owner/shortname2\",\"timestamp\":\"2015-10-10T01:10:10\"}]", result);
  }
}