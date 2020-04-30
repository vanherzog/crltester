package crltester.util;

import java.util.Date;

/**
 * @author Marc Hoersken @ IBM
 * @version $Revision: 1.1 2014-10-18$
 */
public class CurrentTime {
	// Freeze the current time as of program launch.
	private static long milliseconds = System.currentTimeMillis(); 

	/**
	 * @return	Time of program launch in milliseconds.
	 */
	public static long getMilliseconds() {
		return CurrentTime.milliseconds;
	}

	/**
	 * @return	Time of program launch as Date instance.
	 */
	public static Date getDate() {
		return new Date(CurrentTime.milliseconds);
	}
}
