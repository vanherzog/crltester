package crltester.test.http;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import crltester.test.DateStatus;
import crltester.test.State;
import crltester.test.TestStatus;

/**
 * Class that abstracts HTTP CRL testing results.
 *
 * @author Benjamin Sanno @ IBM
 * @author Marc Hoersken @ IBM
 * @version $Revision: 1.2 2014-10-18$
 */
public class HTTPCrlState extends State {

	public HTTPCrlState() {
		super();
	}

	protected HTTPCrlState(String distinguishedName, TestStatus testStatus) {
		super(distinguishedName, testStatus);
	}

	protected HTTPCrlState(String distinguishedName, Date thisUpdate, Date nextUpdate, DateStatus dateState) {
		super(distinguishedName, thisUpdate, nextUpdate, dateState);
	}

	protected HTTPCrlState(String name, Date thisUpdate, Date nextUpdate, DateStatus dateState, Set<TestStatus> testStates) {
		super(name, thisUpdate, nextUpdate, dateState, testStates);
	}

}
