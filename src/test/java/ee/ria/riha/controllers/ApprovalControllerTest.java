package ee.ria.riha.controllers;

import ee.ria.riha.models.Approval;
import ee.ria.riha.services.ApprovalStorageService;
import ee.ria.riha.services.DateTimeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApprovalControllerTest {

  @Mock ApprovalStorageService storageService;
  @Mock DateTimeService dateTimeService;

  @Spy @InjectMocks
  ApprovalController controller = new ApprovalController();

  @Test
  public void updateApprovalStatus() {
    doNothing().when(storageService).saveInfosystemApproval(any(Approval.class));
    ZonedDateTime approvalTimestamp = ZonedDateTime.of(2016, 12, 12, 10, 10, 10, 0, ZoneId.of("Europe/Tallinn"));
    doReturn(approvalTimestamp).when(dateTimeService).now();

    String result = controller.updateApprovalStatus("Authorization token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ", "http://base.url/infosystem", "MITTE KOOSKÕLASTATUD", "kommentaar");

    JSONAssert.assertEquals("{\"uri\":\"http://base.url/infosystem\",\"timestamp\":\"2016-12-12T08:10:10\",\"status\":\"MITTE KOOSKÕLASTATUD\",\"token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ\",\"comment\":\"kommentaar\"}", result, true);
    ArgumentCaptor<Approval> approvalCaptor = ArgumentCaptor.forClass(Approval.class);
    verify(storageService).saveInfosystemApproval(approvalCaptor.capture());
    Approval approval = approvalCaptor.getValue();
    assertEquals(approval.getUri(), "http://base.url/infosystem");
    assertEquals(approval.getTimestamp(), "2016-12-12T08:10:10");
    assertEquals(approval.getStatus(), "MITTE KOOSKÕLASTATUD");
    assertEquals(approval.getToken(), "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ");
    assertEquals(approval.getComment(), "kommentaar");
  }

  @Test
  public void approvals() {
    List<Approval> approvals = asList(new Approval("http://base.url/shortname1", "2016-01-01T10:00:00", "MITTE KOOSKÕLASTATUD", "testJWT", "kommentaar"), new Approval("http://base.url/shortname2", "2015-10-10T01:10:10", "KOOSKÕLASTATUD", "testJWT", "kommentaar"));
    doReturn(approvals).when(storageService).allApprovals();

    String result = controller.approvals();

    String expected = "[{\"uri\":\"http://base.url/shortname1\",\"timestamp\":\"2016-01-01T10:00:00\",\"token\":\"testJWT\",\"status\":\"MITTE KOOSKÕLASTATUD\",\"comment\":\"kommentaar\"}," +
      "{\"uri\":\"http://base.url/shortname2\",\"timestamp\":\"2015-10-10T01:10:10\",\"token\":\"testJWT\",\"status\":\"KOOSKÕLASTATUD\",\"comment\":\"kommentaar\"}]";
    JSONAssert.assertEquals(expected, result, true);
  }
}