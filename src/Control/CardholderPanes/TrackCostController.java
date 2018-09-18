package Control.CardholderPanes;

import Control.Controller;
import Support.CardHolder;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controls the TrackCost panel for cardholders. Displays information regarding costs of the user in
 * a given month.
 */
public class TrackCostController extends Controller {

  /** Cardholder of which the pane belongs to */
  private CardHolder cardHolder;

  @FXML private Text costSummary;

  /**
   * Constructor for TrackCostController
   *
   * @param cardHolder the cardholder of which the information belongs to
   */
  public TrackCostController(CardHolder cardHolder) {
    super();
    this.cardHolder = cardHolder;
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    displayInfo();
  }

  /**
   * Displays the stats regarding total and average cost of trips for cardholder. Indicates the
   * amount they would have saved if they purchased a monthly pass.
   */
  private void displayInfo() {
    StringBuilder output = new StringBuilder();

    double average = cardHolder.trackAvgCost();
    int numTrips = cardHolder.getNumTrips();
    double total = cardHolder.getMonthlyCost();

    output.append("Number of trips taken: \t").append(numTrips);
    output.append(newLine).append(newLine);
    output.append("Total amount spent: \t\t$").append(total);
    output.append(newLine).append(newLine);

    if (numTrips == 0) output.append("Average per trip: \t\t$").append("Not Applicable");
    else output.append("Average per trip: \t\t$").append(average);
    output.append(newLine).append(newLine).append(newLine);
    output.append("Monthly pass would've cost: \t\t$" + "90").append(newLine);

    if (90 - total < 0 && cardHolder.getPasses().size() == 0)
      output.append("You would have saved $").append(total - 90).append(" with a monthly pass!");

    costSummary.setText(output.toString());
  }
}
