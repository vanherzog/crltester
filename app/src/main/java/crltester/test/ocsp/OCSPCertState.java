package crltester.test.ocsp;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.bouncycastle.cert.ocsp.CertificateStatus;

import crltester.test.DateStatus;
import crltester.test.State;
import crltester.test.TestStatus;

/**
 * Class that abstracts OCSP certificate testing results.
 *
 * @author Benjamin Sanno @ IBM
 * @author Marc Hoersken @ IBM
 * @version $Revision: 1.1 2013-04-09$
 */
public class OCSPCertState extends State {
	private CertificateStatus certStatus;

	protected OCSPCertState(String distinguishedName, TestStatus testStatus) {
		super(distinguishedName, testStatus);
	}

	protected OCSPCertState(String distinguishedName, Date thisUpdate, Date nextUpdate, DateStatus dateState) {
		super(distinguishedName, thisUpdate, nextUpdate, dateState);
	}

	protected OCSPCertState(String name, Date thisUpdate, Date nextUpdate, DateStatus dateState, CertificateStatus certStatus , Set<TestStatus> testStates) {
		super(name, thisUpdate, nextUpdate, dateState, testStates);
		this.certStatus = certStatus;
	}

	/**
	 * @return Certificate state.
	 */
	public CertificateStatus getCertStatus() {
		return this.certStatus;
	}

}
