package Control;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.util.HashMap;

/** Switches between different scenes as requested by other classes. */
public class SceneController {
  /** The controller for MainPageController */
  private Controller mainController;

  /** HashMap for fxml file resources */
  private HashMap<String, String> resourceHashMap;

  /** The scene where the panes are to be displayed on */
  private Scene defaultScene;

  /**
   * Constructor for SceneController
   *
   * @param defaultScene the scene on which panes are to be displayed on
   */
  SceneController(Scene defaultScene) {
    this.defaultScene = defaultScene;
    resourceHashMap = new HashMap<>();
    mainController = null;
  }

  /**
   * Getter for mainController
   *
   * @return controller for mainPage
   */
  public Controller getMainController() {
    return mainController;
  }

  /**
   * Setter for mainController
   *
   * @param mainController the mainController to be set
   */
  public void setMainController(Controller mainController) {
    this.mainController = mainController;
  }

  /**
   * Adds a fxml file path for given name of the pane
   *
   * @param name the pane name
   * @param resource fxml file path for the pane
   */
  public void addResource(String name, String resource) {
    resourceHashMap.put(name, resource);
  }

  /**
   * Sets the root of the scene with designated pane and controller
   *
   * @param name name of the pane
   * @param controller controller for the pane
   */
  public void activatePane(String name, Controller controller) {
    Pane root = new Pane();

    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(resourceHashMap.get(name)));
      loader.setController(controller);
      root = loader.load();
    } catch (Exception ignored) {
    }
    defaultScene.setRoot(root);
  }
}
