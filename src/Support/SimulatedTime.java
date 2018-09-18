package Support;

import java.io.Serializable;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

/** Simulates the time of the system. */
public class SimulatedTime implements Serializable {

  /** Logger, mainly used to log whenever time skips ahead */
  private static final Logger logger = Logger.getLogger("fileLogger");
  /** the instance of time of the system */
  public static SimulatedTime instance = new SimulatedTime();
  /** Simulated clock. Based on offsetting the system clock. */
  private Clock clock;
  /** How much the clock is offset from the system clock. */
  private Long offset;

  /** Constructor for simulated time */
  private SimulatedTime() {
    clock = Clock.systemDefaultZone();
    offset = (long) 0;
  }

  /** Skips the time by 15 min */
  public void skipTime() { // 15 minutes by default
    clock = Clock.offset(clock, Duration.ofMinutes(15));
    logger.info(
        "Time has been set 15 minutes into the future. The current time is now "
            + getTime()
            + System.lineSeparator());
    this.offset += 15;
  }

  /**
   * Skips the time by amount minutes entered
   *
   * @param minutes minutes to skip the system by
   */
  public void skipTime(long minutes) {
    if (minutes < 0) {
      logger.warning(
          "Can not set time to an instant in the past. If you want to reset time, please delete all user data.");
    } else {
      clock = Clock.offset(clock, Duration.ofMinutes(minutes));
      logger.info(
          String.format(
              "Time has been set %d minutes into the future. The current time is now %s%n",
              minutes, getTime()));
      this.offset += minutes;
    }
  }

  /**
   * Returns the current time in current timezone.
   *
   * @return LocalDateTime
   */
  public LocalDateTime now() {
    return LocalDateTime.ofInstant(clock.instant(), clock.getZone());
  }

  /**
   * Returns the instance of clock
   *
   * @return Instant
   */
  public Instant getInstant() {
    return clock.instant();
  }

  /**
   * Returns the current time
   *
   * @return LocalTime
   */
  public LocalTime getTime() {
    return now().toLocalTime();
  }

  /**
   * Returns the current date
   *
   * @return LocalDate
   */
  public LocalDate getDate() {
    return now().toLocalDate();
  }

  /**
   * Returns the clock time in milliseconds
   *
   * @return long
   */
  public long getMillis() {
    return clock.millis();
  }

  /**
   * Returns the elapsed time between two time in minutes
   *
   * @param start start of time
   * @param end end of time
   * @return difference of time in minutes
   */
  public long getElapsedMinutes(LocalTime start, LocalTime end) {
    if (end.isBefore(start)) {
      // If end comes before start, swap them
      LocalTime temp = start;
      start = end;
      end = temp;
    }
    return ChronoUnit.MINUTES.between(start, end);
  }

  /**
   * Returns the elapsed time between two date time in minutes
   *
   * @param start start date time
   * @param end end date time
   * @return difference of date time in minutes
   */
  public long getElapsedMinutes(LocalDateTime start, LocalDateTime end) {
    return getElapsedMinutes(start.toLocalTime(), end.toLocalTime());
  }

  /**
   * Returns the elapsed time between two days in days
   *
   * @param start start date
   * @param end end date
   * @return difference of date in days
   */
  public long getElapsedDays(LocalDate start, LocalDate end) {
    if (end.isBefore(start)) {
      // If end comes before start, swap them
      LocalDate temp = start;
      start = end;
      end = temp;
    }
    return ChronoUnit.DAYS.between(start, end);
  }

  /**
   * Returns if current time is during rush hour
   *
   * @param time time to check if in rush hour
   * @return boolean
   */
  public boolean duringRushHour(LocalTime time) {
    // Rush hour is between 7-9am and 5-7pm
    boolean duringMorningRH = time.isAfter(LocalTime.of(7, 0)) && time.isBefore(LocalTime.of(9, 0));
    boolean duringEveningRH =
        time.isAfter(LocalTime.of(17, 0)) && time.isBefore(LocalTime.of(19, 0));
    return duringMorningRH || duringEveningRH;
  }

  /**
   * Returns the difference between the system time and actual time
   *
   * @return difference in long
   */
  public Long getOffset() {
    return this.offset;
  }

  /**
   * Sets the simulated clock to be offset from the system clock by a specific amount. Should only
   * be used in serialization. Use SimulatedTime.skipTime for use in code.
   *
   * @param offset what to offset the system clock by.
   */
  public void setOffset(Long offset) {
    this.offset = offset;
    clock = Clock.offset(Clock.systemDefaultZone(), Duration.ofMinutes(offset));
  }
}
