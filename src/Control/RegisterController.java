package Control;

import Support.AdminManager;
import Support.CardHolderManager;
import Support.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/** Allows users to register onto the system as either an admin or cardholder. */
public class RegisterController extends Controller implements Initializable {
  /** FXML button for registering. */
  @FXML private Button register;
  /** FXML textfield for email input. */
  @FXML private TextField emailInput;
  /** FXML password field for password input. */
  @FXML private PasswordField passwordInput;

  /** FXML textfield for name input. */
  @FXML private TextField nameInput;
  /** FXML toggle button to see if it is an admin or cardholder. */
  @FXML private ToggleButton isAdmin;

  /** FXML button to go back to home page. */
  @FXML private Text back;

  /** The associated cardholderManager to register users in. */
  private CardHolderManager cardHolderManager;
  /** The associated adminManager to register users in. */
  private AdminManager adminManager;

  /**
   * Contructor for RegisterController.
   *
   * @param sceneController SceneController to switch between main page and itself.
   * @param cardHolderManager CardholderManager to register cardholders in.
   * @param adminManager AdminManager to register admins in.
   */
  RegisterController(
      SceneController sceneController,
      CardHolderManager cardHolderManager,
      AdminManager adminManager) {
    super(sceneController);
    this.cardHolderManager = cardHolderManager;
    this.adminManager = adminManager;
  }

  /**
   * Returns whether the inputted email <email> is a valid email address. A valid email address must
   * be in the form of "<username>@<host>.<suffix>".
   *
   * @param email String
   * @return boolean
   */
  private static boolean isValidEmail(String email) {
    return email.matches("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$");
  }

  /**
   * Returns whether the inputted password <password> is a valid password. A valid password must
   * have length at least 8 and contains at least one number and one letter.
   *
   * @param password String
   * @return boolean
   */
  private static boolean isValidPassword(String password) {
    return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{8,}$");
  }

  /**
   * Returns whether the inputted name <name> is a valid name. A valid name must not contain only
   * white spaces.
   *
   * @param name String
   * @return boolean
   */
  private static boolean isValidName(String name) {
    return name.matches("[ a-zA-Z]+") && name.length() <= 15 && name.trim().length() > 0;
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    isAdmin.setSelected(true);
    register.setOnMouseClicked(
        event -> {
          registerUser();
        });
    super.setBack(back);
    super.initialize(url, rb);
  }

  /**
   * Registers a user with input name, email, and password. It checks for insufficient information,
   * duplicate account, and input format for all fields. An account is only created when given
   * unique email.
   */
  private void registerUser() {
    Alert registerAlert = new Alert(Alert.AlertType.ERROR);
    registerAlert.setTitle("Error");
    registerAlert.setHeaderText("Register Error");

    String email = emailInput.getCharacters().toString().toLowerCase();
    String password = passwordInput.getCharacters().toString();
    String name = nameInput.getCharacters().toString();

    if (email.equals("") || password.equals("") || name.equals("")) {
      registerAlert.setContentText("Fields not complete!");
      registerAlert.showAndWait();
    } else if (!isValidName(name)) {
      registerAlert.setContentText(
          "Name must be alphabet only."
              + System.lineSeparator()
              + "Name must not have more than 15 characters.");
      registerAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      registerAlert.showAndWait();
    } else if (!isValidEmail(email)) {
      registerAlert.setContentText(
          "Email address is invalid!"
              + System.lineSeparator()
              + "Please enter a valid email address since it cannot be changed once registered.");
      registerAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      registerAlert.showAndWait();
    } else if (!isValidPassword(password)) {
      registerAlert.setContentText(
          "Password is invalid!"
              + System.lineSeparator()
              + "A valid password must have at least 8 characters."
              + System.lineSeparator()
              + "A valid password must contains at least one number and one letter.");
      registerAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      registerAlert.showAndWait();
    } else {
      User user = null;
      if (isAdmin.isSelected()) {
        if (cardHolderManager.find(email) == null)
          user = adminManager.create(name, email, password);
      } else {
        if (adminManager.find(email) == null)
          user = cardHolderManager.create(name, email, password);
      }

      if (user == null) {
        registerAlert.setContentText("User already exists!");
        registerAlert.showAndWait();
      } else {
        Alert success = new Alert(Alert.AlertType.CONFIRMATION);
        success.setTitle("Confirmation");
        success.setHeaderText("Registering User");
        success.setContentText("User successfully created!");
        success.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        success.showAndWait();

        sceneController.activatePane("main", sceneController.getMainController());
      }
    }
  }
}
