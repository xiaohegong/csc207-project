package Control;

import Support.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Logger;

public class Main extends Application {

  /** Logger */
  private static final Logger logger = Logger.getLogger("fileLogger");
  /** TransitManager associated with the system */
  private TransitManager transitManager;
  /** AdminManager associated with the system */
  private AdminManager adminManager;
  /** CardHolderManager associated with the system */
  private CardHolderManager cardHolderManager;

  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Load routes for subway/bus lines/routes and stations/stops from specified file path.
   *
   * @param transitManager the TransitManager to add Support.TransitSystem. (to make everything
   *     non-static in phase 2).
   * @param filePath The file path that is to be read from.
   */
  private static void loadRoute(TransitManager transitManager, String filePath) {
    Alert fileNotExistAlert = new Alert(Alert.AlertType.ERROR);
    fileNotExistAlert.setTitle("System Error");
    Optional<ButtonType> result = Optional.empty();

    File f;
    Scanner sc;
    try {
      f = new File(filePath);
      sc = new Scanner(f);

      try {
        while (sc.hasNextLine()) {
          String transitSystemName = sc.nextLine();
          transitManager.addTransitSystem(transitSystemName);

          TransitSystem ts = transitManager.getTransitSystem(transitSystemName);

          for (int k = 0; k < 2; k++) {
            String transitType = sc.next().toLowerCase();
            int numRoutes = Integer.parseInt(sc.next());

            sc.nextLine();
            for (int i = 0; i < numRoutes; i++) {
              int routeNum = Integer.parseInt(sc.next());

              Route route;
              if (transitType.equals("subway")) route = new Route(RouteTypes.Subway, routeNum);
              else route = new Route(RouteTypes.Bus, routeNum);
              int numStations = Integer.parseInt(sc.next());

              sc.nextLine();

              for (int j = 0; j < numStations; j++) {
                String name = sc.nextLine();
                String id = sc.next();
                int x = Integer.parseInt(sc.next());
                int y = Integer.parseInt(sc.next());

                boolean transfer = sc.next().equals("1");
                boolean isSubway = route.routeType == RouteTypes.Subway;

                if (sc.hasNextLine()) sc.nextLine();

                Station station = new Station(Integer.parseInt(id), name, isSubway, x, y, transfer);
                route.addStation(station);
              }
              ts.addRoute(routeNum, route);
            }
          }
        }
      } catch (Exception e) {
        fileNotExistAlert.setHeaderText("System File Format Error");
        fileNotExistAlert.setContentText("System file format is incorrect!");
        result = fileNotExistAlert.showAndWait();
      }
    } catch (FileNotFoundException e) {
      fileNotExistAlert.setHeaderText("System Loading Error");
      fileNotExistAlert.setContentText("System file routes.txt not found!");
      result = fileNotExistAlert.showAndWait();
    }
    if (result.isPresent() && result.get() == ButtonType.OK) {
      System.exit(0);
    }
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    FileLogger.init();

    Object serializedTransitSystems = Serializer.readFromFile("serializedTransit.ser");
    Object serializedAdmins = Serializer.readFromFile("serializedAdmin.ser");
    Object serializedCardholders = Serializer.readFromFile("serializedCardholder.ser");
    Object serializedTime = Serializer.readFromFile("serializedTime.ser");
    Object serializedBusinessDate = Serializer.readFromFile("serializedBusinessDate.ser");

    boolean loadSuccess = true;

    if (serializedTime instanceof Long) {
      SimulatedTime.instance.setOffset((Long) serializedTime);
    } else {
      logger.warning("Unable to deserialize clock." + System.lineSeparator());
      loadSuccess = false;
    }

    transitManager = new TransitManager();
    if (serializedTransitSystems instanceof Collection) {
      transitManager.setTransitSystemMap((ArrayList<TransitSystem>) serializedTransitSystems);
    } else {
      logger.warning(
          "Unable to deserialize transit systems. Creating new transit systems."
              + System.lineSeparator());
      transitManager.addTransitSystem("GO Transit");
      transitManager.addTransitSystem("TTC");
      loadRoute(transitManager, "phase2/routes.txt");
      loadSuccess = false;
    }
    if (serializedBusinessDate instanceof LocalDate) {
      transitManager.setBusinessDate((LocalDate) serializedBusinessDate);
    } else {
      logger.warning("Unable to deserialize transit manager's business date.");
    }

    adminManager = new AdminManager(transitManager);
    if (serializedAdmins instanceof Collection) {
      adminManager.setUsers((ArrayList<User>) serializedAdmins);
    } else {
      logger.warning("Unable to deserialize admin users." + System.lineSeparator());
      loadSuccess = false;
    }

    cardHolderManager = new CardHolderManager(transitManager);
    if (serializedCardholders instanceof Collection) {
      cardHolderManager.setUsers((ArrayList<User>) serializedCardholders);
    } else {
      logger.warning("Unable to deserialize cardholders." + System.lineSeparator());
      loadSuccess = false;
    }

    if (!loadSuccess) {
      Alert error = new Alert(Alert.AlertType.ERROR);
      error.setTitle("Error");
      error.setHeaderText("Cannot Load Data");
      error.setContentText("Unable to load all user data. New user data has been created.");
      error.showAndWait();
    }

    Pane root = new Pane();
    Scene scene = new Scene(root, 1200, 750);
    SceneController sceneController = new SceneController(scene);

    Controller mainPage =
        new MainPageController(sceneController, cardHolderManager, adminManager, transitManager);

    sceneController.setMainController(mainPage);

    sceneController.addResource("main", "mainPage.fxml");
    sceneController.addResource("register", "registerPane.fxml");
    sceneController.addResource("admin", "AdminPane.fxml");
    sceneController.addResource("cardholder", "CardholderPane.fxml");
    sceneController.addResource("tempUser", "TempUser.fxml");

    sceneController.activatePane("main", mainPage);

    primaryStage.setResizable(false);
    primaryStage.setTitle("Voyageur");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  @Override
  public void stop() {
    cardHolderManager.stopTimers();

    logger.info("Serializing user info." + System.lineSeparator());
    Serializer.saveToFile("serializedTransit.ser", transitManager.getTransitSystems());
    Serializer.saveToFile("serializedAdmin.ser", adminManager.getUsers());
    Serializer.saveToFile("serializedCardholder.ser", cardHolderManager.getUsers());
    Serializer.saveToFile("serializedTime.ser", SimulatedTime.instance.getOffset());
    Serializer.saveToFile("serializedBusinessDate.ser", transitManager.getBusinessDate());
  }
}
