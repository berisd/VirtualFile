/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class DateUtils {
    public static Instant getLocalDateTimeFromInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    /**
     * The UTC time zone. Not sure if {@link TimeZone#getTimeZone(String)}
     * understands "UTC" in all environments, but it'll fall back to GMT
     * in such cases, which is in practice equivalent to UTC.
     */
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    /**
     * Custom time zone used to interpret date values without a time
     * component in a way that most likely falls within the same day
     * regardless of in which time zone it is later interpreted. For
     * example, the "2012-02-17" date would map to "2012-02-17T12:00:00Z"
     * (instead of the default "2012-02-17T00:00:00Z"), which would still
     * map to "2012-02-17" if interpreted in say Pacific time (while the
     * default mapping would result in "2012-02-16" for UTC-8).
     */
    public static final TimeZone MIDDAY = TimeZone.getTimeZone("GMT-12:00");

    /**
     * Returns a ISO 8601 representation of the given date. This method
     * is thread safe and non-blocking.
     *
     * @see <a href="https://issues.apache.org/jira/browse/TIKA-495">TIKA-495</a>
     * @param date given date
     * @return ISO 8601 date string, including timezone details
     */
    public static String formatDate(Date date) {
        Calendar calendar = GregorianCalendar.getInstance(UTC, Locale.US);
        calendar.setTime(date);
        return doFormatDate(calendar);
    }
    /**
     * Returns a ISO 8601 representation of the given date. This method
     * is thread safe and non-blocking.
     *
     * @see <a href="https://issues.apache.org/jira/browse/TIKA-495">TIKA-495</a>
     * @param date given date
     * @return ISO 8601 date string, including timezone details
     */
    public static String formatDate(Calendar date) {
        // Explicitly switch it into UTC before formatting
        date.setTimeZone(UTC);
        return doFormatDate(date);
    }
    /**
     * Returns a ISO 8601 representation of the given date, which is
     *  in an unknown timezone. This method is thread safe and non-blocking.
     *
     * @see <a href="https://issues.apache.org/jira/browse/TIKA-495">TIKA-495</a>
     * @param date given date
     * @return ISO 8601 date string, without timezone details
     */
    public static String formatDateUnknownTimezone(Date date) {
        // Create the Calendar object in the system timezone
        Calendar calendar = GregorianCalendar.getInstance(TimeZone.getDefault(), Locale.US);
        calendar.setTime(date);
        // Have it formatted
        String formatted = formatDate(calendar);
        // Strip the timezone details before returning
        return formatted.substring(0, formatted.length()-1);
    }
    private static String doFormatDate(Calendar calendar) {
        return String.format(
                Locale.ROOT,
                "%04d-%02d-%02dT%02d:%02d:%02dZ",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND));
    }
}
