package ee.ria.riha.services;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ee.ria.riha.services.LoggerClass;

public class UseLogger {
    // use the classname for the logger, this way you can refactor
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);


    public void doSomeThingAndLog() {
        // ... more code


        // set the LogLevel to Info, severe, warning and info will be written
        // finest is still not written
        LOGGER.setLevel(Level.INFO);
        LOGGER.severe("Info Log");
        LOGGER.finest("Really not important");
    }

    public static void main(String[] args) {
        UseLogger tester = new UseLogger();
        try {
            LoggerClass.setup();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Problems with creating the log files");
        }
        tester.doSomeThingAndLog();
    }
}