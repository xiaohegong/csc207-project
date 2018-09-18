package Support;

import java.io.Serializable;

/** Station class to store information about stations. */
public class Station implements Serializable {
  /** Should not be ever changed since a station cannot be physically moved */
  private final int x, y;
  /** Station id */
  private int id;
  /** Name of the station */
  private String name;
  /** whether the station is subway station */
  private boolean isSubway;
  /** whether the station is an intersection of two lines */
  private boolean isTransfer;

  /**
   * Create new station
   *
   * @param id the unique id of this station
   * @param name the common name of this station
   * @param x x coordniate
   * @param y y coordinate
   */
  public Station(int id, String name, boolean isSubway, int x, int y, boolean isTransfer) {
    this.id = id;
    this.name = name;
    this.isSubway = isSubway;
    this.x = x;
    this.y = y;
    this.isTransfer = isTransfer;
  }

  /**
   * Returns if station is intersection of multiple lines
   *
   * @return boolean
   */
  public boolean isTransfer() {
    return isTransfer;
  }

  /**
   * Return the station's unique ID
   *
   * @return station id
   */
  public int getId() {
    return id;
  }

  /**
   * Return the station's common name
   *
   * @return station name
   */
  public String getName() {
    return name;
  }

  /**
   * Reassign the common name of the station
   *
   * @param newName the station's new name
   */
  public void setName(String newName) {
    this.name = newName;
  }

  /**
   * Returns the location of the station in array format.
   *
   * @return The location in (x, y) format.
   */
  public int[] getLocation() {
    return new int[] {x, y};
  }

  /**
   * Checks if this station has the same location (x, y) with another station.
   *
   * @param other the station/stop to test location
   * @return if they have the same location.
   */
  public boolean sameLocation(Station other) {
    return x == other.x && y == other.y;
  }

  /**
   * Returns whether the station is a subway station
   *
   * @return boolean
   */
  public boolean isSubway() {
    return this.isSubway;
  }

  @Override
  public String toString() {
    return this.name;
  }

  @Override
  public boolean equals(Object other) {
    if (other == null) return false;
    if (other == this) return true;
    if (!(other instanceof Station)) return false;
    else {
      int[] location = {x, y};
      return (id == ((Station) other).id)
          && (name.equals(((Station) other).getName()))
          && (location == ((Station) other).getLocation());
    }
  }
}
