package Control.CardholderPanes;

import Control.Controller;
import Support.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.ResourceBundle;

/** Controller for settings page for both admin and cardholder. */
public class SettingsController extends Controller {

  @FXML private TextField nameInput, minAmount;
  @FXML private PasswordField passwordInput;
  @FXML private PasswordField passwordConfirm;
  @FXML private Button submit, autoloadConfirm;

  /** specifies which user this pane belongs to */
  private User user;

  /** the minimum balance card can have before pre-authorize reload */
  private Double minDollar = null;

  /**
   * Constructor for SettingController
   *
   * @param user the user to change the info for
   */
  public SettingsController(User user) {
    super();
    this.user = user;
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    submit.setOnMouseClicked(event -> updateInfo());

    autoloadConfirm.setOnMouseClicked(event -> setUpAutoLoad());
  }

  /**
   * Updates the information on name and password based on user input. Checks the validity of the
   * input name and password using regex. Outputs appropriate messages.
   */
  private void updateInfo() {
    String name = nameInput.getCharacters().toString();
    String password = passwordInput.getCharacters().toString();
    String passConfirm = passwordConfirm.getCharacters().toString();

    Alert error = new Alert(Alert.AlertType.WARNING);
    error.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    error.setTitle("Setting Error");
    error.setHeaderText("Incorrect Format Error");

    // Check if name is blank, do not change name if name is blank
    if (!name.equals("")) {
      if (!user.setName(name)) {
        error.setContentText(
            "Please enter in correct format!" + System.lineSeparator() + user.nameRequirement());
        error.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        error.showAndWait();
      }
    }
    if (!user.setPassword(password)) {
      error.setContentText(
          "Please enter in correct format!" + System.lineSeparator() + user.passwordRequirement());
      error.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      error.showAndWait();
    } else if (!password.equals(passConfirm)) {
      error.setContentText("Your passwords do not natch!");
      error.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      error.showAndWait();
    } else {
      Alert success = new Alert(Alert.AlertType.CONFIRMATION);
      success.setTitle("Setting Confirmation");
      success.setHeaderText("Setting Success");
      success.setContentText("You have successfully updated your information!");
      success.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      success.showAndWait();
    }
    nameInput.clear();
    passwordInput.clear();
    passwordConfirm.clear();
  }

  /** Set up pre-authorized autoload if user intend to. */
  private void setUpAutoLoad() {
    Alert error = new Alert(Alert.AlertType.WARNING);
    error.setTitle("Autoload setup Error");
    error.setHeaderText("Invalid Minimum Amount Error");

    String amount = minAmount.getCharacters().toString();

    try {
      minDollar = Double.parseDouble(amount);
    } catch (NumberFormatException e) {
      error.setContentText("Please enter a valid CAD $ amount!");
      error.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      error.showAndWait();
    }

    if (minDollar != null) {

      if (minDollar < 0) {
        error.setContentText("A negative minimum amount is not valid!");
        error.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        error.showAndWait();
      } else {
        Alert confirmation =
            new Alert(
                Alert.AlertType.CONFIRMATION,
                "Do you authorize us to autoload your cards when their balances fall below CAD $ "
                    + amount
                    + " ?",
                ButtonType.YES,
                ButtonType.NO);
        confirmation.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        confirmation.showAndWait();

        if (confirmation.getResult() == ButtonType.YES) {
          this.user.setAutoLoad(true, minDollar);

          Alert success =
              new Alert(
                  Alert.AlertType.CONFIRMATION,
                  "You have successfully set up autoload for your cards!",
                  ButtonType.OK);
          success.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
          success.setTitle("Setting Confirmation");
          success.setHeaderText("Autoload set-up Success");
          success.showAndWait();
        }
        if (confirmation.getResult() == ButtonType.NO) {
          this.minDollar = null;
        }
      }
    }
  }
}
