package crltester.test.ldap;

import android.os.Build;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchResults;
import com.novell.ldap.LDAPUrl;

import crltester.test.CRLValidator;
import crltester.test.DateStatus;
import crltester.test.TestStatus;


/**
 * Class performing LDAP-based CRL-DP tests.
 *
 * @author Benjamin Sanno @ IBM
 * @author Markus Dolze @ T-Systems
 * @author Marc Hoersken @ IBM
 * @version $Revision: 1.3 2014-10-18$
 */
public class LDAPTester {



	/**
	 * Check CRL from LDAP-based CRL-DP (LDAP directory).
	 *
	 * @param ldapUrlPath	LDAP URL path.
	 * @return	LDAP CRL state.
	 */
	public static LDAPCrlState checkLdapUrlPath(String ldapUrlPath) {
		LDAPUrl ldapUrl;
		String ldapBase;
		try {
			ldapUrl = new LDAPUrl(ldapUrlPath);
			ldapBase = LDAPUrl.decode(ldapUrl.getDN());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		String ldapCrlAttribute = "certificateRevocationList";
		String[] ldapUrlAttributes = ldapUrl.getAttributeArray();
		if (ldapUrlAttributes != null && ldapUrlAttributes.length == 1) {
			ldapCrlAttribute = ldapUrlAttributes[0];
		}
		return checkLdap(ldapUrl.getHost(), ldapUrl.getPort(),
				ldapBase, ldapUrl.getScope(), ldapUrl.getFilter(),
				ldapCrlAttribute);
	}

	/**
	 * Check CRL from LDAP-based CRL-DP (LDAP directory).
	 *
	 * @param host			LDAP directory hostname.
	 * @param port			LDAP directory TCP-port.
	 * @param base			LDAP base of CRL stored in LDAP directory.
	 * @param scope			LDAP scope to search for CRL.
	 * @param filter		LDAP filter to search for CRL.
	 * @param crlAttribute	LDAP CRL attribute to retrieve CRL from.
	 * @return	LDAP CRL state.
	 */
	protected static LDAPCrlState checkLdap(String host, int port,
			String base, int scope, String filter, String crlAttribute) {
		System.out.print("Trying to connect to " + host + " with port " + port + ": ");

		LDAPConnection connection;
        try {
          connection = LDAPTester.getConnection(host, port);
        } catch (Exception e) {
          System.out.println(e.toString());
          System.out.println("LDAP test is aborted due to a connection error to the LDAP directory.");
          return new LDAPCrlState(base, TestStatus.CONNECTION_ERROR);
        }

		System.out.println("succesfully connected.");

		System.out.println("Trying to search: " + base);
		try {
			return LDAPTester.searchCRL(connection, base, scope, filter, crlAttribute);
		} finally {
			try {
				connection.disconnect();
			} catch (LDAPException e) {
				System.out.println(e.toString());
			}
		}
	}

	/**
	 * Search CRL from LDAP-based CRL-DP (LDAP directory).
	 *
	 * @param connection	LDAP connection.
	 * @param base			LDAP base of CRL stored in LDAP directory.
	 * @param scope			LDAP scope to search for CRL.
	 * @param filter		LDAP filter to search for CRL.
	 * @param crlAttribute	LDAP CRL attribute to retrieve CRL from.
	 * @return	LDAP CRL state.
	 */
	private static LDAPCrlState searchCRL(LDAPConnection connection,
			String base, int scope, String filter, String crlAttribute) {
		String[] attr;
		if (!crlAttribute.endsWith(";binary")) {
			attr = new String[] { crlAttribute, crlAttribute + ";binary" };
		} else {
			attr = new String[] { crlAttribute };
		}

		LDAPSearchResults results;
		try {
			System.out.println("Search in LDAP directory with:");
			System.out.println("    Base :" + base);
			System.out.println("    Filter: " + filter);
			System.out.println("    Attribute: " + attr[0]);
			results = connection.search(base, scope, filter, attr, false);
			System.out.println("Search done.");
		} catch (LDAPException e) {
			System.out.println(e.toString());
			return new LDAPCrlState(base, TestStatus.INCOMPLETE_RESPONSE_ERROR);
		}

		System.out.println("Trying to fetch: " + base);
		return LDAPTester.fetchCRL(results, base, crlAttribute);
	}

	/**
	 * Fetch CRL from LDAP-based CRL-DP (LDAP directory).
	 *
	 * @param results		LDAP search results.
	 * @param base			LDAP base of CRL stored in LDAP directory.
	 * @param crlAttribute	LDAP CRL attribute to retrieve CRL from.
	 * @return	LDAP CRL state.
	 */
	private static LDAPCrlState fetchCRL(LDAPSearchResults results,
			String base, String crlAttribute) {
		List<X509CRL> crls = new ArrayList<X509CRL>();

		// process all entries in the search result
		while (results.hasMore()) {
			LDAPEntry entry;
			try {
				entry = results.next();
			} catch (LDAPException e) {
				// warn about anything that goes wrong, but continue
				System.out.println(e.toString());
				continue;
			}

			// try to fall back to the ;binary attribute, just in case
			LDAPAttribute attribute = entry.getAttribute(crlAttribute);
			if (attribute == null && !crlAttribute.endsWith(";binary")) {
				attribute = entry.getAttribute(crlAttribute + ";binary");
			}
			if (attribute == null) {
				continue;
			}

			/*
			 * Note: certificateRevocationList is a single-value field,
			 * but we process all values nevertheless.
			 */
			byte[][] values = attribute.getByteValueArray();
			for (int index = 0; index < values.length; index++) {
				InputStream inputStream = new ByteArrayInputStream(values[index]);
				try {
					CertificateFactory cf = CertificateFactory.getInstance("X.509");
					X509CRL crl = (X509CRL) cf.generateCRL(inputStream);
					System.out.println("Found CRL with issuer:\n - '" + crl.getIssuerDN().toString() + "'");
					crls.add(crl);
				} catch (CertificateException e) {
					System.out.println(e.toString());
					return new LDAPCrlState(base, TestStatus.PROGRAM_ERROR);
				} catch (CRLException e) {
					System.out.println(e.toString());
					return new LDAPCrlState(base, TestStatus.INVALID_RESPONSE_ERROR);
				} finally {
					try {
						inputStream.close();
					} catch (IOException e) {
						System.out.println(e.toString());
					}
				}
			}
		}

		int crlsSize = crls.size();
		System.out.println("CertificateRevocationLists found: " + crlsSize);
		if (crlsSize > 1) {
			System.out.println("More than one CRL found with the specified filter and attribute! LDAP test is aborted!");
			return new LDAPCrlState(base, TestStatus.TOO_MANY_RESULTS_WARNING);
		} else if (crlsSize == 0) {
			System.out.println("No CRL found with the specified filter and attribute! LDAP test is aborted!\n");
			return new LDAPCrlState(base, TestStatus.NO_RESULTS_WARNING);
		}

		// get the one remaining CRL
		X509CRL crl = crls.iterator().next();
		return LDAPTester.validateCRL(crl);
	}

	/**
	 * Validate CRL from LDAP-based CRL-DP (LDAP directory).
	 *
	 * @param crl	X509 CRL.
	 * @return	LDAP CRL state.
	 */
	private static LDAPCrlState validateCRL(X509CRL crl) {
		Set<TestStatus> testStates = new HashSet<TestStatus>();
		String name = crl.getIssuerDN().toString();
		Date thisUpdate = crl.getThisUpdate();
		Date nextUpdate = crl.getNextUpdate();

		System.out.println("Verification result for issuer:\n - '" + name + "'");

		DateStatus dateStatus = DateStatus.RED;
		if (thisUpdate != null && nextUpdate != null) {
			//18000000 sind 5h
			long warningTimespanMilliseconds = 18000001;
			dateStatus = CRLValidator.validateExpiration(thisUpdate, nextUpdate, warningTimespanMilliseconds);
		} else {
			testStates.add(TestStatus.INCOMPLETE_RESPONSE_ERROR);
		}

		return new LDAPCrlState(name, thisUpdate, nextUpdate, dateStatus, testStates);
	}

	private static final LDAPConnection getConnection(String host, int port) throws LDAPException, InterruptedException{
	  int retry = 0;
      LDAPConnection connection;
      LDAPException exception = null;
      while(retry < 4) {
		  try {
			  connection = new LDAPConnection();
			  connection.connect(host, port);
			  return connection;
		  } catch (LDAPException e) {
			  if (exception != null) {
				  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
					  e.addSuppressed(exception);
				  }
			  }
			  exception = e;
		  }
		  retry++;
	  }
      throw exception;
  }
}
