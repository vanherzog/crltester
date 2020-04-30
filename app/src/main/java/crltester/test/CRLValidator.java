package crltester.test;

import java.util.Date;

import crltester.util.CurrentTime;

/**
 * Class performing CRL validation.
 * 
 * @author Benjamin Sanno @ IBM
 * @author Markus Dolze @ T-Systems
 * @author Marc Hoersken @ IBM
 * @version $Revision: 1.3 2014-10-18$
 */
public class CRLValidator {
	/**
	 * Validate CRL expiration.
	 * 
	 * @param thisUpdate	Current date of the CRL.
	 * @param nextUpdate	Next update of the CRL.
	 * @param warningTimespanMilliseconds Time span till expiration warning.
	 * @return	Date status.
	 */
	public static DateStatus validateExpiration(Date thisUpdate, Date nextUpdate, long warningTimespanMilliseconds) {
		long currentTimeMilliseconds = CurrentTime.getMilliseconds();
		Date currentTime = CurrentTime.getDate();

		long thisUpdateMilliseconds = thisUpdate.getTime();
		long nextUpdateMilliseconds = nextUpdate.getTime();
		System.out.println("thisUpdate of CRL is:\n - "  + thisUpdate + "\n   (" + thisUpdateMilliseconds + "ms since 01.01.1970 00:00:00)");
		System.out.println("nextUpdate of CRL is:\n - "  + nextUpdate + "\n   (" + nextUpdateMilliseconds + "ms since 01.01.1970 00:00:00)");
		System.out.println("currentTime of this system:\n - " + currentTime + "\n   (" + currentTimeMilliseconds + "ms since 01.01.1970 00:00:00)");

		long timespanMilliseconds = nextUpdateMilliseconds - currentTimeMilliseconds;
		String timespan = DateVerifier.getTimeSpan(timespanMilliseconds);
		System.out.println("Time till next update:\n - " + timespan + "\n   (" + timespanMilliseconds + "ms)");

		DateStatus dateStatus = DateVerifier.getDateStatus(nextUpdateMilliseconds, warningTimespanMilliseconds);

		switch (dateStatus) {
			case GREEN: {
				System.out.println("CRL is valid!");
				System.out.println("CRL will expire on " + nextUpdate + " (in: " + DateVerifier.getTimeSpan(timespanMilliseconds) + ")");
				break;
			}
			case YELLOW: {
				System.out.println("Warning, CRL will expire soon!");
				System.out.println("CRL will expire on "+ nextUpdate + " (in: " + DateVerifier.getTimeSpan(timespanMilliseconds) + ")");
				break;
			}
			case RED: {
				System.out.println("Critical, CRL already expired!");
				System.out.println("CRL is outdated since " + nextUpdate + " (for: " + DateVerifier.getTimeSpan(timespanMilliseconds) + ")");
				break;
			}
		}

		return dateStatus;
	}
}
