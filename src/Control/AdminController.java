package Control;

import Control.AdminPanes.AddTSController;
import Control.AdminPanes.ViewStatsController;
import Control.CardholderPanes.*;
import Support.Admin;
import Support.AdminManager;
import Support.SimulatedTime;
import Support.TransitManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for Admin. Allows admin to switch between different functions according to the buttons
 * they press
 */
public class AdminController extends Controller implements Initializable {

  @FXML private Text logOut;
  @FXML private Pane pane;
  @FXML private Text addTS, viewStats, settings;
  @FXML private Text manageCards, takeTrip, viewTrips, trackCost;
  @FXML private Text name;
  @FXML private TextField numMinutes;
  @FXML private Button skip;

  /** Admin of whom the pane belongs to */
  private Admin admin;
  /** AdminManager associated with the system */
  private AdminManager adminManager;
  /** TransitManager associated with the system */
  private TransitManager transitManager;

  /**
   * Constructor for controller of Admin Panel, stores information about this admin user and the
   * transit systems under this user's management.
   *
   * @param sceneController for switching between panes
   * @param admin the admin user
   * @param adminManager AdminManager associated with the system
   * @param tm TransitManager of the system
   */
  AdminController(
      SceneController sceneController, Admin admin, AdminManager adminManager, TransitManager tm) {
    super(sceneController);
    this.admin = admin;
    this.adminManager = adminManager;
    this.transitManager = tm;
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    name.setText(admin.getName());
    setSkipTime();

    viewStats.setOnMouseClicked(
        event ->
            changePane(
                pane,
                "/Control/AdminPanes/ViewStats.fxml",
                new ViewStatsController(admin, transitManager)));

    addTS.setOnMouseClicked(
        event ->
            changePane(
                pane,
                "/Control/AdminPanes/AddTS.fxml",
                new AddTSController(admin, adminManager, transitManager)));

    settings.setOnMouseClicked(
        event ->
            changePane(
                pane, "/Control/CardholderPanes/SettingsPane.fxml", new SettingsController(admin)));

    manageCards.setOnMouseClicked(
        event ->
            changePane(
                pane,
                "/Control/CardholderPanes/ManageCardPane.fxml",
                new ManageCardController(admin.getAssociatedAccount())));

    takeTrip.setOnMouseClicked(
        event ->
            changePane(
                pane,
                "/Control/CardholderPanes/TakeTripPane.fxml",
                new TakeTripController(admin.getAssociatedAccount(), transitManager)));

    viewTrips.setOnMouseClicked(
        event ->
            changePane(
                pane,
                "/Control/CardholderPanes/ViewTripPane.fxml",
                new ViewTripController(admin.getAssociatedAccount())));

    trackCost.setOnMouseClicked(
        event ->
            changePane(
                pane,
                "/Control/CardholderPanes/TrackCostPane.fxml",
                new TrackCostController(admin.getAssociatedAccount())));

    // Initialize this scene
    super.setBack(logOut);
    super.initialize(url, rb);
  }

  /** Changes the time displayed by skipping into the future the amount time entered. */
  private void setSkipTime() {
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
