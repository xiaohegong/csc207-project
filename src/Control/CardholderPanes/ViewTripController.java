package Control.CardholderPanes;

import Control.Controller;
import Support.CardHolder;
import Support.Trip;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for ViewTrip pane. Displays three most recent trips taken by the user including
 * location, time, and fare charged.
 */
public class ViewTripController extends Controller {

  /** Cardholder of which the pane belongs to */
  private CardHolder cardHolder;

  @FXML private Text trips;
  @FXML private ScrollPane scrollPane;

  /**
   * Constructor for ViewTrip pane for given cardholder
   *
   * @param cardHolder cardholder of which the pane belongs to
   */
  public ViewTripController(CardHolder cardHolder) {
    super();
    this.cardHolder = cardHolder;
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    List<Trip> trips = cardHolder.getMostRecentTrips();

    StringBuilder output = new StringBuilder();

    if (trips.size() == 0) output.append("You have not taken any trips yet!");
    else {
      for (Trip trip : trips) {
        output.append(newLine);
        output.append(trip.toString());
        output.append(newLine);
      }
    }
    this.trips.setText(output.toString());
    fitTextToScroll(this.trips, scrollPane);
  }
}
