package Support;

import java.util.Date;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/** A class to manage all CardHolders. */
public class CardHolderManager extends UserManager {

  /** Logger */
  private static final Logger logger = Logger.getLogger("fileLogger");
  /** Timer to schedule day closures */
  private Timer closeDayTimer;

  /**
   * Creates a new CardHolderManager.
   *
   * @param transitManager the transit manager this CardHolderManager belongs to
   */
  public CardHolderManager(TransitManager transitManager) {
    super(false, transitManager);

    closeDayTimer = new Timer();
    closeDayTimer.schedule(
        new CheckMidnightTask(this),
        new Date(),
        TimeUnit.MILLISECONDS.convert(10, TimeUnit.SECONDS));
  }

  /**
   * Forces all CardHolder to get off the bus or subway and charge proper amount to each of them.s
   */
  public void closeDay() {
    for (User cardHolder : super.users) {
      ((CardHolder) cardHolder).updatePasses(SimulatedTime.instance.getDate());
      for (Card card : ((CardHolder) cardHolder).getActiveCards()) {
        card.forceExit(transitManager.getTransitSystem(card.getLastTap().getSystemName()));
      }
    }
    transitManager.closeDay();
  }

  public void stopTimers() {
    this.closeDayTimer.cancel();
  }
}
