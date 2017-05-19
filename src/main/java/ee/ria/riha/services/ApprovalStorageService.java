package ee.ria.riha.services;

import ee.ria.riha.models.Approval;
import ee.ria.riha.models.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Service
public class ApprovalStorageService {
  @Autowired DateTimeService dateTimeService;
  private Logger logger = LoggerFactory.getLogger(ApprovalStorageService.class);
  List<String> loggedApprovals = new ArrayList<String>();
  File file = new File("approvals.db");

  synchronized public void saveInfosystemApproval(Approval approval) {
    Properties properties = loadProperties();
    properties.setProperty(approval.getUri(), approval.getTimestamp() + "|" + approval.getStatus() + "|" + approval.getToken()+ "|" + approval.getComment());
    
    String JWTBody = extractJWTBody(approval.getToken());
	String decodedBody = decodeBase64(JWTBody);
	String filteredToken = tokenStringFormatting(decodedBody);
    loggedApprovals.add(approval.getTimestamp()+ " | " +approval.getUri()+ " | "+ approval.getStatus() +" | "+ approval.getComment().replace("\n"," ") +" | "+ filteredToken);
    save(properties);
  }
  
  public List<Approval> allApprovals() {
    //todo review to use get..., setProperty
    return loadProperties().entrySet().stream().map(property -> {
      String[] value = ((String)property.getValue()).split("\\|");
      String filteredToken = formatToken(value[2]);
      
      return new Approval((String)property.getKey(), value[0], value[1], filteredToken, value[3]);
    }).collect(Collectors.toList());
  }
  
  public List<String> approvalLog(){
	 return loggedApprovals;
  }
  
  //A method for extracting JSON Web Tokens body from full JWT
  public String extractJWTBody(String token){
	  String fullJWT = token;
	  return fullJWT.substring(fullJWT.indexOf('.') + 1, fullJWT.lastIndexOf('.'));
  }

  //A method which takes in a base64 encoded string and returns the decoded version of it
  public String decodeBase64(String str){
	  byte[] byteArray = Base64.decodeBase64(str.getBytes());
	  String decodedString = new String(byteArray);
	  
	  return decodedString.replace("\"", "");
  }
  //A method which takes in a decoded token and formats it to extract only wanted data
  public String tokenStringFormatting(String token){
	  String approverName = token.substring(token.indexOf("nimi"), token.indexOf("} },")+3);
	  String approverInstitution = token.substring(token.indexOf("asutus"), token.indexOf("}, rollid"));
	  return approverName + ", " + approverInstitution;
  }
  //Method which uses previous helper methods to take in token and return decoded and formated string
  public String formatToken(String token) {
	  String decodedBody = decodeBase64(extractJWTBody(token));
	  return tokenStringFormatting(decodedBody);
  }
  
  public List<Approval> approvedApprovals() {
    return allApprovals().stream().filter(a -> a.getStatus().equals(Status.APPROVED.getValue())).collect(Collectors.toList());
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
