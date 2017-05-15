package ee.ria.riha.services;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsingLogger {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	public void logApprovals(String approvals) {
		try {
            LoggerClass.setup();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Problems with creating the log files");
        }
		
        LOGGER.setLevel(Level.SEVERE);
        LOGGER.severe(approvals);
        LOGGER.warning("Info Log");
        LOGGER.info("Info Log");
        LOGGER.finest("Really not important");
        
    }
	
}
