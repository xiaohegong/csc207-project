package Control;

import Support.SimulatedTime;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Parent class of all different controllers of different pages. Includes some general features
 * shared by two or more controllers.
 */
public class Controller implements Initializable {

  /** A newLine for easier output */
  protected String newLine = System.lineSeparator();
  /** Controls switching between panes */
  SceneController sceneController;

  /** Text to return to MainPage when pressed */
  @FXML private Text back;

  @FXML private Text time;

  /**
   * Constructor for Controller
   *
   * @param sceneController allows switching between scenes
   */
  public Controller(SceneController sceneController) {
    this.sceneController = sceneController;
  }

  /** OverLoading constructor for those that do not need sceneController */
  public Controller() {}

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    displayTime(time);
    back.setOnMouseClicked(
        event -> {
          sceneController.activatePane("main", sceneController.getMainController());
        });
  }

  /**
   * Sets <back> with the specific Text depending on the pane.
   *
   * @param back pane-specific back/log out Text
   */
  void setBack(Text back) {
    this.back = back;
  }

  /**
   * Sets the ChoiceBox with list of choices
   *
   * @param choiceBox ChoiceBox to be set
   * @param list List of items to be placed in the ChoiceBox
   * @param <T> Generic type of items in ChoiceBox
   */
  protected <T> void setChoiceBox(ChoiceBox<T> choiceBox, ArrayList<T> list) {
    ObservableList<T> choiceList = FXCollections.observableArrayList(list);
    choiceBox.getItems().clear();
    choiceBox.getItems().addAll(choiceList);
    choiceBox.getSelectionModel().selectFirst();
  }

  /**
   * Changes the internal pane of <pane> to resource pane.
   *
   * @param pane pane of which its internal pane is to be changed
   * @param resource file path for fxml for pane to be switched to
   * @param controller controller for the new pane
   */
  void changePane(Pane pane, String resource, Controller controller) {
    FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
    loader.setController(controller);
    try {
      pane.getChildren().clear();
      pane.getChildren().add(loader.load());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  /**
   * Set selected toggle button <selectedButton> to green and unselected toggle button
   * <unselectedButton> to white.
   *
   * @param selectedButton ToggleButton
   * @param unselectedButton ToggleButton
   */
  protected void setToggleStyle(ToggleButton selectedButton, ToggleButton unselectedButton) {
    unselectedButton.setStyle("-fx-background-color: white; -fx-text-fill: #45931f;");
    selectedButton.setStyle("-fx-background-color: #45931f; -fx-text-fill: white;");
  }

  /**
   * Set selected toggle button <selectedButton> to green and unselected toggle buttons
   * <unselectedButtonOne> and <unselectedButtonTwo> to white.
   *
   * @param selectedButton ToggleButton
   * @param unselectedButtonOne ToggleButton
   * @param unselectedButtonTwo ToggleButton
   */
  protected void setToggleStyle(
      ToggleButton selectedButton,
      ToggleButton unselectedButtonOne,
      ToggleButton unselectedButtonTwo) {
    unselectedButtonOne.setStyle("-fx-background-color: white; -fx-text-fill: #45931f;");
    unselectedButtonTwo.setStyle("-fx-background-color: white; -fx-text-fill: #45931f;");
    selectedButton.setStyle("-fx-background-color: #45931f; -fx-text-fill: white;");
  }

  /**
   * Set the given text <text> to be able to fit the <scrollPane>, in order to scroll down long
   * text.
   *
   * @param text Text
   * @param scrollPane ScrollPane
   */
  protected void fitTextToScroll(Text text, ScrollPane scrollPane) {
    text.wrappingWidthProperty().bind(scrollPane.widthProperty());
    scrollPane.setFitToWidth(true);
    scrollPane.setContent(text);
  }

  /**
   * Displays the current time (based on the application)
   *
   * @param time time to start
   */
  void displayTime(Text time) {
    Timeline clock =
        new Timeline(
            new KeyFrame(
                Duration.ZERO,
                e -> {
                  LocalDateTime now = SimulatedTime.instance.now();
                  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss");
                  time.setText(formatter.format(now));
                }),
            new KeyFrame(Duration.seconds(1)));
    clock.setCycleCount(Animation.INDEFINITE);
    clock.play();
  }
}
