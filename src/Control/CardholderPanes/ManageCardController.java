package Control.CardholderPanes;

import Control.Controller;
import Support.Card;
import Support.CardHolder;
import Support.SimulatedTime;
import Support.TermPass;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Controller for ManageCard pane for specific cardholder. Cardholder can add/delete cards,
 * suspend/un-suspend cards, add balance, view cards balance.
 */
public class ManageCardController extends Controller {

  /** Cardholder of which the pane belongs to. */
  private CardHolder cardHolder;

  // The types of card an user may add
  @FXML private RadioButton newCard, newDayPass, newMonthlyPass;

  // The cards information displayed to help this user to manage cards
  @FXML private Text cardsInfo;

  @FXML private ChoiceBox<Card> allCards, cardsSuspended;
  @FXML private ChoiceBox<Card> activeCards, activeCards2;
  @FXML private Button addCard, removeCard, suspendCard, unsuspendCard, addBalance;
  @FXML private ToggleButton ten, twenty, fifty;
  @FXML private ToggleGroup amount, radioGroup;
  @FXML private ScrollPane scrollPane;

  /**
   * Constructor for ManageCard pane for given cardholder
   *
   * @param cardHolder cardholder of which the pane belongs to
   */
  public ManageCardController(CardHolder cardHolder) {
    super();
    this.cardHolder = cardHolder;
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    updateCardInfo();

    addCard.setOnMouseClicked(event -> addCard());
    removeCard.setOnMouseClicked(event -> removeCard());
    suspendCard.setOnMouseClicked(event -> suspendCard());
    unsuspendCard.setOnMouseClicked(event -> unSuspendCard());
    addBalance.setOnMouseClicked(event -> addBalance());
  }

  /** Updates cards in all ChoiceBoxes after every action. */
  private void updateCardInfo() {
    super.setChoiceBox(activeCards, cardHolder.getActiveCards());
    super.setChoiceBox(activeCards2, cardHolder.getActiveCards());
    super.setChoiceBox(cardsSuspended, cardHolder.getSuspendedCards());
    super.setChoiceBox(allCards, cardHolder.getAllCards());

    StringBuilder info = new StringBuilder();
    ArrayList<Card> allCardsList = cardHolder.getAllCards();
    for (Card card : allCardsList) {
      info.append("Card #").append(card.getID()).append("\t\t$").append(card.getBalance());

      if (card.isSuspended()) info.append("\tSuspended");
      info.append(newLine);
    }

    cardHolder.updatePasses(SimulatedTime.instance.getDate());
    ArrayList<TermPass> passes = cardHolder.getPasses();
    for (TermPass pass : passes) {
      info.append("Pass")
          .append("\t\t\t")
          .append("Expiry Date: ")
          .append(pass.getExpiryDate().toString())
          .append(newLine);
    }
    cardsInfo.setText(info.toString());
    fitTextToScroll(cardsInfo, scrollPane);
  }

  /**
   * Suspends the selected card from cardholder's possession and refunds the remaining amount to the
   * user.
   */
  private void suspendCard() {
    if (activeCards.getValue() != null) {
      Card card = activeCards.getValue();
      cardHolder.suspendCard(card);

      Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
      confirmation.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      confirmation.setTitle("Suspend Card");
      confirmation.setHeaderText("Suspend Card #" + card.getID());
      confirmation.setContentText("You have successfully suspended card #" + card.getID());
      confirmation.showAndWait();
    } else {
      Alert noCardSelected = new Alert(Alert.AlertType.ERROR);
      noCardSelected.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      noCardSelected.setTitle("Error");
      noCardSelected.setHeaderText("Error Suspending Card");
      noCardSelected.setContentText("No card selected! Please select a card to suspend!");
      noCardSelected.showAndWait();
    }
    updateCardInfo();
  }

  /** Removes the card chosen by cardholder */
  private void removeCard() {
    if (allCards.getValue() != null) {
      Card card = allCards.getValue();
      cardHolder.removeCard(card);

      Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
      confirmation.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      confirmation.setTitle("Remove Card");
      confirmation.setHeaderText("Remove Card #" + card.getID());
      confirmation.setContentText(
          "You have successfully removed card #"
              + card.getID()
              + newLine
              + "Remaining balance $"
              + card.getBalance()
              + " has been refunded to you.");
      confirmation.showAndWait();
    } else {
      Alert noCardSelected = new Alert(Alert.AlertType.ERROR);
      noCardSelected.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      noCardSelected.setTitle("Error");
      noCardSelected.setHeaderText("Error Removing Card");
      noCardSelected.setContentText("No card selected! Please select a card to remove!");
      noCardSelected.showAndWait();
    }
    updateCardInfo();
  }

  /** Unsuspends the card chosen by cardholder */
  private void unSuspendCard() {
    if (cardsSuspended.getValue() != null) {
      Card card = cardsSuspended.getValue();
      cardHolder.unsuspendCard(card);

      Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
      confirmation.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      confirmation.setTitle("Un-suspend Card");
      confirmation.setHeaderText("Un-suspend Card #" + card.getID());
      confirmation.setContentText("You have successfully un-suspended card #" + card.getID());
      confirmation.showAndWait();
    } else {
      Alert noCardSelected = new Alert(Alert.AlertType.ERROR);
      noCardSelected.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      noCardSelected.setTitle("Error");
      noCardSelected.setHeaderText("Error Un-Suspending Card");
      noCardSelected.setContentText("No card selected! Please select a card to un-suspend!");
      noCardSelected.showAndWait();
    }
    updateCardInfo();
  }

  /**
   * Adds the selected balance to the selected card for the user. Amount can only be $10, $20, $50.
   */
  private void addBalance() {
    if (activeCards2.getValue() != null) {
      Card card = activeCards2.getValue();

      Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
      confirmation.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      confirmation.setTitle("Add Balance");
      confirmation.setHeaderText("Adding balance to card");

      if (ten.isSelected()) {
        cardHolder.addBalance(card, 10);
        confirmation.setContentText("$10 have been added Card #" + card.getID());
      } else if (twenty.isSelected()) {
        cardHolder.addBalance(card, 20);
        confirmation.setContentText("$20 have been added Card #" + card.getID());
      } else if (fifty.isSelected()) {
        cardHolder.addBalance(card, 50);
        confirmation.setContentText("$50 have been added Card #" + card.getID());
      }
      confirmation.showAndWait();
    }
    updateCardInfo();
  }

  /** Adds a card, or day pass, or monthly pass as selected by user. */
  private void addCard() {
    if (newCard.isSelected()) createCard();
    else if (newDayPass.isSelected()) createDayPass();
    else if (newMonthlyPass.isSelected()) createMonthlyPass();

    updateCardInfo();
  }

  /** Creates a new card for the user. All balance start at $19. */
  private void createCard() {
    Card card = cardHolder.addCard();
    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
    confirmation.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    confirmation.setTitle("New Card");
    confirmation.setHeaderText("New Card for " + cardHolder.getName());
    confirmation.setContentText("You have successfully created a new card: Card #" + card.getID());
    confirmation.showAndWait();
  }

  /** Creates a day pass for the user. Expires after one calender day. */
  private void createDayPass() {
    TermPass pass = cardHolder.addPass(1, 10);
    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
    confirmation.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    confirmation.setTitle("New Day Pass");
    confirmation.setHeaderText("New Day Pass for " + cardHolder.getName());
    confirmation.setContentText(
        "You have successfully purchased a new day pass. It costs $10. It will expire on: "
            + pass.getExpiryDate().toString());
    confirmation.showAndWait();
  }

  /** Creates a monthly pass for the user. Expires after end of each month. */
  private void createMonthlyPass() {
    LocalDate today = SimulatedTime.instance.getDate();
    int length = today.lengthOfMonth() - today.getDayOfMonth();

    TermPass pass = cardHolder.addPass(length, 120);
    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
    confirmation.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    confirmation.setTitle("New Monthly Pass");
    confirmation.setHeaderText("New Monthly Pass for " + cardHolder.getName());
    confirmation.setContentText(
        "You have successfully purchased a new monthly pass. It costs $120. It will expire on: "
            + pass.getExpiryDate().toString());
    confirmation.showAndWait();
  }
}
