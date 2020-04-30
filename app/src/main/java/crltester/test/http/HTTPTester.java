package crltester.test.http;


import android.os.StrictMode;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import crltester.conf.CRLTester;
import crltester.test.CRLValidator;
import crltester.test.DateStatus;
import crltester.test.TestStatus;

/**
 * Class performing HTTP-based CRL-DP tests.
 * 
 * @author Niklas Herzog @ BWI
 * @version $Revision: 1.2 2019-04-05$
 */ public class HTTPTester {

	/**
	 * Check CRL from HTTP-based CRL-DP (HTTP webserver).
	 * 
	 * @param httpUrlPath	HTTP URL path.
	 * @return	HTTP CRL state.
	 */
	public static HTTPCrlState checkHttpUrlPath(String httpUrlPath) {
		URL httpUrl;
		try {
			httpUrl = new URL(httpUrlPath);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		return checkHttpUrlPath(httpUrl);
	}

	/**
	 * Check CRL from HTTP-based CRL-DP (HTTP webserver).
	 * 
	 * @param httpUrl	HTTP URL.
	 * @return	HTTP CRL state.
	 */
	private static HTTPCrlState checkHttpUrlPath(URL httpUrl) {
		System.out.print("Trying to connect to " + httpUrl.getHost() + " with port " + httpUrl.getPort() + ": ");

		URLConnection connection;
		try {
			connection = HTTPTester.getConnection(httpUrl);
		} catch (Exception e) {

			System.out.println(e.toString());
			System.out.println("HTTP test is aborted due to a connection error to the HTTP webserver.");
			return new HTTPCrlState(null, TestStatus.CONNECTION_ERROR);
		}

		System.out.println("succesfully connected.");

		System.out.println("Trying to fetch: " + httpUrl);
		return HTTPTester.fetchCRL(connection); //this
	}

	/**
	 * Fetch CRL from HTTP-based CRL-DP (HTTP webserver).
	 *
	 * @param connection	HTTP URL connection.
	 * @return	HTTP CRL state.
	 */
	private static HTTPCrlState fetchCRL(URLConnection connection) {
		X509CRL crl;
	InputStream inputStream = null;

		try {
			inputStream = connection.getInputStream();
			try {
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				crl = (X509CRL) cf.generateCRL(inputStream);
				System.out.println("Found CRL with issuer:\n - '" + crl.getIssuerDN().toString() + "'");
			} catch (CertificateException e) {
				System.out.println(e.toString());
				return new HTTPCrlState(null, TestStatus.PROGRAM_ERROR);
			} catch (CRLException e) {
				System.out.println(e.toString());
				return new HTTPCrlState(null, TestStatus.INVALID_RESPONSE_ERROR);
			}
		} catch (IOException e) {
			System.out.println(e.toString());
			return new HTTPCrlState(null, TestStatus.INCOMPLETE_RESPONSE_ERROR);
		}
		finally {
          try {
            if (inputStream != null){  
              inputStream.close();
            }
          } catch (IOException e) {
              System.out.println(e.toString());
          }
      }

		System.out.println("CertificateRevocationLists found.");
		return HTTPTester.validateCRL(crl); //this
	}

	/**
	 * Validate CRL from HTTP-based CRL-DP (HTTP webserver).
	 *
	 * @param crl	X509 CRL.
	 * @return	HTTP CRL state.
	 */
	private static HTTPCrlState validateCRL(X509CRL crl) {
		Set<TestStatus> testStates = new HashSet<TestStatus>(); 
		String name = crl.getIssuerDN().toString();
		Date thisUpdate = crl.getThisUpdate();
		Date nextUpdate = crl.getNextUpdate();

		System.out.println("Verification result for issuer:\n - '" + name + "'");

		DateStatus dateStatus = DateStatus.RED;
		if (thisUpdate != null && nextUpdate != null) {
			//18000000 sind 5h
			long warningTimespanMilliseconds = 18000000;
			dateStatus = CRLValidator.validateExpiration(thisUpdate, nextUpdate, warningTimespanMilliseconds);
		} else {
			testStates.add(TestStatus.INCOMPLETE_RESPONSE_ERROR);
		}

		return new HTTPCrlState(name, thisUpdate, nextUpdate, dateStatus, testStates);
	}
	
	private static final HttpURLConnection getConnection(URL entries) throws IOException, InterruptedException{
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy);

	    int retry = 0;
	    HttpURLConnection connection = (HttpURLConnection)entries.openConnection();
	    connection.setRequestProperty("User-Agent", CRLTester.USER_AGENT);
	    while(retry < 4) {
			switch (connection.getResponseCode()) {
				case HttpURLConnection.HTTP_OK:
					System.out.println(entries + " **OK**");
					return connection;
				case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
					System.out.println(entries + " **gateway timeout**");
					break;
				case HttpURLConnection.HTTP_UNAVAILABLE:
					System.out.println(entries + "**unavailable**");
					break;
				default:
					System.out.println(entries + " **unknown response code**.");
					break;
			}
			retry++;
		}
	        System.out.println("Failed retry " + retry + "/" );
        throw new IOException("Can't connect to HTTP Host");
	}
}
