package Support;

import javafx.scene.control.Alert;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

/**
 * Class generalizing day pass, monthly pass. Passes do not hold any information about trips taken.
 */
public class TermPass implements Serializable {
  /** Logger */
  private static final Logger logger = Logger.getLogger("fileLogger");
  /** Cost of purchasing such a pass */
  private double cost;
  /** Length of validity of the pass */
  private int length;
  /** Purchase date of the pass */
  private LocalDate purchaseDate;

  /** Cardholder associated with the pass */
  private CardHolder holder;

  /**
   * Constructor for the pass
   *
   * @param holder cardholder who holds the pass
   * @param length length of validity
   * @param cost cost of pass
   */
  TermPass(CardHolder holder, int length, double cost) {
    this.holder = holder;
    purchaseDate = SimulatedTime.instance.getDate();
    this.length = length;
    this.cost = cost;
  }

  /**
   * Returns the length of validity of the pass.
   *
   * @return length of validity of the pass.
   */
  public int getLength() {
    return length;
  }

  /**
   * Checks whether the pass is still valid. Sets <isValid> upon checking.
   *
   * @param date date to check if pass is still valid then
   * @return validity of the pass at specified date
   */
  public boolean checkValidity(LocalDate date) {
    return ChronoUnit.DAYS.between(purchaseDate, date) <= length;
  }

  /**
   * Getter for cost of pass
   *
   * @return cost of the pass
   */
  public double getCost() {
    return this.cost;
  }

  /**
   * Returns the expiry date of the pass based on the purchase date
   *
   * @return expiry date of pass
   */
  public LocalDate getExpiryDate() {
    return purchaseDate.plusDays(length);
  }

  /**
   * Allows a pass to enter the specified location.
   *
   * @param start the name of the stop/station to begin trip.
   */
  public void enter(Station start) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Entering");
    alert.setHeaderText("Enter successful!");
    alert.setContentText("You have entered " + start.getName() + ".");

    logger.info(
        String.format(
            "Pass entered %s at %s.%n", start.getName(), SimulatedTime.instance.getTime()));

    alert.showAndWait();
  }

  /**
   * Allows a card to exit the specified location.
   *
   * @param end the name of the stop/station.
   */
  public void exit(Station end) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

    alert.setTitle("Exiting");
    alert.setHeaderText("Exit successful!");
    alert.setContentText("You have exited " + end.getName() + ".");
    logger.info(
        String.format("Pass exited %s at %s%n", end.getName(), SimulatedTime.instance.getTime()));

    alert.showAndWait();
  }

  @Override
  public String toString() {
    return "Pass: expiry date: " + getExpiryDate().toString();
  }
}
