package edu.purdue.cs.encourse.domain.relations;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Represents a relation between a student and the score they received for a particular test case.
 * Primarily used for providing a breakdown of which test cases a student is passing.
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Getter
@Entity
@Table(name = "STUDENT_PROJECT_TEST")
public class StudentProjectTest {
    /** Primary key for relation in database. Never used directly */
    @EmbeddedId
    StudentProjectTestID id;

    /** Indicates if students are able to see the result of this test script */
    @Setter
    boolean isPassing;

    /** Indicates if students are able to see the result of this test script */
    @Setter
    boolean isHidden;

    /** Number of points earned for passing the test script */
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

    /**
     * Converts test result into a string to be parsed by a Python script.
     * Primarily used to communicate test results from Java to Python.
     *
     * @return String with the format {NAME}:{P/F}:{POINT_VALUE}
     */
    public String getTestResultString() {
        if(isPassing()) {
            return getTestScriptName() + ":P:" + pointsWorth;
        }
        else {
            return getTestScriptName() + ":F:" + pointsWorth;
        }
    }
}

@Getter
@Embeddable
class StudentProjectTestID implements Serializable {
    /** Key used to identify the student */
    private String studentID;

    /** Key used to identify the project */
    private String projectIdentifier;

    /** Key used to identify the test script */
    private String testScriptName;

    public StudentProjectTestID(String studentID, String projectIdentifier, String testScriptName) {
        this.studentID = studentID;
        this.projectIdentifier = projectIdentifier;
        this.testScriptName = testScriptName;
    }

    public StudentProjectTestID() {

    }
}
