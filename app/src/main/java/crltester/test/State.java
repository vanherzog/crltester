package crltester.test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class State {

    private String distinguishedName;
    private Date thisUpdate;
    private Date nextUpdate;
    private DateStatus dateState;
    private Set<TestStatus> testStates = new HashSet<TestStatus>();

    public boolean isTested() {
        return tested;
    }

    public void setTested(boolean tested) {
        this.tested = tested;
    }

    private boolean tested;

    public State() {
        setTested(false);
    }

    protected State(String distinguishedName, TestStatus testStatus) {
        this.distinguishedName = distinguishedName;
        this.testStates.add(testStatus);
        setTested(true);
    }

    protected State(String distinguishedName, Date thisUpdate, Date nextUpdate, DateStatus dateState) {
        this.distinguishedName = distinguishedName;
        this.thisUpdate = thisUpdate;
        this.nextUpdate = nextUpdate;
        this.dateState = dateState;
        setTested(true);
    }

    protected State(String name, Date thisUpdate, Date nextUpdate, DateStatus dateState, Set<TestStatus> testStates) {
        this(name, thisUpdate, nextUpdate, dateState);
        this.testStates = testStates;
        setTested(true);
    }

    /**
     * @return Distinguished name CRL field.
     */
    public String getDistinguishedName() {
        return this.distinguishedName;
    }


    /**
     * @return Common name CRL field.
     */
    public String getCommonName() {
        String commonName = this.getDistinguishedName();
        if (commonName != null) {
            String commonNameUpper = commonName.toUpperCase();
            if (commonNameUpper.length() != commonName.length()) {
                commonNameUpper = commonName;
            }
            if (commonNameUpper.contains("CN=")) {
                int start = commonNameUpper.indexOf("CN=");
                int end = commonName.indexOf(",", start);
                commonName = commonName.substring(start + 3, end).trim();
            }
        }
        return commonName;
    }

    /**
     * @return This update CRL field.
     */
    public Date getThisUpdate() {
        return this.thisUpdate;
    }

    /**
     * @return Next update CRL field.
     */
    public Date getNextUpdate() {
        return this.nextUpdate;
    }

    /**
     * @return Date state.
     */
    public DateStatus getDateState() {
        return this.dateState;
    }

    /**
     * @return Test states.
     */
    public Set<TestStatus> getTestStates() {
        return this.testStates;
    }


}
