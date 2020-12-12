/**
 * MIT License
 *
 * Copyright (c) 2017-2020 Julb
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.julb.library.utility.date;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import me.julb.library.utility.constants.Temporals;

/**
 * A date utility to generate standard formatted date and date times.
 * <P>
 * @author Julb.
 */
public final class DateUtility {

    /**
     * Constructor.
     */
    private DateUtility() {
        // Do nothing
    }

    /**
     * Returns the UTC date time in ISO 8601 format.
     * <P>
     * Example: <i>2018-08-01T16:14:27.965Z</i>
     * @param date the input date.
     * @return the UTC date time serialized in ISO 8601 format.
     */
    public static String dateTime(Date date) {
        return DateTimeFormatter.ofPattern(Temporals.ISO_8601_DATE_TIME).format(ZonedDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC));
    }

    /**
     * Returns the "now" UTC date time in ISO 8601 format.
     * <P>
     * Example: <i>2018-08-01T16:14:27.965Z</i>
     * @return the "now" UTC date time serialized in ISO 8601 format.
     */
    public static String dateTimeNow() {
        return DateTimeFormatter.ofPattern(Temporals.ISO_8601_DATE_TIME).format(ZonedDateTime.now(ZoneOffset.UTC));
    }

    /**
     * Returns the current date minus the given value in the given unit.
     * @param value the value to subtract.
     * @param unit the unit.
     * @return the current date minus the given value in the given unit.
     */
    public static String dateTimeMinus(Integer value, TemporalUnit unit) {
        return dateTimeMinus(value.longValue(), unit);
    }

    /**
     * Returns the current date plus the given value in the given unit.
     * @param value the value to add.
     * @param unit the unit.
     * @return the current date plus the given value in the given unit.
     */
    public static String dateTimePlus(Integer value, TemporalUnit unit) {
        return dateTimePlus(value.longValue(), unit);
    }

    /**
     * Returns the current date minus the given value in the given unit.
     * @param value the value to subtract.
     * @param unit the unit.
     * @return the current date minus the given value in the given unit.
     */
    public static String dateTimeMinus(Long value, TemporalUnit unit) {
        return DateTimeFormatter.ofPattern(Temporals.ISO_8601_DATE_TIME).format(ZonedDateTime.now(ZoneOffset.UTC).minus(value, unit));
    }

    /**
     * Returns the current date plus the given value in the given unit.
     * @param value the value to add.
     * @param unit the unit.
     * @return the current date plus the given value in the given unit.
     */
    public static String dateTimePlus(Long value, TemporalUnit unit) {
        return DateTimeFormatter.ofPattern(Temporals.ISO_8601_DATE_TIME).format(ZonedDateTime.now(ZoneOffset.UTC).plus(value, unit));
    }

    /**
     * Returns <code>true</code> if the given date is before now, <code>false</code> otherwise.
     * @param dateTime the date time to compare.
     * @return <code>true</code> if the given date is before now, <code>false</code> otherwise.
     */
    public static Boolean dateTimeBeforeNow(String dateTime) {
        return dateTime.compareTo(dateTimeNow()) <= 0;
    }

    /**
     * Returns <code>true</code> if the given date is after now, <code>false</code> otherwise.
     * @param dateTime the date time to compare.
     * @return <code>true</code> if the given date is after now, <code>false</code> otherwise.
     */
    public static Boolean dateTimeAfterNow(String dateTime) {
        return dateTime.compareTo(dateTimeNow()) > 0;
    }

    /**
     * Returns the UTC date in ISO 8601 format.
     * <P>
     * Example: <i>2018-08-01</i>
     * @param date the input date.
     * @return the UTC date serialized in ISO 8601 format.
     */
    public static String date(Date date) {
        return DateTimeFormatter.ofPattern(Temporals.ISO_8601_DATE).format(ZonedDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC));
    }

    /**
     * Returns the "now" UTC date in ISO 8601 format.
     * <P>
     * Example: <i>2018-08-01</i>
     * @return the "now" UTC date serialized in ISO 8601 format.
     */
    public static String dateNow() {
        return DateTimeFormatter.ofPattern(Temporals.ISO_8601_DATE).format(ZonedDateTime.now(ZoneOffset.UTC));
    }

    /**
     * Returns the current date minus the given value in the given unit.
     * @param value the value to subtract.
     * @param unit the unit.
     * @return the current date minus the given value in the given unit.
     */
    public static String dateMinus(Integer value, TemporalUnit unit) {
        return DateTimeFormatter.ofPattern(Temporals.ISO_8601_DATE).format(ZonedDateTime.now(ZoneOffset.UTC).minus(value, unit));
    }

    /**
     * Returns the current date plus the given value in the given unit.
     * @param value the value to add.
     * @param unit the unit.
     * @return the current date plus the given value in the given unit.
     */
    public static String datePlus(Integer value, TemporalUnit unit) {
        return DateTimeFormatter.ofPattern(Temporals.ISO_8601_DATE).format(ZonedDateTime.now(ZoneOffset.UTC).plus(value, unit));
    }

    /**
     * Returns the "now" UTC time in ISO 8601 format.
     * <P>
     * Example: <i>02:00:00.000</i>
     * @return the "now" UTC time serialized in ISO 8601 format.
     */
    public static String timeNow() {
        return DateTimeFormatter.ofPattern(Temporals.ISO_8601_TIME).format(ZonedDateTime.now(ZoneOffset.UTC));
    }

    /**
     * Returns the "now" seconds since epoch.
     * @return the "now" seconds since epoch.
     */
    public static Long epochSecondNow() {
        return ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();
    }

    /**
     * Returns the second since epoch equivalent of a date.
     * @param date the date.
     * @return the second since epoch equivalent of a date.
     */
    public static Long epochSecond(Date date) {
        if (date == null) {
            return null;
        }
        return ZonedDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC).toEpochSecond();
    }

    /**
     * Returns the second since epoch equivalent of a date.
     * @param dateTime the date time.
     * @return the second since epoch equivalent of a date.
     */
    public static Long epochSecond(String dateTime) {
        if (dateTime == null) {
            return null;
        }
        return ZonedDateTime.ofInstant(DateTimeFormatter.ofPattern(Temporals.ISO_8601_DATE_TIME).parse(dateTime, ZonedDateTime::from).toInstant(), ZoneOffset.UTC).toEpochSecond();
    }

    /**
     * Returns the number of seconds necessary to reach given date time.
     * @param dateTime the date time.
     * @return the number of seconds necessary to reach given date time.
     */
    public static Long secondsUntil(String dateTime) {
        if (dateTime == null) {
            return null;
        }
        return Math.max(0, epochSecond(dateTime) - epochSecondNow());
    }

    /**
     * Returns the date equivalent of second since epoch.
     * @param epochSecond the epoch seconds.
     * @return the date equivalent of second since epoch.
     */
    public static Date dateFromEpochSecond(Long epochSecond) {
        if (epochSecond == null) {
            return null;
        }
        return new Date(TimeUnit.SECONDS.toMillis(epochSecond));
    }

    /**
     * Example: <i>2018-08-01T16:14:27.965Z</i>
     * @param datetime the input date.
     * @return the UTC date time serialized in ISO 8601 format.
     */
    public static Date parseDateTime(String datetime) {
        return Date.from(DateTimeFormatter.ofPattern(Temporals.ISO_8601_DATE_TIME).parse(datetime, ZonedDateTime::from).toInstant());
    }

    /**
     * Returns the given date time in ISO 8601 format in RFC1123 String in the given timezone.
     * @param datetime the input date.
     * @param timeZone the time zone.
     * @return the given date time in ISO 8601 format in RFC1123 String in the given timezone.
     */
    public static String dateTimeToRfc1123(String datetime, TimeZone timeZone) {
        ZonedDateTime parse = DateTimeFormatter.ofPattern(Temporals.ISO_8601_DATE_TIME).parse(datetime, ZonedDateTime::from);
        return parse.withZoneSameInstant(timeZone.toZoneId()).format(DateTimeFormatter.RFC_1123_DATE_TIME);
    }
}
