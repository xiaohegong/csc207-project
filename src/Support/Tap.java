package Support;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/** Each tap that cardholder makes. Stores information about route, station of the tap. */
public class Tap implements Serializable {
  /** The types of Card.Tap, NULL for the first element in ArrayList to avoid EmptyStackException */
  private TapTypes type;
  /** the Support.Station the tap occurred at */
  private Station station;
  /** the date the tap occurred on */
  private LocalDate travelDay;
  /** the time of day when the tap occurred */
  private LocalTime time;
  /** if true, the tap was at a subway station. otherwise, the tap was at a bus stop */
  private boolean isSubway;
  /** if true, the tap was used to transfer between routes */
  private boolean transfer;
  /** the name of the transit system the tap occurred on */
  private String systemName;

  private Route route;

  /**
   * Creates a new tap instance
   *
   * @param type the type of tap (entry or exit)
   * @param station the station where the tap occurred
   * @param transfer if true, the tap was to transfer between two routes
   * @param systemName the transit system of the tap
   */
  public Tap(TapTypes type, Route route, Station station, boolean transfer, String systemName) {
    this.type = type;
    this.route = route;
    this.station = station;
    this.travelDay = SimulatedTime.instance.getDate();
    this.time = SimulatedTime.instance.getTime();
    if (station != null) this.isSubway = station.isSubway();
    this.transfer = transfer;
    this.systemName = systemName;
  }

  /**
   * Returns the route of the tap
   *
   * @return route of the tap
   */
  public Route getRoute() {
    return route;
  }

  /**
   * Return the station where the tap occurred
   *
   * @return station
   */
  public Station getStation() {
    return station;
  }

  /**
   * Return true if the tap occurred at a subway station, false if tapped at a bus stop
   *
   * @return whether the tap occurred at a subway or not
   */
  public boolean isSubway() {
    return isSubway;
  }

  /**
   * Return the time the tap occurred
   *
   * @return tap time
   */
  public LocalTime getTime() {
    return time;
  }

  /**
   * Return the name of the transit system the tapped station belonged to
   *
   * @return transit system's name
   */
  public String getSystemName() {
    return systemName;
  }

  /**
   * Return the type of tap this was (entry/exit)
   *
   * @return tap type
   */
  public String getTapTypes() {
    return this.type.toString();
  }

  /**
   * Return true if this tap was used to transfer between routes
   *
   * @return whether this tap was to transfer.
   */
  public boolean isTransfer() {
    return this.transfer;
  }
}
