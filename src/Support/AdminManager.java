package Support;

/** Manager class for admins, allows admins to add transit systems to their jurisdiction. */
public class AdminManager extends UserManager {
  /**
   * The constructor for AdminManager. Initializes a list of admins and associates itself with the
   * appropriate TransitManager.
   *
   * @param transitManager the TransitManager it is associated with.
   */
  public AdminManager(TransitManager transitManager) {
    super(true, transitManager);
  }

  /**
   * Adds clearance to view certain TransitSystem to an admin.
   *
   * @param email the email specifying the admin.
   * @param tsName the TransitSystem name of which to be added.
   */
  public void addManagingTransitSystem(String email, String tsName) {
    Admin admin = (Admin) super.find(email);
    TransitSystem ts = transitManager.getTransitSystem(tsName);
    admin.addTransitSystem(ts);
  }

  @Override
  public User create(String name, String email, String password) {
    User user = super.create(name, email, password);
    if (user instanceof Admin) {
      CardHolder cardHolder = new CardHolder(name, email, password);
      ((Admin) user).setAssociatedAccount(cardHolder);
    }

    return user;
  }
}
