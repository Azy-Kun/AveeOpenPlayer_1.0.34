package christophedelory.rss;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * RFC822 date and time-related methods.
 * See also <a href="http://www.ietf.org/rfc/rfc0822.txt">RFC 822</a>.
 * @version $Revision: 91 $
 * @author Christophe Delory
 */
final class RFC822
{
    /**
     * RFC822 date and time format, full version.
     */
    private static final DateFormat FULL_RFC822_DATETIME_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US); // Should not throw NullPointerException, IllegalArgumentException.

    /**
     * RFC822 date and time format, full version, without seconds.
     */
    private static final DateFormat FULL_RFC822_DATETIME_FORMAT_2 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm Z", Locale.US); // Should not throw NullPointerException, IllegalArgumentException.

    /**
     * RFC822 date and time format, compact version.
     */
    private static final DateFormat COMPACT_RFC822_DATETIME_FORMAT = new SimpleDateFormat("d MMM yyyy HH:mm:ss Z", Locale.US); // Should not throw NullPointerException, IllegalArgumentException.

    /**
     * RFC822 date and time format, compact version, without seconds.
     */
    private static final DateFormat COMPACT_RFC822_DATETIME_FORMAT_2 = new SimpleDateFormat("d MMM yyyy HH:mm Z", Locale.US); // Should not throw NullPointerException, IllegalArgumentException.

    /**
     * The ISO8601 {@link Date} formatter for date-time without time zone.
     * The {@link java.util.TimeZone} used here is the default local time zone.
     * The input {@link Date} is a GMT date and time.
     */
    public static final DateFormat ISO8601_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US); // Should not throw NullPointerException, IllegalArgumentException.

    /**
     * Returns a RFC822 date and time string representation of the specified date.
     * @param date the date to represent as a RFC822 date and time string.
     * @return a RFC822 date and time string.
     * @throws NullPointerException if <code>date</code> is <code>null</code>.
     */
    public static String toString(final Date date)
    {
        synchronized(FULL_RFC822_DATETIME_FORMAT)
        {
            return FULL_RFC822_DATETIME_FORMAT.format(date); // Throws NullPointerException if date is null.
        }
    }

    /**
     * Returns a date representation of the specified RFC822 date and time string.
     * @param dateString the RFC822 date and time string to decode as a date.
     * @return a date. Is <code>null</code> if the <code>dateString</code> does not represent a valid RFC822 date and time string.
     * @throws NullPointerException if <code>dateString</code> is <code>null</code>.
     */
    public static Date valueOf(final String dateString)
    {
        Date ret = null;

        synchronized(FULL_RFC822_DATETIME_FORMAT)
        {
            try
            {
                ret = FULL_RFC822_DATETIME_FORMAT.parse(dateString); // May throw ParseException. Throws NullPointerException if dateString is null.
            }
            catch (ParseException e)
            {
                // Continue and try next format.
                ret = null;
            }
        }

        if (ret == null)
        {
            synchronized(FULL_RFC822_DATETIME_FORMAT_2)
            {
                try
                {
                    ret = FULL_RFC822_DATETIME_FORMAT_2.parse(dateString); // May throw ParseException.
                }
                catch (ParseException e)
                {
                    // Continue and try next format.
                    ret = null;
                }
            }
        }

        if (ret == null)
        {
            synchronized(COMPACT_RFC822_DATETIME_FORMAT)
            {
                try
                {
                    ret = COMPACT_RFC822_DATETIME_FORMAT.parse(dateString); // May throw ParseException.
                }
                catch (ParseException e)
                {
                    // Continue and try next format.
                    ret = null;
                }
            }
        }

        if (ret == null)
        {
            synchronized(COMPACT_RFC822_DATETIME_FORMAT_2)
            {
                try
                {
                    ret = COMPACT_RFC822_DATETIME_FORMAT_2.parse(dateString); // May throw ParseException.
                }
                catch (ParseException e)
                {
                    // Continue and try next format.
                    ret = null;
                }
            }
        }

        return ret;
    }

    /**
     * The no-arg constructor shall not be accessible.
     */
    private RFC822()
    {
    }
}
