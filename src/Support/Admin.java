package Support;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Admin who manages transit systems. Each admin has an associated cardholder account. They can view
 * stats of different transit systems.
 */
public class Admin extends User {

  /** Logger */
  private static final Logger logger = Logger.getLogger("fileLogger");

  /** The list of transit systems managed by this admin */
  private ArrayList<TransitSystem> transitSystems;

  /** A Cardholder account associated with each admin. */
  private CardHolder associatedAccount;

  /**
   * Create an admin user who run the transit system.
   *
   * @param name the name of this admin user.
   * @param emailAddress the email address of this admin user.
   */
  public Admin(String name, String emailAddress, String password) {
    super(name, emailAddress, password);
    transitSystems = new ArrayList<>();
  }

  /**
   * Getter for the associated Cardholder account
   *
   * @return associated Cardholder account
   */
  public CardHolder getAssociatedAccount() {
    return this.associatedAccount;
  }

  /**
   * Setter for associatedAccount
   *
   * @param cardHolder the Cardholder account for the admin
   */
  public void setAssociatedAccount(CardHolder cardHolder) {
    this.associatedAccount = cardHolder;
    logger.info(String.format("Created new cardholder account for admin with email %s%n", email));
  }

  /**
   * Add a transit system for this admin to manage.
   *
   * @param ts the transit system to be added.
   */
  public void addTransitSystem(TransitSystem ts) {
    if (!this.transitSystems.contains(ts)) {
      this.transitSystems.add(ts);
    }
  }

  /**
   * Returns true iff this admin user manages the given transit system name.
   *
   * @param name the transit system name to be checked.
   */
  public boolean hasTransitSystemName(String name) {
    for (TransitSystem transitSystem : this.transitSystems) {
      if (transitSystem.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Print the daily reports of each transit system managed by this admin.
   *
   * @return the report in String format.
   */
  public String getDailyReport() {
    StringBuilder out = new StringBuilder();
    Double revenue = 0.0;
    Double cost = 0.0;
    Double totalRevenue = 0.0;
    Double totalCost = 0.0;

    for (TransitSystem ts : transitSystems) {
      revenue += ts.getTodayRevenue();
      cost += ts.getTodayCost();
      totalCost += ts.getTotalCost();
      totalRevenue += ts.getTotalRevenue();
    }

    totalCost += cost;
    totalRevenue += revenue;

    out.append(System.lineSeparator())
        .append(SimulatedTime.instance.getDate())
        .append(System.lineSeparator())
        .append(System.lineSeparator())
        .append(String.format("Revenue:       $%.2f", revenue))
        .append(System.lineSeparator())
        .append(System.lineSeparator())
        .append(String.format("Cost:          $%.2f", cost))
        .append(System.lineSeparator())
        .append(System.lineSeparator())
        .append(String.format("Profit:        $%.2f", revenue - cost))
        .append(System.lineSeparator())
        .append(System.lineSeparator())
        .append(System.lineSeparator())
        .append("Grand Statistics")
        .append(System.lineSeparator())
        .append(System.lineSeparator())
        .append(String.format("Total Revenue: $%.2f", totalRevenue))
        .append(System.lineSeparator())
        .append(System.lineSeparator())
        .append(String.format("Total Cost:    $%.2f", totalCost))
        .append(System.lineSeparator())
        .append(System.lineSeparator())
        .append(String.format("Total Revenue: $%.2f", totalRevenue - totalCost))
        .append(System.lineSeparator());

    if (transitSystems.size() != 0) return out.toString();
    else return "";
  }

  /**
   * Return the transit systems managed by this admin.
   *
   * @return ArrayList<TransitSystem>
   */
  public ArrayList<TransitSystem> getTransitSystems() {
    return transitSystems;
  }
}
