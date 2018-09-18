package Support;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/** Class to characterise a trip by time, location, etc. */
public class Trip implements Serializable {

  /** The name of the TransitSystem it is associated with. */
  private TransitSystem transitSystem;

  /** The shortest path (in terms of station/stop) from the start location to end location. */
  private ArrayList<Station> path;

  /** The starting station/stop of the trip. */
  private Station start;

  /** The ending station/stop of the trip. */
  private Station end;

  /** The starting and ending time in format of hh:mm. */
  private LocalTime startTime, endTime;

  /** The day when this trip takes place. */
  private LocalDate day;

  /** Indicates whether this is a subway or bus ride. */
  private boolean isSubway;

  /** The amount of fare associated with this trip. */
  private double fare;

  /**
   * Constructor for Trip class. Intended for intersecting lines.
   *
   * @param startRoute the line/route where the start station/stop is in.
   * @param endRoute the line/route where the end station/stop is in.
   * @param start the starting station/stop.
   * @param end the ending station/stop.
   * @param ts the TransitSystem where the trip takes place.
   */
  public Trip(Route startRoute, Route endRoute, Station start, Station end, TransitSystem ts) {
    this.start = start;
    this.end = end;
    this.isSubway = start.isSubway() && end.isSubway();
    this.transitSystem = ts;

    this.path = generateMultiPath2(startRoute, start, endRoute, end);
  }

  /**
   * Sets the time of starting time and ending time of the trip.
   *
   * @param day the day when this trip takes place.
   * @param startTime the starting time of this trip in 24 hour format.
   * @param endTime the ending time of this trip in 24 hour format.
   */
  public void setTime(LocalDate day, LocalTime startTime, LocalTime endTime) {
    this.day = day;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  /**
   * Generates the shortest path between start and end stations
   *
   * @param startRoute route of the stations of path.
   * @param start the station the path begins with.
   * @param end the station the path ends with.
   * @return the path of stations.
   */
  private ArrayList<Station> generatePath(Route startRoute, Station start, Station end) {
    ArrayList<Station> path = new ArrayList<>();

    Station startWith = start;
    Station endWith = end;

    if (start.getId() > end.getId()) {
      startWith = end;
      endWith = start;
    }

    boolean startLoading = false, endLoading = false;

    for (Station station : startRoute) {
      if (station.equals(startWith)) startLoading = true;
      if (station.equals(endWith)) endLoading = true;

      if (startLoading) path.add(station);
      if (endLoading) break;
    }

    if (startWith == end) {
      ArrayList<Station> tempPath = new ArrayList<>();
      for (int i = 0; i < path.size(); i++) {
        tempPath.add(path.get(path.size() - i - 1));
      }
      path = tempPath;
    }

    return path;
  }

  /**
   * Generates the shortest path between start and end stations with one intersection. Algorithm
   * breaks the trip into two parts. From start to transfer then from transfer to end.
   *
   * @param startRoute the route where the starting station is in.
   * @param start the station the path begins with.
   * @param endRoute the route where the ending station is in.
   * @param end the station the path ends with.
   * @return the path made of stations
   */
  private ArrayList<Station> generateMultiPath2(
      Route startRoute, Station start, Route endRoute, Station end) {
    ArrayList<Station> path = new ArrayList<>();
    ArrayList<Station> path1 = new ArrayList<>();
    int length = Integer.MAX_VALUE;

    if (start.getName().equals(end.getName())) {
      path.add(start);
      return path;
    }

    if (startRoute.hasStation(end.getName())) {
      path1.addAll(generatePath(startRoute, start, startRoute.findStation(end.getName())));
      if (path1.size() < length) {
        length = path1.size();
        path = path1;
      }
      path1 = new ArrayList<>();
    }
    if (endRoute.hasStation(start.getName())) {
      path1.addAll(generatePath(endRoute, endRoute.findStation(start.getName()), end));
      if (path1.size() < length) {
        length = path1.size();
        path.clear();
        path.addAll(path1);
      }
      path1 = new ArrayList<>();
    }
    ArrayList<Station> transferStations = startRoute.getTransferStation();

    for (Station transfer : transferStations) {
      if (endRoute.hasStation(transfer.getName())) {
        path1.addAll(generatePath(startRoute, start, transfer));
        if (path1.size() != 0) path1.remove(path1.size() - 1);
        path1.addAll(generatePath(endRoute, endRoute.findStation(transfer.getName()), end));

        if (path1.size() != 0
            && path1.get(0).getName().equals(start.getName())
            && path1.get(path1.size() - 1).getName().equals(end.getName())) {
          if (path1.size() < length) {
            length = path1.size();
            path = path1;
          }
        }
      }
      path1 = new ArrayList<>();
    }
    return path;
  }

  /**
   * Returns the length of the trip in terms of stations.
   *
   * @return number of stations/stops in the trip
   */
  public int getPathLength() {
    return this.path.size();
  }

  /**
   * Returns the starting location of this trip.
   *
   * @return the starting station/stop of the trip.
   */
  private Station getStartStation() {
    return start;
  }

  /**
   * Returns the ending location of this trip/
   *
   * @return the ending station/stop of the trip.
   */
  private Station getEndStation() {
    return end;
  }

  /**
   * Calculates and returns the total travel from startTime to endTime.
   *
   * @return total time travelled in minutes.
   */
  public long getTripTime() {
    return SimulatedTime.instance.getElapsedMinutes(startTime, endTime);
  }

  /**
   * Calculates the total fare charged based on the number of stations reached id by subway.
   *
   * @return the amount required to be charged for this trip.
   */
  public double getFarePaid(double feeCap) {
    boolean duringRushHour =
        SimulatedTime.instance.duringRushHour(startTime)
            && SimulatedTime.instance.duringRushHour(endTime);
    double rushHourMultiplier = duringRushHour ? 1.5 : 1.0;

    if (!isSubway) return rushHourMultiplier * 2.00;
    else {
      return rushHourMultiplier
          * ((path.size() != 0) ? Math.min((this.path.size() - 1) * 0.5, feeCap) : 0);
    }
  }

  /**
   * Gets the fare paid for the trip.
   *
   * @return trip's fare.
   */
  public double getFare() {
    return this.fare;
  }

  /**
   * Alters the record of the trip's fare paid.
   *
   * @param amount fare that was paid
   */
  public void setFare(double amount) {
    this.fare = amount;
  }

  /**
   * Calculates the operation cost associated with this trip. Calculated by the number of
   * stations/stops reached multiply a constant associated with the Support.TransitSystem.
   *
   * @return the total operation cost for the Support.TransitSystem.
   */
  public double getOperationCost(double costPerStation) {
    return (this.path.size() - 1) * costPerStation;
  }

  /**
   * Returns whether this was a subway or bus ride.
   *
   * @return if this trip was subway ride or not.
   */
  public boolean isSubway() {
    return isSubway;
  }

  @Override
  public String toString() {
    return String.format(
        "Trip: %15s to %-15s | Date: %-12s | Duration: %-3d | Fare: %-5.2f ",
        getStartStation().getName(),
        getEndStation().getName(),
        day.toString(),
        getTripTime(),
        getFare());
  }
}
