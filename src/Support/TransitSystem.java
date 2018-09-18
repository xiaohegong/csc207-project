package Support;

import javafx.collections.FXCollections;
import javafx.scene.chart.XYChart;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A transit system with bus and subway services. Keeps track of the revenue, cost of operation.
 * Contains list of bus and subway routes.
 */
public class TransitSystem implements Serializable {
  /** Logger */
  private static final Logger logger = Logger.getLogger("fileLogger");
  /** The cost the TransitSystem is associated for each ride a person has. */
  public final double COST_PER_STATION = 0.02;
  /** The time limit of the TransitSystem for fee cap */
  public final long TIME_LIMIT = 2;
  /** The fee cap for the transit system */
  public final double FEE_CAP = 6;
  /** Name of the transit system */
  private String name;
  /** Revenue of the day */
  private double todayRevenue = 0;
  /** Cost of the day */
  private double todayCost = 0;
  /** The current date */
  private LocalDate date;
  /** Map of the date and revenue */
  private Map<LocalDate, Double> dailyRevenue;
  /** Map of the date and cost */
  private Map<LocalDate, Double> dailyCost;
  /** Map of bus routes and route numbers */
  private Map<Integer, Route> busRoutes;
  /** Map of subway lines and line numbers */
  private Map<Integer, Route> subwayLines;

  /**
   * Constructor for the Transit system
   *
   * @param systemName name of the transit system
   */
  public TransitSystem(String systemName) {
    this.name = systemName;
    dailyRevenue = new HashMap<>();
    dailyCost = new HashMap<>();
    busRoutes = new HashMap<>();
    subwayLines = new HashMap<>();
    this.date = SimulatedTime.instance.getDate();
  }

  /**
   * Return the name of the transit system
   *
   * @return name of transit system
   */
  public String getName() {
    return name;
  }

  /**
   * Adds a route/line with route/line number to either busRoutes or subwayRoutes.
   *
   * @param routeNum The route number the route is associated with.
   * @param route The route to be added to the system.
   */
  public void addRoute(int routeNum, Route route) {
    if (route.getRouteType().equals("Bus")) busRoutes.put(routeNum, route);
    else if (route.getRouteType().equals("Subway")) subwayLines.put(routeNum, route);
  }

  /**
   * Returns all the bus routes or subway lines of the system
   *
   * @param isSubway whether to retrun bus route or subway line
   * @return bus route or subway line
   */
  public ArrayList<Integer> getAllRoutes(boolean isSubway) {
    return (isSubway) ? new ArrayList<>(subwayLines.keySet()) : new ArrayList<>(busRoutes.keySet());
  }

  /**
   * Returns the rout/line associated with the specified route number.
   *
   * @param isSubway Whether to retrieve subway or bus route.
   * @param routeNum The route/line number the route is associated with.
   * @return The route/line associated with the specified route number.
   */
  public Route getRoute(boolean isSubway, int routeNum) {
    return (isSubway) ? subwayLines.get(routeNum) : busRoutes.get(routeNum);
  }

  /** Closes day but resetting all today's stats to 0 and tabulate result in Maps. */
  public void closeDay(LocalDate businessDate) {
    dailyRevenue.put(businessDate, todayRevenue);
    dailyCost.put(businessDate, todayCost);
    todayCost = 0;
    todayRevenue = 0;
    logger.info(String.format("%s transit system closed for the day!", getName()));
    logger.info(
        String.format(
            "Total Profit: $%.2f%n%n",
            getTotalRevenue()
                - getTotalCost())); // TODO: should this be daily revenue/cost, not total?
  }

  /**
   * Gets the gross revenue for this day. Resets upon day closing.
   *
   * @return today's gross revenue
   */
  public double getTodayRevenue() {
    return todayRevenue;
  }

  /**
   * Gets the total cost for this day. Resets upon day closing.
   *
   * @return today's cost
   */
  public double getTodayCost() {
    return todayCost;
  }

  /**
   * Increase revenue by amount. Used for statistical and bookkeeping purposes.
   *
   * @param amount amount to be added
   */
  public void addRevenue(double amount) {
    todayRevenue += amount;
  }

  /**
   * Increase cost by amount. Used for statistical and bookkeeping purposes.
   *
   * @param amount amount to be added
   */
  public void addCost(double amount) {
    todayCost += amount;
  }

  /**
   * Return a map of the daily revenue earned on every day on record so far.
   *
   * @return A Map that takes days as keys and returns revenue values
   */
  public Map<LocalDate, Double> getDailyRevenue() {
    return dailyRevenue;
  }

  /**
   * Return a map of the daily cost on every day on record so far.
   *
   * @return A Map that takes days as keys and returns cost values
   */
  public Map<LocalDate, Double> getDailyCost() {
    return dailyCost;
  }

  /**
   * Returns the total revenue of the Support.TransitSystem.
   *
   * @return the total revenue.
   */
  public double getTotalRevenue() {
    return getTotal(dailyRevenue);
  }

  /**
   * Returns the total cost of the Support.TransitSystem.
   *
   * @return the total cost.
   */
  public double getTotalCost() {
    return getTotal(dailyCost);
  }

  /**
   * Calculates the total of values in a Map.
   *
   * @param lst the Map of which its values are to be totaled.
   * @return the total amount.
   */
  private double getTotal(Map<LocalDate, Double> lst) {
    double total = 0;

    for (double individual : lst.values()) {
      total += individual;
    }
    return total;
  }

  @Override
  public String toString() {
    return this.name;
  }

  /**
   * Outputs the statistical data for graph displaying. The statistics will be from the given date
   * to today.
   *
   * @param date LocalDate
   * @return The report of stats.
   */
  public XYChart.Series<String, Double> getRevenueData(LocalDate date) {
    long daysFromToday = ChronoUnit.DAYS.between(date, SimulatedTime.instance.getDate());

    XYChart.Series<String, Double> data = new XYChart.Series<>();
    data.setName("Revenue since " + date.toString());
    LocalDate today = SimulatedTime.instance.getDate();

    // Add today's revenue
    data.getData().add(new XYChart.Data<>(today.toString(), todayRevenue));

    // Add data from before today
    for (int i = 1; i < daysFromToday + 1; i++) {
      LocalDate key = today.minusDays(i);
      if (dailyRevenue.containsKey(key)) {
        data.getData().add(new XYChart.Data<>(key.toString(), dailyRevenue.get(key)));
      } else {
        break;
      }
    }

    FXCollections.reverse(data.getData());
    return data;
  }
}
