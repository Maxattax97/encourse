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
@Table(name = "STUDENT_PROJECT_TEST")
public class StudentProjectTest {
    @EmbeddedId
    StudentProjectTestID id;

    /** Indicates if students are able to see the result of this test script **/
    @Setter
    boolean isPassing;

    /** Indicates if students are able to see the result of this test script **/
    @Setter
    boolean isHidden;

    /** Number of points earned for passing the test script **/
    @Setter
    double pointsWorth;

    public StudentProjectTest(String studentID, String projectIdentifier, String testScriptName, boolean isPassing, boolean isHidden, double pointsWorth) {
        this.id = new StudentProjectTestID(studentID, projectIdentifier, testScriptName);
        this.isPassing = isPassing;
        this.isHidden = isHidden;
        this.pointsWorth = pointsWorth;
    }

    public StudentProjectTest() {

    }

    public String getStudentID() {
        return id.getStudentID();
    }


    public String getProjectIdentifier() {
        return id.getProjectIdentifier();
    }

    public String getTestScriptName() {
        return id.getTestScriptName();
    }

    public String getTestResultString() {
        if(isPassing()) {
            return getTestScriptName() + ":" + pointsWorth;
        }
        else {
            return getTestScriptName() + ":" + 0.0;
        }
    }
}

@Getter
@Embeddable
class StudentProjectTestID implements Serializable {
    /** Key used to identify the project test scripts are for **/
    private String studentID;

    /** Key used to identify the project test scripts are for **/
    private String projectIdentifier;

    /** Key used to identify which test script this is **/
    private String testScriptName;

    public StudentProjectTestID(String studentID, String projectIdentifier, String testScriptName) {
        this.studentID = studentID;
        this.projectIdentifier = projectIdentifier;
        this.testScriptName = testScriptName;
    }

    public StudentProjectTestID() {

    }
}
