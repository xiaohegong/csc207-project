package Control;

import Control.CardholderPanes.*;
import Support.CardHolder;
import Support.SimulatedTime;
import Support.TransitManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for Cardholder Pane. Allows cardholder to perform different actions such as manage
 * cards. take trips, view previous trips, track cost, settings.
 */
public class CardholderController extends Controller {
  @FXML private Text logOut;
  @FXML private Text name;
  @FXML private Text manageCards;
  @FXML private Text takeTrip;
  @FXML private Text viewTrips;
  @FXML private Text trackCost;
  @FXML private Text settings;
  @FXML private AnchorPane welcomePane;
  @FXML private Pane pane;
  @FXML private TextField numMinutes;
  @FXML private Button skip;

  /** Cardholder of whom the pane belongs to. */
  private CardHolder cardHolder;
  /** TransitManager associated with the system. */
  private TransitManager transitManager;

  /**
   * Constructor for Cardholder Pane
   *
   * @param sceneController for switching between panes
   * @param cardHolder cardholder of which this pane belongs to
   * @param tm TransitManager of the system
   */
  CardholderController(SceneController sceneController, CardHolder cardHolder, TransitManager tm) {
    super(sceneController);
    this.cardHolder = cardHolder;
    this.transitManager = tm;
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    name.setText(cardHolder.getName());
    configureSkipTimeForm();

    manageCards.setOnMouseClicked(
        event ->
            changePane(
                pane,
                "/Control/CardholderPanes/ManageCardPane.fxml",
                new ManageCardController(cardHolder)));

    takeTrip.setOnMouseClicked(
        event ->
            changePane(
                pane,
                "/Control/CardholderPanes/TakeTripPane.fxml",
                new TakeTripController(cardHolder, transitManager)));

    viewTrips.setOnMouseClicked(
        event ->
            changePane(
                pane,
                "/Control/CardholderPanes/ViewTripPane.fxml",
                new ViewTripController(cardHolder)));

    trackCost.setOnMouseClicked(
        event ->
            changePane(
                pane,
                "/Control/CardholderPanes/TrackCostPane.fxml",
                new TrackCostController(cardHolder)));
    settings.setOnMouseClicked(
        event ->
            changePane(
                pane,
                "/Control/CardholderPanes/SettingsPane.fxml",
                new SettingsController(cardHolder)));

    super.setBack(logOut);
    super.initialize(url, rb);
  }

  /** Changes the time displayed by skipping into the future the amount time entered. */
  private void configureSkipTimeForm() {
    numMinutes.setOnMouseClicked(
        event -> {
          // Set the caret to blink when clicked
          numMinutes.setStyle("-fx-display-caret: true;");
        });
    numMinutes.setOnMouseExited(
        event -> {
          // Remove caret when no longer active
          numMinutes.setStyle("-fx-display-caret: false;");
        });

    skip.setOnAction(
        event -> {
          SimulatedTime.instance.skipTime(Long.parseLong(numMinutes.getText()));
          numMinutes.clear();
        });
  }
}
