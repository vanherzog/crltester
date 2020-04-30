package crltester.test;

/**
 * @author Benjamin Sanno @ IBM
 * @version $Revision: 1.0 2009-11-26$
 */
public enum DateStatus {
	/**
	 * Date state is green: CRL is not within expiration warning time span.
	 */
	GREEN,

	/**
	 * Date state is yellow: CRL is within expiration warning time span.
	 */
	YELLOW,

	/**
	 * Date state is red: CRL is already expired.
	 */
	RED
};
