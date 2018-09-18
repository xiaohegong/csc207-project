package Support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Logger;

/** Manager class to manage all users */
public class UserManager implements Serializable {
  private static final Logger logger = Logger.getLogger("fileLogger");
  /** The list of users managed by this UserManager */
  protected ArrayList<User> users;
  /** The transitManager assigned */
  protected TransitManager transitManager;
  /** Whether this is a Admin user or a CardHolder user. */
  private boolean isAdmin;

  /**
   * The constructor for UserManager. Initializes a list of users and associates itself with the
   * appropriate TransitManager.
   *
   * @param isAdmin whether this user is an Admin or a CardHolder.
   * @param transitManager the TransitManager it is associated with.
   */
  public UserManager(boolean isAdmin, TransitManager transitManager) {
    this.isAdmin = isAdmin;
    users = new ArrayList<>();
    this.transitManager = transitManager;
  }

  /**
   * Returns the list of all users. Mainly for serialization purposes.
   *
   * @return list of all users.
   */
  public ArrayList<User> getUsers() {
    return this.users;
  }

  /**
   * Sets the list of users for serialization purposes.
   *
   * @param users list of users to be set.
   */
  public void setUsers(ArrayList<User> users) {
    this.users = users;
  }
  /**
   * Creates a new User by name and email where email is unique.
   *
   * @param name the name of the new User.
   * @param email the unique email of the new User.
   * @return the new User created. Returns null if the account already exists.
   */
  public User create(String name, String email, String password) {
    User user = find(email);
    if (user != null) {
      return null;
    } else {
      if (isAdmin) {
        user = new Admin(name, email, password);
        logger.info(String.format("Created new admin with email %s%n", email));
      } else {
        user = new CardHolder(name, email, password);
        logger.info(String.format("Created new cardholder with email %s%n", email));
      }
      users.add(user);
    }
    return user;
  }

  /**
   * Finds and returns an User with the given email. Returns null if the user does not exist.
   *
   * @param email the email for finding the correct User.
   * @return the User to be found if it exists.
   */
  public User find(String email) {
    for (User user : users) {
      if (user.getEmail().equals(email)) {
        return user;
      }
    }
    return null;
  }
}
