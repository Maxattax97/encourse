package edu.purdue.cs.encourse.domain.relations;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Represents a relation between a project and the test scripts that it runs.
 * Primarily used during testall to determine the grade that a student receives.
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Getter
@Entity
@Table(name = "PROJECT_TEST_SCRIPT")
public class ProjectTestScript {
    /** Primary key for relation in database. Never used directly */
    @EmbeddedId
    ProjectTestScriptID id;

    /** Indicates if students are able to see the result of this test script */
    @Setter
    private boolean isHidden;

    /** Number of points earned for passing the test script */
    @Setter
    private double pointsWorth;

    /** Listing of suites involving the test case, separated by commas */
    private String suites;

    public ProjectTestScript(String projectID, String testScriptName, boolean isHidden, double pointsWorth) {
        this.id = new ProjectTestScriptID(projectID, testScriptName);
        this.isHidden = isHidden;
        this.pointsWorth = pointsWorth;
        this.suites = "testall";
    }

    public ProjectTestScript() {

    }

    public String getProjectID() {
        return id.getProjectID();
    }

    public String getTestScriptName() {
        return id.getTestScriptName();
    }

    public void addSuite(String suiteName) {
        suites += "," + suiteName;
    }

    public boolean hasSuite(String suiteName) {
        String[] suiteArray = suites.split(",");
        for(String s : suiteArray) {
            if(s.equals(suiteName)) {
                return true;
            }
        }
        return false;
    }
}

@Getter
@Embeddable
class ProjectTestScriptID implements Serializable {
    /** Key used to identify the project */
    private String projectID;

    /** Key used to identify the test script */
    private String testScriptName;

    public ProjectTestScriptID(String projectID, String testScriptName) {
        this.projectID = projectID;
        this.testScriptName = testScriptName;
    }

    public ProjectTestScriptID() {

    }
}
