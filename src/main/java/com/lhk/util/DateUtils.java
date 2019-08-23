package com.lhk.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtils {
	public static final String ISO_8601_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	static final String ISO_8601_DATETIME_PATTERN2 = "yyyy-MM-dd'T'HH:mm:ss.SSS";
	public static final String STANDARD_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String STANDARD_DATE_PATTERN = "yyyy-MM-dd";
    public static final String STANDARD_TIME_PATTERN = "HH:mm:ss";
	public static final TimeZone utcTZ = TimeZone.getTimeZone("UTC");

	private static String[] datePatterns = new String[] { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss",
			"MM/dd/yyyy", "yyyy.MM.dd G 'at' HH:mm:ss z", DateUtils.ISO_8601_DATETIME_PATTERN2,  "yyyy-MM-dd'T'HH:mm:ss'Z'",
			DateUtils.ISO_8601_DATETIME_PATTERN,"yyyy-MM-dd HH:mm:ss.S","yyyy-MM-dd HH:mm:ss:SSS" };

	public static Date parse(String dateString) {
		try {
			return org.apache.commons.lang3.time.DateUtils.parseDate(dateString, datePatterns);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalArgumentException("can not parse " + dateString + " to Date object");
		}
	}

	public static Date nowUTC() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());

		long utcTimeStamp = toUTC(cal.getTimeInMillis(), TimeZone.getDefault());

		Calendar utcCal = GregorianCalendar.getInstance(utcTZ);

		utcCal.setTimeInMillis(utcTimeStamp);

		return utcCal.getTime();
	}

	public static Date getEndOfDate(Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);

		return calendar.getTime();

	}

	public static Date getStartOfDate(Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();

	}

	public static Date toUTCDate(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);

		long utcTimeStamp = toUTC(cal.getTimeInMillis(), TimeZone.getDefault());

		Calendar utcCal = Calendar.getInstance();
		utcCal.setTimeInMillis(utcTimeStamp);

		return utcCal.getTime();
	}

	public static long toLocalTime(long time, TimeZone to) {
		return convertTime(time, utcTZ, to);
	}

	public static String formatAsISO8601StandardDateTime(Date date) {
		if (date == null) {
			return "";
		}

		SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_DATETIME_PATTERN);
		return sdf.format(date);
	}

	public static String formatAsStandardDateTime(Date date) {
		if (date == null) {
			return "";
		}

		SimpleDateFormat sdf = new SimpleDateFormat(STANDARD_DATETIME_PATTERN);
		return sdf.format(date);
	}

	public static String formatAsStandardDate(Date date) {
		if (date == null) {
			return "";
		}

		SimpleDateFormat sdf = new SimpleDateFormat(STANDARD_DATE_PATTERN);
		return sdf.format(date);
	}

	public static String formatAsCustomizeDate(Date date, String format) {
        if (date == null) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

	public static long toUTC(long time, TimeZone from) {
		return convertTime(time, from, utcTZ);
	}

	public static long convertTime(long time, TimeZone from, TimeZone to) {
		return time + getTimeZoneOffset(time, from, to);
	}

	public static Date convertTime(Date time, TimeZone from, TimeZone to) {
		long convetedTime = time.getTime() + getTimeZoneOffset(time.getTime(), from, to);

		Calendar utcCal = Calendar.getInstance();
		utcCal.setTimeInMillis(convetedTime);

		return utcCal.getTime();
	}

	private static long getTimeZoneOffset(long time, TimeZone from, TimeZone to) {
		int fromOffset = from.getOffset(time);
		int toOffset = to.getOffset(time);
		int diff = 0;

		if (fromOffset >= 0) {
			if (toOffset > 0) {
				toOffset = -1 * toOffset;
			} else {
				toOffset = Math.abs(toOffset);
			}
			diff = (fromOffset + toOffset) * -1;
		} else {
			if (toOffset <= 0) {
				toOffset = -1 * Math.abs(toOffset);
			}
			diff = (Math.abs(fromOffset) + toOffset);
		}
		return diff;
	}

	public static boolean isAfterByDay(Date oneDate, Date anotherDate) {
		return oneDate.after(anotherDate) && !isSameDay(oneDate, anotherDate);
	}

	public static boolean isSameDay(Date oneDate, Date anotherDate) {
		GregorianCalendar oneCalendar = new GregorianCalendar();
		oneCalendar.setTime(oneDate);

		GregorianCalendar anothereCalendar = new GregorianCalendar();
		anothereCalendar.setTime(anotherDate);

		return oneCalendar.get(Calendar.YEAR) == anothereCalendar.get(Calendar.YEAR)
				&& oneCalendar.get(Calendar.DAY_OF_YEAR) == anothereCalendar.get(Calendar.DAY_OF_YEAR);
	}

	public static boolean isAfterOrEqual(Date oneDate, Date anotherDate) {
		return oneDate.after(anotherDate) || isSameDay(oneDate, anotherDate);
	}

	public static Date offsetDay(Date aDay, int days) {
		GregorianCalendar oneCalendar = new GregorianCalendar();
		oneCalendar.setTime(aDay);
		oneCalendar.add(Calendar.DAY_OF_MONTH, days);

		return oneCalendar.getTime();

	}

	public static Date offsetMinutes(Date aDay, int minutes) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(aDay);
		cal.add(Calendar.MINUTE, minutes);

		return cal.getTime();

	}

	public static Date offsetSeconds(Date aDay, int seconds) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(aDay);
		cal.add(Calendar.SECOND, seconds);

		return cal.getTime();

	}

	public static int diffDays(Date start, Date end) {
		long diff = end.getTime() - start.getTime();

		int diffDays = (int) (diff / (24 * 60 * 60 * 1000));

		return diffDays;

	}

	public static int diffHours(Date start, Date end) {
		long diff = end.getTime() - start.getTime();

		int diffHours = (int) (diff / (60 * 60 * 1000));

		return diffHours;

	}

	public static int diffMinutes(Date start, Date end) {
		// Get msec from each, and subtract.
		long diff = end.getTime() - start.getTime();

		int diffMinutes = (int) (diff / (60 * 1000));

		return diffMinutes;

	}

	public static int diffSeconds(Date start, Date end) {
		// Get msec from each, and subtract.
		long diff = end.getTime() - start.getTime();

		int diffSeconds = (int) (diff / (1000));

		return diffSeconds;

	}

	public static int hourIn24Hours(Date aDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(aDate);

		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	public static int minutesIn60Minutes(Date aDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(aDate);

		return calendar.get(Calendar.MINUTE);
	}

    public static Date asDate(LocalDate localDate) {
        return localDate==null ? null : Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date asDate(LocalDateTime localDateTime) {
        return localDateTime==null ? null : Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

	public static Date asDate(Instant instant) {
		return instant==null ? null : Date.from(instant);
	}

    public static LocalDate asLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

	public static LocalDate asLocalDate(String date) {
		Date d = DateUtils.parse(date);
		return Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

    public static LocalDateTime asLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
