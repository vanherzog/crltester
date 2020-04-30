package crltester.test;

import crltester.util.CurrentTime;

/**
 * Helper class to verify date states and time spans.
 * 
 * @author Benjamin Sanno @ IBM
 * @author Marc Hoersken @ IBM
 * @version $Revision: 1.2 2014-10-18$
 */
public class DateVerifier {
	/**
	 * Get date state depending upon time till expiration.
	 * 
	 * @param expirationTimeMilliseconds	Time till expiration.
	 * @param warningTimespanMilliseconds	Time till warning.
	 * @return	Date state.
	 */
	public static DateStatus getDateStatus(long expirationTimeMilliseconds, long warningTimespanMilliseconds) {
		long currentTimeMilliseconds = CurrentTime.getMilliseconds();

		if (expirationTimeMilliseconds >= currentTimeMilliseconds + warningTimespanMilliseconds)
			return DateStatus.GREEN;

		if (expirationTimeMilliseconds >= currentTimeMilliseconds)
			return DateStatus.YELLOW;

		return DateStatus.RED;
	}

	/**
	 * Get formatted time span from milliseconds.
	 * 
	 * @param milliseconds	Time span in milliseconds.
	 * @return	Time span as formatted string.
	 */
	public static String getTimeSpan(long milliseconds) {
		long days, hours, minutes, seconds;
		StringBuffer timeSpan;

		if (milliseconds < 0) {
			milliseconds = Math.abs(milliseconds);
			timeSpan = new StringBuffer("-");
		} else {
			timeSpan = new StringBuffer();
		}

		days = milliseconds / (24 * 60 * 60 * 1000);
		timeSpan.append(days);
		timeSpan.append("d and ");
		milliseconds = milliseconds % (24 * 60 * 60 * 1000);

		hours = milliseconds / (60 * 60 * 1000);
		timeSpan.append(hours);
		timeSpan.append(":");
		milliseconds = milliseconds % (60 * 60 * 1000);

		minutes = milliseconds / (60 * 1000);
		timeSpan.append(minutes);
		timeSpan.append(":");
		milliseconds = milliseconds % (60 * 1000);

		seconds = milliseconds / 1000;
		timeSpan.append(seconds);
		timeSpan.append(".");
		milliseconds = milliseconds % 1000;

		timeSpan.append(milliseconds);
		return timeSpan.toString();
	}

	/**
	 * Get milliseconds from days, hours, minutes and seconds.
	 * 
	 * @param days		Number of days.
	 * @param hours		Number of hours.
	 * @param minutes	Number of minutes.
	 * @param seconds	Number of seconds.
	 * @return			Number of milliseconds.
	 */
	public static long getMilliseconds(long days, long hours, long minutes, long seconds) {
		return (days * 24 * 60 * 60 * 1000)
				+ (hours * 60 * 60 * 1000)
				+ (minutes * 60 * 1000)
				+ (seconds * 1000);
	}
}
