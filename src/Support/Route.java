package Support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Describes the basic structure of such a route or line containing station/stop name, id, and x, y
 * coordinates
 */
public class Route implements Serializable, Iterable<Station> {
  public RouteTypes routeType;
  private int routeNum;
  private ArrayList<Station> routeStops;
  private Station start;
  private Station end;
  private int numStations;

  /**
   * Creates a new route
   *
   * @param type the type of route [Bus, Subway]
   * @param routeNum the number of the route in its transit system
   */
  public Route(RouteTypes type, int routeNum) {
    this.routeType = type;
    this.routeNum = routeNum;
    this.routeStops = new ArrayList<>();
    this.numStations = 0;
  }

  /**
   * Returns whether this is a bus or subway route or line.
   *
   * @return the RouteType of the Support.Route.
   */
  public String getRouteType() {
    return this.routeType.toString();
  }

  /**
   * Return the number of this route in its transit system
   *
   * @return the route number
   */
  private int getRouteNum() {
    return routeNum;
  }

  /**
   * Reassign the route's number
   *
   * @param newRouteNum the route's new number
   */
  public void setRouteNum(int newRouteNum) {
    this.routeNum = newRouteNum;
  }

  /**
   * Return a list of all stops/stations this route serves
   *
   * @return list of route's stops
   */
  public ArrayList<Station> getStations() {
    return routeStops;
  }

  /**
   * Return the first station of this route
   *
   * @return first station of this route
   */
  public Station getStart() {
    return start;
  }

  /**
   * Return the last station of this route
   *
   * @return last station of this route
   */
  public Station getEnd() {
    return end;
  }

  /**
   * Return all the stations able to transfer to another line in this Route
   *
   * @return list of stations
   */
  public ArrayList<Station> getTransferStation() {
    ArrayList<Station> transferStations = new ArrayList<>();

    for (Station station : this.routeStops) {
      if (station.isTransfer()) {
        transferStations.add(station);
      }
    }
    return transferStations;
  }

  /**
   * Return the number of stations this route serves.
   *
   * @return the number of stations in the route
   */
  private int getNumStations() {
    return numStations;
  }

  /**
   * Returns the station found with input name.
   *
   * @param name the name of the station to be found.
   * @return the Support.Station to be found, null if it does not exist.
   */
  public Station findStation(String name) {
    for (Station station : routeStops) {
      if (station.getName().equals(name)) return station;
    }
    return null;
  }

  /**
   * Determine if this route contains a station with input name
   *
   * @param name the staion name to be searched for in route.
   * @return if the station exists in this route.
   */
  public boolean hasStation(String name) {
    for (Station station : routeStops) {
      if (station.getName().equals(name)) return true;
    }
    return false;
  }

  /**
   * Adds a station to the current route. Update number of Stations in the route. Assigns the
   * station to field start or end or both if applicable;
   *
   * @param station The station to be added to the route.
   */
  public void addStation(Station station) {
    if (numStations == 0) this.start = station;

    routeStops.add(station);
    numStations += 1;
    end = station;
  }

  @Override
  public boolean equals(Object other) {
    if (other == null) return false;
    if (other == this) return true;
    if (!(other instanceof Route)) return false;

    if (this.routeNum != ((Route) other).getRouteNum()) return false;
    else if (this.numStations != ((Route) other).getNumStations()) return false;
    else if (!this.end.equals(((Route) other).getEnd())) return false;
    else {
      ArrayList<Station> otherRoute = ((Route) other).getStations();
      for (int i = 0; i < numStations; i++) {
        if (!routeStops.get(i).equals(otherRoute.get(i))) return false;
      }
    }
    return true;
  }

  @Override
  public Iterator<Station> iterator() {
    return this.routeStops.iterator();
  }
}
