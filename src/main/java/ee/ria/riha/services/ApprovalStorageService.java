package ee.ria.riha.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

@Service
public class ApprovalStorageService {

  private Logger logger = LoggerFactory.getLogger(ApprovalStorageService.class);

  File file = new File("approval.properties");

  synchronized public void saveInfosystemApproval(String id, ZonedDateTime approvalTimestamp) {
    System.out.println(id + "|" + approvalTimestamp.toString());
    Properties properties = load();
    properties.setProperty(id, ZonedDateTime.ofInstant(approvalTimestamp.toInstant(), ZoneId.of("UTC")).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    save(properties);
  }

  private void save(Properties properties) {
    try (OutputStream outputStream = new FileOutputStream(file)) {
      properties.store(outputStream, null);
    }
    catch (IOException e) {
      logger.error("Could not save approvals", e);
      throw new RuntimeException(e);
    }
  }

  public Properties load() {
    try (InputStream inputStream = new FileInputStream(file)) {
      Properties properties = new Properties();
      properties.load(inputStream);
      return properties;
    }
    catch (IOException e) {
      logger.error("Could not load approvals", e);
      throw new RuntimeException(e);
    }
  }
}
