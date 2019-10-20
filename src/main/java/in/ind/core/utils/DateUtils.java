package in.ind.core.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * The type Date utils.
 *
 * Created by abhay on 20/10/19.
 */
public class DateUtils {
    /**
     * Get current utc timestamp long.
     *
     * @return the long
     */
    public static long getCurrentUTCTimestamp(){
        return (new DateTime(DateTimeZone.UTC)).getMillis();
    }

    /**
     * Gets date from utc timestamp.
     *
     * @param timestamp the timestamp
     * @return the date from utc timestamp
     */
    public static DateTime getDateFromUTCTimestamp(long timestamp) {
        return new DateTime(timestamp, DateTimeZone.UTC);
    }
}
