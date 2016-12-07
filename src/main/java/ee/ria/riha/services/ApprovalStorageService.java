package ee.ria.riha.services;

import ee.ria.riha.models.Approval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
public class ApprovalStorageService {

  @Autowired DateTimeService dateTimeService;

  private Logger logger = LoggerFactory.getLogger(ApprovalStorageService.class);

  File file = new File("approvals.db");

  synchronized public void saveInfosystemApproval(String id, String approvalTimestamp) {
    Properties properties = loadProperties();
    properties.setProperty(id, approvalTimestamp);
    save(properties);
  }

  public List<Approval> load() {
    return loadProperties().entrySet().stream().map(property -> new Approval((String)property.getKey(), (String)property.getValue())).collect(Collectors.toList());
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

  Properties loadProperties() {
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
