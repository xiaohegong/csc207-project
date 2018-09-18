package Control.AdminPanes;

import Control.Controller;
import Support.Admin;
import Support.SimulatedTime;
import Support.TransitManager;
import Support.TransitSystem;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Controller for Viewing Stats by admins. Includes revenue and cost with a chart displaying profit
 */
public class ViewStatsController extends Controller implements Initializable {

  /** The list of transit system that can be managed by this admin user */
  @FXML private ChoiceBox<TransitSystem> transitSys;
  /** The revenue and cost data displayed in text */
  @FXML private Text displayedStats;
  /** The scroll pane to display text on */
  @FXML private ScrollPane scrollPane = new ScrollPane();
  /** Toggle buttons to select duration of stats the user would like to view */
  @FXML private ToggleButton fourWeeks, threeMonths, oneYear;
  /** Statistics graph to view revenue in selected duration */
  @FXML private LineChart<String, Double> revenueGraph;
  /** The x axis of statistics graph */
  @FXML private NumberAxis yAxis;
  /** The y axis of statistics graph */
  @FXML private CategoryAxis xAxis;
  /** Informs the user to add a transit system if none has been added */
  @FXML private Text hint;

  private Admin admin;
  private TransitManager transitManager;
  private TransitSystem tsToView;

  /**
   * Constructor for controller of Admin Panel, stores information about this admin user and the
   * transit systems under this user's management.
   *
   * @param admin the admin user
   */
  public ViewStatsController(Admin admin, TransitManager tm) {
    super();
    this.admin = admin;
    this.transitManager = tm;
    transitSys = new ChoiceBox<>();
    displayedStats = new Text();
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    // Set choice box to select a transit system
    setChoiceBox();

    // Set stats displayed and fit with scrollPane width
    displayStats();

    // Check if any transit system is selected to view
    selectTransitSystem();
  }

  /**
   * Set displayedStats to get all the statistics for this Admin user to view. Set displayed text to
   * fit the width of the scroll pane.
   */
  private void displayStats() {
    displayedStats.setText(this.admin.getDailyReport());
    fitTextToScroll(displayedStats, scrollPane);
  }

  /**
   * Set transitSys, a choice box, to get the list of transit system managed by this Admin user, and
   * make it available for displaying.
   */
  private void setChoiceBox() {
    if (admin.getTransitSystems().size() == 0) {
      String msg =
          "None of the transit systems is under your management. Please add a transit system first by selecting \"Manage Transit System\" on the left menu bar.";
      hint.setText(msg);
    } else {
      super.setChoiceBox(transitSys, admin.getTransitSystems());
    }
  }

  /**
   * Get the transit system selected to view by this Admin user and display selected duration of
   * graphs if any.
   */
  private void selectTransitSystem() {
    tsToView = transitSys.getSelectionModel().getSelectedItem();
    transitSys.setOnAction(
        event -> {
          tsToView = transitSys.getSelectionModel().getSelectedItem();
        });
    selectFourWeeks();
    selectThreeMonths();
    selectOneYear();
  }

  /**
   * Set the toggle button <fourWeeks> to draw graph and turn to green color (to indicate it has
   * been selected). Display revenue received in the past four weeks if <fourWeeks> was selected.
   */
  private void selectFourWeeks() {
    fourWeeks.setOnMouseClicked(
        event -> {
          setToggleStyle(fourWeeks, oneYear, threeMonths);
          setGraph(SimulatedTime.instance.getDate().minusWeeks(4));
        });
  }

  /**
   * Set the toggle button <threeMonths> to draw graph and turn to green color (to indicate it has
   * been selected). Display revenue received in the past 3 months if <threeMonths> was selected.
   */
  private void selectThreeMonths() {
    threeMonths.setOnMouseClicked(
        event -> {
          setToggleStyle(threeMonths, fourWeeks, oneYear);
          setGraph(SimulatedTime.instance.getDate().minusMonths(3));
        });
  }

  /**
   * Set the toggle button <oneYear> to draw graph and turn to green color (to indicate it has been
   * selected). Display revenue received in the past one year if <oneYear> was selected.
   */
  private void selectOneYear() {
    oneYear.setOnMouseClicked(
        event -> {
          setToggleStyle(oneYear, fourWeeks, threeMonths);
          setGraph(SimulatedTime.instance.getDate().minusYears(1));
        });
  }

  /**
   * Set the LineChart graph <revenueGraph> to corresponding graph of the given <day> by adding data
   * from the selected transit system.
   *
   * @param date LocalDate
   */
  private void setGraph(LocalDate date) {
    if (tsToView != null) {
      revenueGraph.getData().clear();
      XYChart.Series<String, Double> data = tsToView.getRevenueData(date);

      revenueGraph.setAnimated(false);

      // Stop auto-ranging if there is no data
      if (data.getData().isEmpty()) {
        yAxis.setAutoRanging(false);
        xAxis.setAutoRanging(false);
      } else {
        revenueGraph.getData().add(data);
      }
    }
  }
}
