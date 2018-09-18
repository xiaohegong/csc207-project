package Support;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Factory class that creates cards. Acts like the mint of the cards. Cards can only be created and
 * managed through this class. It can track and access existing cards, as well as ensuring not too
 * many cards are distributed.
 */
public class CardManager implements Serializable {

  /** the list of all active cards held by a cardholder. */
  private ArrayList<Card> cards = new ArrayList<>();
  /** the list of all currently suspended cards of a cardholder. */
  private ArrayList<Card> suspendedCards = new ArrayList<>();

  /**
   * Factory method to create and return a new card
   *
   * @param holder the holder the new card will belong to.
   * @return the new card
   */
  public Card create(CardHolder holder) {
    Card newCard = new Card(holder);
    cards.add(newCard);
    return newCard;
  }

  /**
   * Finds and returns an active card with the id cardNum
   *
   * @param cardNum the id query of the card
   * @param includeSuspended if true, will only search for active cards that have not been suspended
   * @return the card with the desired id
   */
  public Card find(String cardNum, boolean includeSuspended) {
    for (Card card : cards) {
      if (card.getID().equals(cardNum)) return card;
    }
    if (includeSuspended) {
      for (Card card : suspendedCards) {
        if (card.getID().equals(cardNum)) return card;
      }
    }
    return null;
  }

  /**
   * Retrun list of all active, non-suspended cards.
   *
   * @return ArrayList of active cards
   */
  public ArrayList<Card> getCards() {
    return cards;
  }

  /**
   * Retrun list of all suspended cards.
   *
   * @return ArrayList of suspended cards
   */
  public ArrayList<Card> getSuspendedCards() {
    return suspendedCards;
  }

  /**
   * Return the corresponding card with the given card number <cardNum>.
   *
   * @param cardNum the card number of the card to be found.
   * @return the card to be found.
   * @throws IllegalArgumentException when card is not found.
   */
  public Card getSelectedCard(String cardNum) {
    Card card = find(cardNum, true);
    if (card == null) {
      throw new IllegalArgumentException(
          "The following card is not an active card of this cardHolder: " + cardNum);
    } else {
      return card;
    }
  }

  /**
   * Adds funds to a particular card's balance.
   *
   * @param card the card to have balanced added.
   * @param amount the amount to be added.
   * @return true if balance was successfully added
   */
  public boolean addBalance(Card card, int amount) {
    return card.addBalance(amount);
  }

  /**
   * Suspends a card from the cardHolder's possession.
   *
   * @param card the card to be suspended.
   */
  public void suspendCard(Card card) {
    card.suspend(true);
    cards.remove(card);
    suspendedCards.add(card);
  }

  /**
   * Un-suspends a suspends card, so it is now usable by cardholders.
   *
   * @param card the card to be un-suspended
   */
  public void unsuspendCard(Card card) {
    card.suspend(false);
    suspendedCards.remove(card);
    cards.add(card);
  }

  /**
   * Removes a card from cardholder's possession.
   *
   * @param card card to be removed
   */
  public void removeCard(Card card) {
    cards.remove(card);
  }
}
