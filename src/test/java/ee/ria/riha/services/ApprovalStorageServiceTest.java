package ee.ria.riha.services;

import ee.ria.riha.models.Approval;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class ApprovalStorageServiceTest {

  private ApprovalStorageService service = spy(new ApprovalStorageService());
  private Path storageFilePath;

  @Before
  public void setUp() throws Exception {
    storageFilePath = Files.createTempFile("", "");
    service.file = storageFilePath.toFile();
  }

  @Test
  public void load() {
    Properties properties = new Properties();
    properties.setProperty("/owner-2/shortname-2", "2015-10-10T01:10:10");
    properties.setProperty("/owner-1/shortname-1", "2016-01-01T10:00:00");
    doReturn(properties).when(service).loadProperties();

    List<Approval> result = service.load();

    assertEquals(2, result.size());
    assertEquals("/owner-1/shortname-1", result.get(0).getId());
    assertEquals("2016-01-01T10:00:00", result.get(0).getTimestamp());
    assertEquals("/owner-2/shortname-2", result.get(1).getId());
    assertEquals("2015-10-10T01:10:10", result.get(1).getTimestamp());
  }

  @Test
  public void saveInfosystemApproval_noExistingFile() throws IOException {
    service.saveInfosystemApproval("owner-id|infosystem-name", "2016-12-12T08:05:08.4567");

    assertEquals("2016-12-12T08:05:08.4567", approvals().getProperty("owner-id|infosystem-name"));
  }

  @Test
  public void saveInfosystemApproval_existingFileWithData() throws IOException {
    Properties existingApprovals = approvals();
    existingApprovals.setProperty("other-owner-id|other-infosystem-name", "2016-12-12T01:01:01");
    existingApprovals.store(Files.newOutputStream(storageFilePath), null);

    service.saveInfosystemApproval("owner-id|infosystem-name", "2016-12-12T08:05:08.4567");

    Properties approvals = approvals();
    assertEquals(2, approvals.size());
    assertEquals("2016-12-12T08:05:08.4567", approvals.getProperty("owner-id|infosystem-name"));
    assertEquals("2016-12-12T01:01:01", approvals.getProperty("other-owner-id|other-infosystem-name"));
  }

  @Test
  public void saveInfosystemApproval_isThreadSafe() throws IOException, InterruptedException {
    List<Thread> threads = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      Thread thread = new Thread(() -> {
        try {
          service.saveInfosystemApproval(Thread.currentThread().getName(), "2016-12-12T08:05:08.4567");
        }
        catch (Throwable e) {
          e.printStackTrace();
        }
      }, String.valueOf("thread" + i));
      threads.add(thread);
    }

    threads.forEach(Thread::start);

    for (Thread thread : threads) {
      thread.join();
    }

    assertEquals(10, approvals().size());
  }

  private Properties approvals() throws IOException {
    try(InputStream inputStream = Files.newInputStream(storageFilePath)) {
      Properties properties = new Properties();
      properties.load(inputStream);
      return properties;
    }
  }
}