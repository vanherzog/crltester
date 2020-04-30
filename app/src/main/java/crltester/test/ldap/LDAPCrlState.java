package crltester.test.ldap;

import java.util.Date;
import java.util.Set;

import crltester.test.DateStatus;
import crltester.test.State;
import crltester.test.TestStatus;

/**
 * Class that abstracts LDAP CRL testing results.
 *
 * @author Benjamin Sanno @ IBM
 * @author Marc Hoersken @ IBM
 * @version $Revision: 1.1 2013-04-09$
 */
public class LDAPCrlState extends State {

	protected LDAPCrlState(String distinguishedName, TestStatus testStatus) {
		super(distinguishedName, testStatus);
	}

	protected LDAPCrlState(String distinguishedName, Date thisUpdate, Date nextUpdate, DateStatus dateState) {
		super(distinguishedName, thisUpdate, nextUpdate, dateState);
	}

	protected LDAPCrlState(String name, Date thisUpdate, Date nextUpdate, DateStatus dateState, Set<TestStatus> testStates) {
		super(name, thisUpdate, nextUpdate, dateState, testStates);
	}

}
