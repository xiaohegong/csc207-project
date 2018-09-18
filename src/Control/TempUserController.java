package Control;

import Support.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * MainPageController for the TempUserController.fxml. This class determines what whether it is an
 * admin or card holder interface and updates accordingly. It also keeps track of the previous scene
 * for users to go back to previous state.
 */
public class TempUserController extends Controller implements Initializable {
  @FXML private Text backText;
  @FXML private ChoiceBox<TransitSystem> transitSystem;
  @FXML private ChoiceBox<Station> startStation, endStation;
  @FXML private ChoiceBox<Integer> startRoute, endRoute;
  @FXML private ToggleButton isSubway, isBus;
  @FXML private Button purchase;

  /** TransitManager of the system */
  private TransitManager transitManager;

  /** The transitSystem to are being displayed in the ChoiceBox as default selected */
  private TransitSystem tsToView;

  /** Constructor for controller for TempUserController, stores previous scenes to go back. */
  TempUserController(SceneController sceneController, TransitManager transitManager) {
    super(sceneController);
    this.transitManager = transitManager;
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    isSubway.setSelected(true);

    super.setChoiceBox(transitSystem, transitManager.getTransitSystems());
    tsToView = transitSystem.getSelectionModel().getSelectedItem();

    setTransitSystem();

    transitSystem.setOnAction(event -> setTransitSystem());
    startRoute.setOnAction(event -> setRouteStation(startRoute, startStation));
    endRoute.setOnAction(event -> setRouteStation(endRoute, endStation));

    isSubway.setOnAction(event -> setTransitSystem());
    isBus.setOnAction(event -> setTransitSystem());

    purchase.setOnMouseClicked(event -> calculateFare());

    super.setBack(backText);
    super.initialize(url, rb);
  }

  /** Updates the TransitSystem shown in ChoiceBox */
  private void setTransitSystem() {
    tsToView = transitSystem.getSelectionModel().getSelectedItem();
    if (tsToView != null) {
      super.setChoiceBox(startRoute, tsToView.getAllRoutes(isSubway.isSelected()));
      setRouteStation(startRoute, startStation);
      super.setChoiceBox(endRoute, tsToView.getAllRoutes(isSubway.isSelected()));
      setRouteStation(endRoute, endStation);
    }
  }

  /**
   * Updates the starting and ending routes and stations shown in ChoiceBox for given transit system
   *
   * @param routes the ChoiceBox of route numbers.
   * @param stations the ChoiceBox of stations of given route
   */
  private void setRouteStation(ChoiceBox<Integer> routes, ChoiceBox<Station> stations) {
    if (routes.getValue() != null) {
      int routeNum = routes.getValue();
      ArrayList<Station> stationList =
          tsToView.getRoute(isSubway.isSelected(), routeNum).getStations();
      super.setChoiceBox(stations, stationList);
    }
  }

  /**
   * Calculate and displays the cost for the token to be purchased. Fare is the minimum of the
   * transit system's (FEE_CAP + 2) dollars and trip length times $ 0.75.
   */
  private void calculateFare() {
    tsToView = transitSystem.getSelectionModel().getSelectedItem();
    Route startR = transitSystem.getValue().getRoute(isSubway.isSelected(), startRoute.getValue());
    Route endR = transitSystem.getValue().getRoute(isSubway.isSelected(), endRoute.getValue());
    Station start = startStation.getValue();
    Station end = endStation.getValue();

    Trip trip = new Trip(startR, endR, start, end, tsToView);

    if (trip.getPathLength() == 0) {
      Alert error = new Alert(Alert.AlertType.WARNING);
      error.setTitle("Error");
      error.setHeaderText("Error taking trip.");
      error.setContentText("There is no path between chosen stations");
      error.showAndWait();
    } else {
      double fare = Math.min(tsToView.FEE_CAP + 2, (trip.getPathLength() - 1) * 0.75);
      tsToView.addRevenue(fare);

      Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
      confirmation.setTitle("Purchase Token");
      confirmation.setHeaderText("From " + start.getName() + " to " + end.getName());
      confirmation.setContentText("Your total fare to be paid is $" + fare);
      confirmation.showAndWait();

      sceneController.activatePane("main", sceneController.getMainController());
    }
  }
}
