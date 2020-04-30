package crltester.test.ocsp;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to abstract OCSP hash algorithms.
 * 
 * @author Markus Dolze @ T-Systems
 * @version $Revision: 1.1 2008-03-12$
 */
public class OCSPHashAlgorithms {
	private static Map<String, String> digests;

	static {
		if (OCSPHashAlgorithms.digests == null) {
			OCSPHashAlgorithms.digests = new HashMap<String, String>();
			OCSPHashAlgorithms.digests.put("SHA-1", "1.3.14.3.2.26");
			OCSPHashAlgorithms.digests.put("SHA-256", "2.16.840.1.101.3.4.2.1");
			OCSPHashAlgorithms.digests.put("SHA-512", "2.16.840.1.101.3.4.2.3");
			OCSPHashAlgorithms.digests.put("MD5", "1.2.840.113549.2.5");
			OCSPHashAlgorithms.digests.put("RIPEMD160", "1.3.36.3.2.1");
		}
	}

	/**
	 * @return	OCSP hash algorithm map from name to OID.
	 */
	public static Map<String, String> getDigests() {
		return OCSPHashAlgorithms.digests;
	}
}
