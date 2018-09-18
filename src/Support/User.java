package Support;

import java.io.Serializable;
import java.util.logging.Logger;

/** Class for Users with name and email. Email is not changeable and unique. */
public class User implements Serializable {
  /** Logger */
  static final Logger logger = Logger.getLogger("fileLogger");
  /** The user's email address */
  protected final String email;
  /** The user's password */
  protected String password;
  /** The user's name */
  protected String name;
  /** Whether this user authorizes autoload */
  private boolean autoload = false;
  /** The minimum amount the user set to autoload */
  private double minAmount;

  /**
   * Creates a new uesr. Note that Support.User must be subclassed in order to instantiate.
   *
   * @param name the user's name
   * @param emailAddress the user's unique email
   */
  public User(String name, String emailAddress, String password) {
    this.name = name;
    this.email = emailAddress;
    this.password = password;
  }

  /**
   * Returns whether the inputted password <password> is a valid password. A valid password must
   * have length at least 8 and contains at least one number and one letter.
   *
   * @param password String
   * @return boolean
   */
  private static boolean isValidPassword(String password) {
    return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{8,}$");
  }

  /**
   * Returns whether the inputted name <name> is a valid name. A valid name must not contain only
   * white spaces. In addition name must only have alphabetical letters and must not have more than
   * 15 characters
   *
   * @param name String
   * @return boolean
   */
  private static boolean isValidName(String name) {
    return name.matches("[ a-zA-Z]+") && name.length() <= 15 && name.trim().length() > 0;
  }

  /**
   * Return the name of this user.
   *
   * @return name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Change the user's name.
   *
   * @param name new user name
   */
  public boolean setName(String name) {
    if (isValidName(name)) {
      this.name = name;
      logger.info(String.format("Successfully reset name for user with email %s%n", email));
      return true;
    }
    return false;
  }

  /**
   * Return the email address of this user.
   *
   * @return email address
   */
  public String getEmail() {
    return this.email;
  }
  /**
   * Return the password of this user.
   *
   * @return password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the password of this user to the input.
   *
   * @param password users password.
   * @return true if password is correct.
   */
  public boolean setPassword(String password) {
    if (isValidPassword(password)) {
      this.password = password;
      logger.info(String.format("Successfully reset password for user with email %s%n", email));
      return true;
    }
    return false;
  }

  /**
   * Outputs the name requirements.
   *
   * @return string that indicates name requirements.
   */
  public String nameRequirement() {
    return "Name must contain alphabetical letters only."
        + System.lineSeparator()
        + "Name must not have more than 15 characters.";
  }
  /**
   * Outputs the email requirements.
   *
   * @return string that indicates email requirements.
   */
  public String emailRequirement() {
    return "Email must follow the format of <username>@<host>.<suffix>"
        + System.lineSeparator()
        + "Please enter a valid email address since it cannot be changed once registered.";
  }

  /**
   * Outputs the password requirements.
   *
   * @return string that indicates password requirements.
   */
  public String passwordRequirement() {
    return "Password is invalid!"
        + System.lineSeparator()
        + "A valid password must have at least 8 characters."
        + System.lineSeparator()
        + "A valid password must contains at least one number and one letter.";
  }

  /**
   * Checks if the input email and password matches when user tries to login.
   *
   * @param password the password to be checked for correctness.
   * @return whether the login is successful.
   */
  public boolean login(String password) {
    boolean loginSuccessful = this.password.equals(password);
    if (loginSuccessful) {
      logger.info(String.format("User %s logged in successfully.%n", email));
    } else {
      logger.warning(String.format("User %s failed to login.%n", email));
    }
    return loginSuccessful;
  }

  /**
   * Returns whether or not this user approves to autoload cards when fall below minimum balance.
   */
  public boolean allowAutoLoad() {
    return this.autoload;
  }

  /** Returns the minimum amount to activate autoload for this user. */
  public Double getMinAmount() {
    return this.minAmount;
  }

  /**
   * Sets the autoload approval status of this user to the inputted status, and set the minimum
   * amount from the user from the inputted value minAmount.
   *
   * @param status boolean
   * @param minAmount Double
   */
  public void setAutoLoad(boolean status, Double minAmount) {
    this.autoload = status;
    this.minAmount = minAmount;
  }
}
