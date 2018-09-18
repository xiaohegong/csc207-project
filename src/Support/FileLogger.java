package Support;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Purpose of this class is to create a file logger in the Support package that is initialized in
 * Control.Main
 */
public class FileLogger {
  /** Initiates the logger */
  private static final Logger logger = Logger.getLogger("fileLogger");
  /** Uses handler to allow logger to write to log.txt */
  public static void init() {
    // initiate logger
    logger.setLevel(Level.ALL);

    try {
      FileHandler fileHandler = new FileHandler("log.txt", true);
      fileHandler.setLevel(Level.ALL);
      fileHandler.setFormatter(new SimulatedTimeFormatter());
      logger.addHandler(fileHandler);
    } catch (IOException e) {
      logger.severe(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
    }
  }
}
