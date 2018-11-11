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
    boolean isHidden;

    /** Number of points earned for passing the test script */
    @Setter
    double pointsWorth;

    public ProjectTestScript(String projectIdentifier, String testScriptName, boolean isHidden, double pointsWorth) {
        this.id = new ProjectTestScriptID(projectIdentifier, testScriptName);
        this.isHidden = isHidden;
        this.pointsWorth = pointsWorth;
    }

    public ProjectTestScript() {

    }

    public String getProjectIdentifier() {
        return id.getProjectIdentifier();
    }

    public String getTestScriptName() {
        return id.getTestScriptName();
    }
}

@Getter
@Embeddable
class ProjectTestScriptID implements Serializable {
    /** Key used to identify the project */
    private String projectIdentifier;

    /** Key used to identify the test script */
    private String testScriptName;

    public ProjectTestScriptID(String projectIdentifier, String testScriptName) {
        this.projectIdentifier = projectIdentifier;
        this.testScriptName = testScriptName;
    }

    public ProjectTestScriptID() {

    }
}
