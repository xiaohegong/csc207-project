package Control;

import Support.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the MainPage. Transitions between different pages such as registering, temporary
 * users to purchase tokens, and logging users into the their panes.
 */
public class MainPageController extends Controller implements Initializable {
  @FXML private TextField emailInput;
  @FXML private PasswordField passwordInput;
  @FXML private ImageView login;
  @FXML private Text register;
  @FXML private Text tempUser;
  @FXML private Text time;
  @FXML private ImageView logo;

  /** AdminManager associated with the system */
  private AdminManager adminManager;
  /** CardHolderManager associated with the system */
  private CardHolderManager cardHolderManager;
  /** TransitManager associated with the system */
  private TransitManager transitManager;

  /**
   * Constructor for Main page controller
   *
   * @param sceneController allows switching between scenes
   * @param adminManager allows user to be an admin
   * @param cardHolderManager allows user to be a card holder
   * @param transitManager TransitManager of the system
   */
  MainPageController(
      SceneController sceneController,
      CardHolderManager cardHolderManager,
      AdminManager adminManager,
      TransitManager transitManager) {
    super(sceneController);
    this.adminManager = adminManager;
    this.cardHolderManager = cardHolderManager;
    this.transitManager = transitManager;
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    displayTime(time);
    login.setOnMouseClicked(
        event -> {
          loginProcess();
        });

    emailInput.setOnKeyPressed(
        event -> {
          if (event.getCode() == KeyCode.ENTER) loginProcess();
        });
    passwordInput.setOnKeyPressed(
        event -> {
          if (event.getCode() == KeyCode.ENTER) loginProcess();
        });

    register();
    purchaseToken();
  }

  /** Verifies whether the user is an admin or cardholder. Activates cardholderPane or adminPane */
  private void loginProcess() {
    String emailIn = emailInput.getCharacters().toString().toLowerCase();
    String passIn = passwordInput.getCharacters().toString();

    User cardholder = this.cardHolderManager.find(emailIn);
    User admin = this.adminManager.find(emailIn);
    User user = null;
    if (admin != null) {
      user = admin;
    } else if (cardholder != null) {
      user = cardholder;
    }

    if (user != null && user.login(passIn)) {
      if (user instanceof Admin) {
        Controller adminPane =
            new AdminController(sceneController, (Admin) user, adminManager, transitManager);

        sceneController.activatePane("admin", adminPane);
      } else {
        Controller cardholderPane =
            new CardholderController(sceneController, (CardHolder) user, transitManager);

        sceneController.activatePane("cardholder", cardholderPane);
      }
    } else {
      Alert loginError = new Alert(Alert.AlertType.WARNING);
      loginError.setTitle("Warning");
      loginError.setHeaderText("Log In Error");
      loginError.setContentText("Incorrect password or email!");
      loginError.showAndWait();
    }
    passwordInput.clear();
  }

  /** Calls register pane when button is clicked. */
  private void register() {
    register.setOnMouseClicked(
        event -> {
          Controller registerController =
              new RegisterController(sceneController, cardHolderManager, adminManager);

          sceneController.activatePane("register", registerController);
        });
  }

  /** Calls TempUserPane when button is clicked */
  private void purchaseToken() {
    tempUser.setOnMouseClicked(
        event -> {
          Controller tempUser = new TempUserController(sceneController, transitManager);
          sceneController.activatePane("tempUser", tempUser);
        });
  }
}
