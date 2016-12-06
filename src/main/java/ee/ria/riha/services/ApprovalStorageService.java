package ee.ria.riha.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Properties;

@Service
public class ApprovalStorageService {

  private Logger logger = LoggerFactory.getLogger(ApprovalStorageService.class);

  File file = new File("approvals.db");

  synchronized public void saveInfosystemApproval(String id, String approvalTimestamp) {
    Properties properties = load();
    properties.setProperty(id, approvalTimestamp);
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
    if (!file.exists()) return new Properties();
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