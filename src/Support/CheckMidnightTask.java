package Support;

import java.time.LocalDate;
import java.util.TimerTask;
import java.util.logging.Logger;

/** Class to handle closing day, which automatically is executed at CLOSE_TIME every day. */
class CheckMidnightTask extends TimerTask {
  /** Logger */
  private static final Logger logger = Logger.getLogger("fileLogger");
  /** CardHolderManager of the system */
  private CardHolderManager chm;

  /**
   * Constructor of the the class
   *
   * @param chm CardholderManager associated with the system
   */
  public CheckMidnightTask(CardHolderManager chm) {
    this.chm = chm;
  }

  /** Checks to see if it's a new day. Close day accordingly. */
  public void run() {
    LocalDate now = SimulatedTime.instance.getDate();
    if (!now.equals(chm.transitManager.getBusinessDate())) {
      Long daysPast =
          SimulatedTime.instance.getElapsedDays(now, chm.transitManager.getBusinessDate());
      logger.info(chm.transitManager.getBusinessDate().toString());
      for (int i = 0; i < daysPast; i++) {
        chm.closeDay();
      }
    }
  }
}
