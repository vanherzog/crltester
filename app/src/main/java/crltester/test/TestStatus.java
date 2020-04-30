package crltester.test;

/**
 * @author Marc Hoersken
 * @version $Revision: 1.0 2013-04-12$
 */
public enum TestStatus {
	/**
	 * Test state is unknown error: An unknown runtime error occurred.
	 */
	UNKNOWN_ERROR,

	/**
	 * Test state is program error: An application error occurred.
	 */
	PROGRAM_ERROR,

	/**
	 * Test state is connection error: A connection error occurred.
	 */
	CONNECTION_ERROR,

	/**
	 * Test state is configuration error: A configuration error occurred.
	 */
	CONFIGURATION_ERROR,


	/**
	 * Test state is invalid response error: An invalid response was returned.
	 */
	INVALID_RESPONSE_ERROR,

	/**
	 * Test state is incomplete response error: An incomplete response was returned.
	 */
	INCOMPLETE_RESPONSE_ERROR,


	/**
	 * Test state is a no results warning: Less than expected results were returned.
	 */
	NO_RESULTS_WARNING,

	/**
	 * Test state is a too many results warning: More than expected results were returned.
	 */
	TOO_MANY_RESULTS_WARNING,


	/**
	 * Test state is certificate unknown: The certificate has an unknown OCSP revocation status.
	 */
	CERTIFICATE_UNKNOWN,

	/**
	 * Test state is certificate revoked: The certificate has a revoked OCSP revocation status.
	 */
	CERTIFICATE_REVOKED
}
