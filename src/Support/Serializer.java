package Support;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Serializer {

  /** Logger */
  private static final Logger logger = Logger.getLogger("fileLogger");
  /**
   * Writes the given Serializable <obj> to file at <filePath>.
   *
   * @param filePath the file to write the records to
   * @param obj a Serializable object that will be
   */
  public static void saveToFile(String filePath, Serializable obj) {
    try {
      OutputStream file = new FileOutputStream(filePath);
      ObjectOutput output = new ObjectOutputStream(new BufferedOutputStream(file));

      // serialize the list of users
      output.writeObject(obj);
      output.close();
      logger.info(String.format("Serialized objects in %s%n", filePath));
    } catch (IOException e) {
      logger.log(Level.SEVERE, String.format("Cannot serialize objects in %s%n.", filePath), e);
    }
  }

  /**
   * Reads the Serializable objects from recorded file at <filePath>.
   *
   * @param filePath the file to read the records from
   * @return Object the output object deserialized from file
   * @throws ClassNotFoundException possible exception to be thrown
   */
  public static Object readFromFile(String filePath) throws ClassNotFoundException {
    try {
      InputStream file = new FileInputStream(filePath);
      ObjectInput input = new ObjectInputStream(new BufferedInputStream(file));

      // deserialize the object
      Object obj = input.readObject();
      input.close();
      logger.info(String.format("De-serialized objects in %s%n", filePath));
      return obj;
    } catch (IOException ex) {
      logger.log(Level.SEVERE, String.format("Cannot deserialize objects in %s%n.", filePath), ex);
    }
    return null;
  }
}
