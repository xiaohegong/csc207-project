package Support;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/** TransitManager to manage all transit systems including time. */
public class TransitManager implements Serializable, Iterable<TransitSystem> {
  /** logger */
  private static final Logger logger = Logger.getLogger("fileLogger");
  /**
   * Current date of the transit manager - persists until day is closed (to allow for days ending
   * past midnight)
   */
  private LocalDate businessDate;
  /** associates transit system names with the system itself */
  private Map<String, TransitSystem> transitSystemMap;

  /** Creates new Transit Manager. */
  public TransitManager() {
    businessDate = SimulatedTime.instance.getDate();
    transitSystemMap = new HashMap<>();
  }

  /** @return ArrayList of TransitSystemMap.values. */
  public ArrayList<TransitSystem> getTransitSystems() {
    return new ArrayList<>(transitSystemMap.values());
  }

  /** @return ArrayList of TransitSystemMap.values. */
  public LocalDate getBusinessDate() {
    return this.businessDate;
  }

  /**
   * Sets passed in date to business date.
   *
   * @param date date of the application.
   */
  public void setBusinessDate(LocalDate date) {
    this.businessDate = date;
  }

  /**
   * Sets up the transit system list.
   *
   * @param list the list that is being set for the the system map.
   */
  public void setTransitSystemMap(ArrayList<TransitSystem> list) {
    transitSystemMap.clear();
    for (TransitSystem ts : list) {
      this.transitSystemMap.put(ts.getName(), ts);
    }
  }

  /**
   * Adds a new Support.TransitSystem to TransitManager.
   *
   * @param systemName The name of the Support.TransitSystem to be added.
   */
  public void addTransitSystem(String systemName) {
    boolean exists = false;

    for (String name : transitSystemMap.keySet()) {
      if (name.equals(systemName)) {
        exists = true;
        break;
      }
    }
    if (!exists) transitSystemMap.put(systemName, new TransitSystem(systemName));
  }

  /**
   * Return whether the given <name> is a valid TransitSystem.
   *
   * @param name The name of the Support.TransitSystem to be checked.
   * @return boolean
   */
  public boolean tsExists(String name) {
    return transitSystemMap.containsKey(name);
  }

  /**
   * Retrieves the TransitSystem by the name specified.
   *
   * @param name The name of the Support.TransitSystem to be found.
   * @return The Support.TransitSystem to be found.
   */
  public TransitSystem getTransitSystem(String name) {
    return transitSystemMap.get(name);
  }

  /** Add today's revenue and cost to ArrayList and reset to 0. */
  public void closeDay() {
    logger.info(String.format("Closing day %s.%n", this.getBusinessDate()));
    for (TransitSystem ts : this) {
      ts.closeDay(this.businessDate);
    }
    this.incrementDate();
  }

  /** Increments the current day by 1 day */
  private void incrementDate() {
    this.businessDate = this.businessDate.plusDays(1);
  }

  /** Updates the business date to the current instance of the system ß */
  private void updateDate() {
    this.businessDate = SimulatedTime.instance.getDate();
  }

  @Override
  public Iterator<TransitSystem> iterator() {
    return this.getTransitSystems().iterator();
  }
}
