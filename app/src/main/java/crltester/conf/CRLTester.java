package crltester.conf;

import android.os.AsyncTask;
import android.util.Log;

import com.example.andi.OCSP;
import com.example.andi.Server;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import crltester.test.State;
import crltester.test.http.HTTPTester;
import crltester.test.ldap.LDAPTester;
import crltester.test.ocsp.OCSPTester;

/**
 * @author Benjamin Sanno @ IBM
 * @author Markus Dolze @ T-Systems
 * @author Marc Hoersken @ IBM
 * @version $Revision: 1.4 2015-02-16$
 */
public class CRLTester {
	/**
	 * User-Agent string including program version.
	 */
	public static final String USER_AGENT = "CRL-Tester/1.4.1 (BWI Systeme GmbH)";
	private static final String TAG = "CRL_Tester";

	/**
	 * @param args	Main program argument string array.
	 */
	public static void main(String[] args) {
		System.out.println("--- CRL-Tester Version 1.4.1, 19.06.2017");
		System.out.println("--- Copyright 2009-2015 by BWI Systeme GmbH");

		System.out.println("--- CRL-Tester starting ...");


	}

	public static void performLdapTests(ArrayList<Server> checkedServerList) {
		System.out.println("--- Starting LDAP tests <<<");

		try {
				// do LDAP tests with different URls
				for (Server ldapServer : checkedServerList) {
					ldapServer.setStatus(LDAPTester.checkLdapUrlPath(ldapServer.getLink()));
				}
		} catch (RuntimeException e) {
			System.out.println(e.toString());
		}

		System.out.println("--- Stopping LDAP tests <<<");
	}

	public static void performOcspTests(ArrayList<Server> checkedServerList) {
		System.out.println("--- Starting OCSP tests <<<");
		try{
				// do OCSP tests with different certificates
				OCSPTester ocspTester = new OCSPTester(checkedServerList);
				for (Server ocspServer : checkedServerList) {
					AsyncTask<Server, Void, State> a = ocspTester.execute(ocspServer);
					//ocspTester.doInBackground(ocspServer)
					ocspServer.setStatus(a.get());
				}
		} catch (RuntimeException e) {
			System.out.print(e.toString());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		System.out.println("--- Stopping OCSP tests <<<");
	}

	public static void performHttpTests(ArrayList<Server> checkedServerList) {
		System.out.println("--- Starting HTTP tests <<<");

		try {
				// do HTTP tests with different URLs
				for (Server httpServer : checkedServerList) {
					httpServer.setStatus(HTTPTester.checkHttpUrlPath(httpServer.getLink()));
					Log.d(TAG, "performHttpTests: " + httpServer.getStatus());
				}
		} catch (RuntimeException e) {
			System.out.println(e.toString());
		}

		System.out.println("--- Stopping HTTP tests <<<");
	}

}
