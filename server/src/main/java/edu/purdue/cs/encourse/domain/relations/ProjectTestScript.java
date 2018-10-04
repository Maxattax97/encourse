package edu.purdue.cs.encourse.domain.relations;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Entity
@Table(name = "PROJECT_TEST_SCRIPT")
public class ProjectTestScript {
    @EmbeddedId
    ProjectTestScriptID id;

    /** Indicates if students are able to see the result of this test script **/
    @Setter
    boolean isHidden;

    /** Number of points earned for passing the test script **/
    @Setter
    int pointsWorth;

    public ProjectTestScript(String projectIdentifier, String testScriptName, boolean isHidden, int pointsWorth) {
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
    /** Key used to identify the project test scripts are for **/
    private String projectIdentifier;

    /** Key used to identify which test script this is **/
    private String testScriptName;

    public ProjectTestScriptID(String projectIdentifier, String testScriptName) {
        this.projectIdentifier = projectIdentifier;
        this.testScriptName = testScriptName;
    }

    public ProjectTestScriptID() {

    }
}
