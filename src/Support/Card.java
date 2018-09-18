package Support;

import javafx.scene.control.Alert;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

/**
 * Cards that are held by cardholders. Contains information about its ID, balance, list of past
 * trips taken, and current taps such as when the card was tapped in.
 */
public class Card implements Serializable {

  private static final Logger logger = Logger.getLogger("fileLogger");
  private static int cardCount = 0;
  private String id;
  private double balance;
  private boolean isSuspended = false;
  private ArrayList<Trip> tripHistory;
  private CardHolder holder;
  private Stack<Tap> tapStack;
  private long remainingTime;

  /**
   * Constructor for Support.Card
   *
   * @param holder the holder this Support.Card is associated with.
   */
  public Card(CardHolder holder) {
    this.tripHistory = new ArrayList<>();
    this.balance = 19.00;
    Card.cardCount++;
    this.id = String.valueOf(this.hashCode());
    this.holder = holder;

    this.tapStack = new Stack<>();

    // There is always this empty tap to avoid EmptyStackException for convenience
    this.tapStack.push(new Tap(TapTypes.NULL, null, null, false, null));

    this.remainingTime = 0;
  }

  /**
   * Return the unique id of this card
   *
   * @return id of this card
   */
  public String getID() {
    return this.id;
  }

  /**
   * Suspend this card. When a card is suspended, it can not be used to enter or exit vehicles
   *
   * @param flag state to set card suspension. if true, card will be suspended. if false, card will
   *     be un-suspended.
   */
  public void suspend(boolean flag) {
    this.isSuspended = flag;
  }

  /**
   * Return true if the card is suspended.
   *
   * @return whether the card is suspended
   */
  public boolean isSuspended() {
    return this.isSuspended;
  }

  /**
   * Return the full trip history of this card.
   *
   * @return list of all trips taken by this card.
   */
  public ArrayList<Trip> getTripHistory() {
    return tripHistory;
  }

  /**
   * Returns the 3 most recent trips taken by this card.
   *
   * @return list of Support.Trip.
   */
  public List<Trip> getMostRecentTrips() {
    return tripHistory.subList(Math.max(0, tripHistory.size() - 3), tripHistory.size());
  }

  /**
   * Allows a card to enter the specified location. Determines whether this trip is a transfer
   * (within time limit specified by the Support.TransitSystem). If within time limit, it checks
   * whether the transfer is at the same location to determine whether the time limit cap applies or
   * not
   *
   * @param start the name of the stop/station to begin trip.
   * @param transitSystem the Support.TransitSystem associated with the trip.
   */
  public void enter(Route startRoute, Station start, TransitSystem transitSystem) {
    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle("Warning");
    alert.setHeaderText("Unable to Enter!");

    Tap lastTap = tapStack.peek();
    LocalDateTime now = SimulatedTime.instance.now();
    LocalDate day = now.toLocalDate();
    LocalTime time = now.toLocalTime();

    if (isSuspended) {
      alert.setContentText("Card is suspended. You may not enter!");
      logger.warning(String.format("Card %s unable to tap in: card is suspended.%n", id));
    } else if (balance < 0) {
      alert.setContentText("Card has insufficient balance!");
      logger.warning(String.format("Card %s unable to tap in: has insufficient balance.%n", id));
    } else if (lastTap.getTapTypes().equals("ENTRY")) {
      alert.setContentText("Already in transit!");
      logger.warning(
          String.format(
              "Card %s unable to tap in: already in transit. Please tap out first.%n", id));
    } else {
      boolean transfer = false;

      if (!lastTap.getTapTypes().equals("NULL")) {
        if (lastTap.getStation().sameLocation(start))
          if (SimulatedTime.instance.getElapsedMinutes(lastTap.getTime(), time) <= remainingTime)
            transfer = true;
        if (!transfer) resetTime(transitSystem.TIME_LIMIT);
      }

      tapStack.push(new Tap(TapTypes.ENTRY, startRoute, start, transfer, transitSystem.getName()));
      alert.setAlertType(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Entering");
      alert.setHeaderText("Enter successful!");
      alert.setContentText("You have entered " + start.getName() + ".");

      logger.info(
          String.format(
              "Card %s entered %s at %s.%n",
              id, start.getName(), SimulatedTime.instance.getTime()));
    }

    alert.showAndWait();
  }

  /**
   * Allows a card to exit the specified location and calculates the appropriate fee. - If passenger
   * transfers within the time limit of the Transit System, such transfer is free. - Fee is capped
   * at an amount specified by Support.TransitSystem. - Fee cap does not apply to disjointed trips
   * (end and start at different locations).
   *
   * @param end the name of the stop/station.
   * @param ts the Support.TransitSystem associated with the trip.
   * @param forced true if exit was forced by transit system closing or other external circumstances
   */
  public void exit(Route endRoute, Station end, TransitSystem ts, boolean forced) {
    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle("Warning");
    alert.setHeaderText("Unable to Exit!");

    Tap lastTap = tapStack.peek();

    if (!lastTap.getTapTypes().equals("ENTRY")) {
      alert.setContentText("There was no start!");
      logger.warning(
          String.format(
              "Card %s is unable to exit station %s: it never entered transit!%n",
              id, end.getName()));
    } else if (lastTap.isSubway() != end.isSubway()) {
      alert.setContentText("Not travelling in the same transit type!");
      logger.warning(
          String.format(
              "Card %s is unable to exit station %s: it is not the same type of transit as the station the cardholder entered in!%n",
              id, end.getName()));
    } else if (!ts.getName().equals(lastTap.getSystemName())) {
      alert.setContentText("Not travelling on the same system!");
      logger.warning(
          String.format(
              "Card %s is unable to exit station %s: it does not belong to the same system as the station the cardholder entered in!%n",
              id, end.getName()));
    } else {
      Route startRoute = lastTap.getRoute();

      Trip trip = new Trip(startRoute, endRoute, lastTap.getStation(), end, ts);

      if (trip.getPathLength() == 0) {
        alert.setContentText("There is no connection between the two stations/stops!");
      } else {

        trip.setTime(
            SimulatedTime.instance.getDate(), lastTap.getTime(), SimulatedTime.instance.getTime());

        if (lastTap.isTransfer()) trip.setFare(0);
        else {
          double fare = trip.getFarePaid(ts.FEE_CAP);
          trip.setFare(fare);
          chargeFare(fare);
          ts.addRevenue(fare);
        }

        if (forced) {
          double forcedFare = ts.FEE_CAP;
          trip.setFare(forcedFare);
          chargeFare(forcedFare);
          ts.addRevenue(forcedFare);
        }

        tripHistory.add(trip);
        holder.recordTrip(trip);

        reduceTime(trip.getTripTime());
        if (remainingTime <= 0) resetTime(ts.TIME_LIMIT);

        ts.addCost(trip.getOperationCost(ts.COST_PER_STATION));

        tapStack.push(new Tap(TapTypes.EXIT, endRoute, end, lastTap.isTransfer(), ts.getName()));

        alert.setAlertType(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exiting");
        alert.setHeaderText("Exit successful!");
        alert.setContentText("You have exited " + end.getName() + ".");
        logger.info(
            String.format(
                "Card %s exited %s at %s%n", id, end.getName(), SimulatedTime.instance.getTime()));
      }
    }
    alert.showAndWait();
  }

  /**
   * Forces the card to exit the Transit System and deduct a penalty of maximum amount.
   *
   * @param ts the TransitSystem of which the card is forced to exit from.
   */
  public void forceExit(TransitSystem ts) {
    Tap lastTap = getLastTap();

    if (lastTap.getTapTypes().equals("ENTRY")) {
      exit(lastTap.getRoute(), lastTap.getStation(), ts, true);
    }
  }

  /**
   * Reduces the remaining time allowed for transfer by input amount.
   *
   * @param timeSpent the amount of time to be reduced from remainingTime
   */
  private void reduceTime(long timeSpent) {
    this.remainingTime -= timeSpent;
  }

  public Tap getLastTap() {
    return tapStack.peek();
  }

  /**
   * Resets the remaining time for transfer. This means that either a new disjointed trip has
   * started or the TIME_LIMIT specified by Support.TransitSystem has passed
   *
   * @param timeLimit the time limit for valid transfer specified by Support.TransitSystem
   */
  private void resetTime(long timeLimit) {
    this.remainingTime = timeLimit;
  }

  /**
   * Returns the balance of the card
   *
   * @return remaining balance of card
   */
  public double getBalance() {
    return this.balance;
  }

  /**
   * Charges the fare by input amount from balance. If this user allows autoload when this user's
   * card balances fall below the setted amount, add $10 to this user's balance.
   *
   * @param amount the amount to be charged.
   */
  private void chargeFare(double amount) {
    this.balance -= amount;
    logger.info(
        String.format(
            "$%f is charged from card #%s held by user %s%n", amount, id, holder.getEmail()));
    if (this.holder.allowAutoLoad()) {
      if (this.balance < this.holder.getMinAmount()) {
        this.addBalance(10);
      }
    }
  }

  /**
   * Adds the balance to the card by an appropriate amount. Only $10, $20, or $50 is allowed.
   *
   * @param amount the amount the user wishes to be added
   * @return whether adding the amount is successful i.e. amount was appropriate.
   */
  boolean addBalance(int amount) {
    if (amount != 10 && amount != 20 && amount != 50) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("Warning");
      alert.setHeaderText("Fail to add balance");
      alert.setContentText(
          "Failed to raise funds by $"
              + +amount
              + ". Funds can only be raised by $10, $20, or $50");
      alert.showAndWait();
    } else {
      this.balance += amount;
      return true;
    }
    return false;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof Card && ((Card) o).getID().equals(this.id));
  }

  @Override
  public String toString() {
    return String.format("Card ID %s ($%.2f )", id, balance);
  }
}
