package Control.AdminPanes;

import Control.Controller;
import Support.Admin;
import Support.AdminManager;
import Support.TransitManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/** Controller for admins to add TransitSystems */
public class AddTSController extends Controller implements Initializable {

  /** Logger */
  private static final Logger logger = Logger.getLogger("fileLogger");

  @FXML private Button addButton;
  @FXML private TextField enteredTSName;

  /** AdminManager associated with the system */
  private AdminManager adminManager;
  /** TransitManager associated with the system */
  private TransitManager transitManager;
  /** Admin of whom the pane belongs to */
  private Admin admin;

  /**
   * Constructor for controller of Admin Panel, stores information about this admin user and the
   * transit systems under this user's management.
   *
   * @param admin Admin
   * @param adminManager AdminManager
   * @param tm TransitManager
   */
  public AddTSController(Admin admin, AdminManager adminManager, TransitManager tm) {
    super();
    this.admin = admin;
    this.adminManager = adminManager;
    this.transitManager = tm;
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    // Add a new Transit System to be managed by this admin
    addButton.setOnMouseClicked(
        event -> {
          handleEnteredTSName();
        });
  }

  /**
   * Parse the inputted Transit System name <tsName> and add clearance for this admin user to
   * manage.
   *
   * @param tsName String
   */
  private void manageNewTransitSystem(String tsName) {
    this.adminManager.addManagingTransitSystem(admin.getEmail(), tsName);
    logger.info(
        String.format(
            "Added Transit System %s to admin with email %s%n", tsName, admin.getEmail()));
  }

  /**
   * Get the entered transit system name. If the inputted name is valid, add it under this admin
   * user's management. If it is invalid, i.e., nothing was inputted or the transit system does not
   * exist, alert the user.
   */
  private void handleEnteredTSName() {
    Alert inputAlert = new Alert(Alert.AlertType.ERROR); // error pop-up window
    String tsName = enteredTSName.getText();

    if (tsName.equals("")) {
      inputAlert.setContentText("Entered empty Transit System name !");
      inputAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      inputAlert.showAndWait();
    } else if (!transitManager.tsExists(tsName)) {
      inputAlert.setContentText(
          "Entered Transit System name is invalid!\nPlease note that input is case-sensitive.\nPlease do not put any unnecessary spaces.");
      inputAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      inputAlert.showAndWait();
    } else {
      if (!this.admin.hasTransitSystemName(tsName)) {
        manageNewTransitSystem(tsName);

        // Show confirmation pop-up window
        Alert success = new Alert(Alert.AlertType.CONFIRMATION);
        success.setTitle("Confirmation");
        success.setHeaderText("Adding managing Transit System");
        success.setContentText("Transit System successfully added!");
        inputAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        success.showAndWait();

        enteredTSName.clear();
      } else {
        inputAlert.setContentText("This Transit System is already under your management!");
        inputAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        inputAlert.showAndWait();
      }
    }
  }
}
