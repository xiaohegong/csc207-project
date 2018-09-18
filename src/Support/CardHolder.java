package Support;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Cardholder class. Each cardholder contains a factory class for cards. They can perform different
 * actions on cards such as add/remove, suspend/un-suspend.
 */
public class CardHolder extends User {
  /** Keep the trips taken by this cardHolder. */
  private ArrayList<Trip> tripHistory;
  /** Instance of Support.CardManager to manage this user's cards */
  private CardManager cm;

  /** Passes the cardholder holds */
  private ArrayList<TermPass> passes;
  /** This card holder's average cost (based on expenditure) */
  private double monthlyCost = 0;
  /** This card holder's average number of trips taken */
  private int monthlyNumTrips = 0;

  /**
   * Initialize a new Support.CardHolder with name <name> and email address <emailAddress>. Initiate
   * and assign a new CardManager for this CardHolder.
   *
   * @param name the name of the new card holder
   * @param emailAddress the email address of the new card holder
   */
  public CardHolder(String name, String emailAddress, String password) {
    super(name, emailAddress, password);
    this.cm = new CardManager();
    tripHistory = new ArrayList<>();
    passes = new ArrayList<>();
  }

  /**
   * Adds a pass to the cardholder's possession. Length is either 1 or [(end of month) - today]
   *
   * @param length length of validity of the pass
   * @param cost cost of the pass
   * @return pass created
   */
  public TermPass addPass(int length, double cost) {
    TermPass pass = new TermPass(this, length, cost);
    passes.add(pass);
    monthlyCost += cost;

    if (length == 1) logger.info(String.format("Added new day pass for account %s%n", email));
    else
      logger.info(
          String.format(
              "Added new monthly pass for %s for account %s%n",
              SimulatedTime.instance.getDate().getMonth().toString(), email));
    return pass;
  }

  /**
   * Updates all passes in cardholder's possession. Removes a pass if no longer valid.
   *
   * @param today the date of update
   */
  public void updatePasses(LocalDate today) {
    ArrayList<Integer> removalIndices = new ArrayList<>();

    for (int i = 0; i < passes.size(); i++) {
      TermPass pass = passes.get(i);
      if (!pass.checkValidity(today)) {
        removalIndices.add(i);
        if (pass.getLength() == 1)
          logger.info(String.format("A day pass has been expired for account %s%n", email));
        else logger.info(String.format("A monthly pass has been expired for account %s%n", email));
      }
    }

    for (Integer index : removalIndices) {
      passes.remove((int) index);
    }
  }

  /**
   * Returns all passes in possession of cardholder
   *
   * @return list of passes
   */
  public ArrayList<TermPass> getPasses() {
    return passes;
  }

  /**
   * Return the list of all cards owned by this cardHolder, including suspended ones.
   *
   * @return ArrayList of all cards the cardHolder owns,
   */
  public ArrayList<Card> getAllCards() {
    ArrayList<Card> cards = new ArrayList<>(this.cm.getCards());
    cards.addAll(this.cm.getSuspendedCards());
    return cards;
  }

  /**
   * Return the list of active cards owned by this CardHolder.
   *
   * @return ArrayList of active Cards the CardHolder owns
   */
  public ArrayList<Card> getActiveCards() {
    return this.cm.getCards();
  }

  /**
   * Return the list of all suspended cards owned by the cardholder.
   *
   * @return list of cards
   */
  public ArrayList<Card> getSuspendedCards() {
    return this.cm.getSuspendedCards();
  }

  /**
   * Add a new card for this cardHolder.
   *
   * @return the newly created card
   */
  public Card addCard() {
    Card newCard = this.cm.create(this);
    logger.info(
        String.format("Created new card %s for user with email %s%n", newCard.getID(), email));
    return newCard;
  }

  /**
   * Suspend the corresponding card with the given card number <cardNum> for this
   * Support.CardHolder.
   *
   * @param card the card to be suspended
   */
  public void suspendCard(Card card) {
    this.cm.suspendCard(card);
    logger.info(String.format("Card %s has been suspended. Account: %s%n", card.getID(), email));
  }

  /**
   * Unsuspend the corresponding card with the given card number <cardNum> for this cardHolder.
   *
   * @param card the card to be un-suspended
   */
  public void unsuspendCard(Card card) {
    this.cm.unsuspendCard(card);
    logger.info(String.format("Card %s has been un-suspended. Account: %s%n", card.getID(), email));
  }

  /**
   * Removes a card from cardholder's possession.
   *
   * @param card the card to be removed
   */
  public void removeCard(Card card) {
    this.cm.removeCard(card);
    logger.info(String.format("Card %s has been removed. Account: %s%n", card.getID(), email));
  }

  /**
   * Returns whether this card holder holds a particular card
   *
   * @param cardId the id of the card being queried
   * @return true if this card holder holds card
   */
  public boolean hasCard(String cardId) {
    return this.cm.find(cardId, true) != null;
  }

  /**
   * Record the given trip <trip> in this cardHolder's tripHistory. In addition, record the cost of
   * this trip and increment the monthly number of trips taken by one.
   *
   * @param trip the trip to be recorded.
   */
  public void recordTrip(Trip trip) {
    this.monthlyCost += trip.getFare();
    this.monthlyNumTrips += 1;
    this.tripHistory.add(trip);
  }

  /**
   * Return all of the trips taken by this cardHolder.
   *
   * @return ArrayList of trip, all trips recorded.
   */
  public ArrayList<Trip> viewTrips() {
    return this.tripHistory;
  }

  /**
   * Return the three most recent trips of this cardholder.
   *
   * @return List of trips that are most recent.
   */
  public List<Trip> getMostRecentTrips() {
    return tripHistory.subList(Math.max(0, tripHistory.size() - 3), tripHistory.size());
  }

  /**
   * Return the average transit cost for this month of this cardHolder.
   *
   * @return the average cost of cardHolder.
   */
  public double trackAvgCost() {
    return this.monthlyCost / this.monthlyNumTrips;
  }

  /**
   * Returns the cost per month accumulated by cardholder.
   *
   * @return monthly cost
   */
  public double getMonthlyCost() {
    return this.monthlyCost;
  }

  /**
   * Returns the cost per month accumulated by cardholder.
   *
   * @return monthly cost
   */
  public int getNumTrips() {
    return this.monthlyNumTrips;
  }

  /**
   * Adds balance to a specific card. Can only add $10, $20, or $50.
   *
   * @param card card to have balance added to it.
   * @param amount the amount to add to card balance.
   */
  public void addBalance(Card card, int amount) {
    if (this.cm.addBalance(card, amount)) {
      logger.info(
          String.format(
              "$%d added to balance of card %s. New balance is now $%.02f.%n",
              amount, card.getID(), card.getBalance()));
    } else {
      logger.warning(
          String.format(
              "Failed to raise funds on card %s by $ %d. Funds can only be raised by $10, $20, or $50%n",
              card.getID(), amount));
    }
  }

  /**
   * Return the corresponding card with the given card number <cardNum>.
   *
   * @param cardNum the card number of the card to be found.
   * @return the card to be found.
   * @throws IllegalArgumentException when card is not found.
   */
  public Card getSelectedCard(String cardNum) {
    return this.cm.getSelectedCard(cardNum);
  }

  /** Clear the monthly travel statistics for this cardHolder. */
  public void clearMonthlyRecords() {
    this.monthlyNumTrips = 0;
    this.monthlyCost = 0;
  }
}
