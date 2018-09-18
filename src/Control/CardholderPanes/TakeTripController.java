package Control.CardholderPanes;

import Control.Controller;
import Support.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Controller for TakeTrip. Allows users to use cards or passes to tap in or tap out of stations in
 * different transit systems
 */
public class TakeTripController extends Controller {

  /** Cardholder of whom the pane belongs to */
  private CardHolder cardHolder;
  /** Transit Manager associated with the system */
  private TransitManager transitManager;
  /** TransitSystem that is being displayed in ChoiceBox */
  private TransitSystem system;
  /** Station that is being displayed in ChoiceBox */
  private Station station;

  @FXML private ToggleButton tapIn;
  @FXML private ToggleButton tapOut;
  @FXML private ChoiceBox<TransitSystem> transitSystemsCB;
  @FXML private ChoiceBox<Station> stationsCB;
  @FXML private ChoiceBox<Card> cardsCB;
  @FXML private ChoiceBox<TermPass> passCB;
  @FXML private ChoiceBox<Integer> routeCB;
  @FXML private Button usePass, useCard;
  @FXML private RadioButton bus;
  @FXML private RadioButton subway;

  /**
   * Constructor for TakeTrip Pane.
   *
   * @param cardHolder cardholder of whom the pane belongs to
   * @param transitManager the transitManager associated with the system
   */
  public TakeTripController(CardHolder cardHolder, TransitManager transitManager) {
    super();
    this.cardHolder = cardHolder;
    this.transitManager = transitManager;
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    bus.setSelected(true);

    super.setChoiceBox(transitSystemsCB, transitManager.getTransitSystems());
    super.setChoiceBox(cardsCB, cardHolder.getActiveCards());
    super.setChoiceBox(passCB, cardHolder.getPasses());

    setTransitSystem();

    transitSystemsCB.setOnAction(event -> setTransitSystem());
    routeCB.setOnAction(event -> setRouteStation());

    bus.setOnAction(event -> setTransitSystem());
    subway.setOnAction(event -> setTransitSystem());

    tapIn.setOnAction(event -> tapInSelected());
    tapOut.setOnAction(event -> tapOutSelected());
    useCard.setOnMouseClicked(event -> useCard());
    usePass.setOnMouseClicked(event -> usePass());
  }

  /**
   * Set tapIn to green color if toggle button tapIn is selected with mouse click, and set tapOut to
   * white color as tapOut will be automatically unselected.
   */
  private void tapInSelected() {
    if (tapIn.isSelected()) {
      setToggleStyle(tapIn, tapOut);
    } else {
      tapIn.setStyle("-fx-background-color: white; -fx-text-fill: #45931f;");
    }
  }

  /**
   * Set tapOut to green color if toggle button tapOut is selected with mouse click, and set tapIn
   * to white color as tapIn will be automatically unselected.
   */
  private void tapOutSelected() {
    if (tapOut.isSelected()) {
      setToggleStyle(tapOut, tapIn);
    } else {
      tapOut.setStyle("-fx-background-color: white; -fx-text-fill: #45931f;");
    }
  }

  /** Checks if there is anything wrong with the input such as fields that are not filled out. */
  private void handleTapButton() {
    Alert noTypeSelected = new Alert(Alert.AlertType.ERROR);
    noTypeSelected.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    noTypeSelected.setTitle("Error");

    if (!bus.isSelected() && !subway.isSelected()) {
      noTypeSelected.setHeaderText("No trip type selected");
      noTypeSelected.setContentText("Trip Type not selected! Please select type bus or subway!");
      noTypeSelected.showAndWait();
    } else if (stationsCB.getValue() == null) {
      noTypeSelected.setHeaderText("No station selected");
      noTypeSelected.setContentText("Station is not selected! Please select a station to tap!");
      noTypeSelected.showAndWait();
    }
  }

  /** Allows users to enter/exit using cards when button is pressed. */
  private void useCard() {
    Alert noTypeSelected = new Alert(Alert.AlertType.ERROR);
    noTypeSelected.setTitle("Error");

    handleTapButton();
    Card card = cardsCB.getSelectionModel().getSelectedItem();
    Route route =
        system.getRoute(subway.isSelected(), routeCB.getSelectionModel().getSelectedItem());
    station = stationsCB.getSelectionModel().getSelectedItem();
    system = transitSystemsCB.getSelectionModel().getSelectedItem();

    if (tapIn.isSelected()) card.enter(route, station, system);
    else card.exit(route, station, system, false);

    super.setChoiceBox(cardsCB, cardHolder.getActiveCards());
    super.setChoiceBox(passCB, cardHolder.getPasses());
  }

  /** Allows user to enter/exit using pass when button is pressed. */
  private void usePass() {
    handleTapButton();

    TermPass pass = passCB.getSelectionModel().getSelectedItem();
    station = stationsCB.getSelectionModel().getSelectedItem();

    if (tapIn.isSelected()) pass.enter(station);
    else pass.exit(station);

    super.setChoiceBox(cardsCB, cardHolder.getActiveCards());
    super.setChoiceBox(passCB, cardHolder.getPasses());
  }

  /** Updates the TransitSystem shown in ChoiceBox */
  private void setTransitSystem() {
    system = transitSystemsCB.getSelectionModel().getSelectedItem();
    if (system != null) {
      super.setChoiceBox(routeCB, system.getAllRoutes(subway.isSelected()));
      setRouteStation();
    }
  }

  /**
   * Updates the starting and ending routes and stations shown in ChoiceBox for given transit system
   */
  private void setRouteStation() {
    if (routeCB.getValue() != null) {
      int routeNum = routeCB.getValue();
      ArrayList<Station> stationList = system.getRoute(subway.isSelected(), routeNum).getStations();
      super.setChoiceBox(stationsCB, stationList);
    }
  }
}
