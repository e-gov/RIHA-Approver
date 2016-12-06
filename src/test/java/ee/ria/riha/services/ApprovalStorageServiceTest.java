package ee.ria.riha.services;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class ApprovalStorageServiceTest {

  private ApprovalStorageService service = new ApprovalStorageService();
  private Path storageFilePath;
  private ZonedDateTime now = ZonedDateTime.of(2016, 12, 12, 10, 5, 8, 456700000, ZoneId.of("Europe/Tallinn"));

  @Before
  public void setUp() throws Exception {
    storageFilePath = Files.createTempFile("", "");
    service.file = storageFilePath.toFile();
  }

  @Test
  public void saveInfosystemApproval_noExistingFile() throws IOException {
    service.saveInfosystemApproval("owner-id|infosystem-name", now);

    assertEquals("2016-12-12T08:05:08.4567", approvals().getProperty("owner-id|infosystem-name"));
  }

  @Test
  public void saveInfosystemApproval_existingFileWithData() throws IOException {
    Properties existingApprovals = approvals();
    existingApprovals.setProperty("other-owner-id|other-infosystem-name", "2016-12-12T01:01:01");
    existingApprovals.store(Files.newOutputStream(storageFilePath), null);

    service.saveInfosystemApproval("owner-id|infosystem-name", now);

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
          service.saveInfosystemApproval(Thread.currentThread().getName(), ZonedDateTime.now());
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