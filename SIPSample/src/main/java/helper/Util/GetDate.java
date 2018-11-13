package helper.Util;

/**
 * Created by Nigussie on 09.11.2015.
 */
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class GetDate {



    public static String getShortDateTimeString(long millisec) {
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTimeInMillis(millisec);
            Calendar now = Calendar.getInstance();

            SimpleDateFormat fmt = null;
            if (cal.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
                if (cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) {
                    fmt = new SimpleDateFormat("HH:mm");
                } else {
                    fmt = new SimpleDateFormat("MMM dd");
                }
            } else {
                int monthDiff = 12 * (now.get(Calendar.YEAR) - cal.get(Calendar.YEAR))
                        + (now.get(Calendar.MONTH) - cal.get(Calendar.MONTH));
                if (monthDiff < 6)
                    fmt = new SimpleDateFormat("MMM dd");
                else
                    fmt = new SimpleDateFormat("yyyy");
            }

            return fmt.format(cal.getTime());
        }

        public static String getLongDateTimeString(long millisec) {
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTimeInMillis(millisec);

            return DateFormat.getDateTimeInstance().format(cal.getTime());
        }
    }


