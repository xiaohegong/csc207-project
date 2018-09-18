package Support;

import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class SimulatedTimeFormatter extends SimpleFormatter {

  @Override
  public String format(LogRecord record) {
    record.setMillis(SimulatedTime.instance.getMillis());
    return super.format(record);
  }
}
