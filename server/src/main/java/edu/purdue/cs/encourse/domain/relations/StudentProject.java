package edu.purdue.cs.encourse.domain.relations;

import lombok.Getter;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Entity
@Table(name = "STUDENT_PROJECT")
public class StudentProject {
    @EmbeddedId
    StudentProjectID id;

    /** Current grade output by testall for the project **/
    private String currentGrade;

    public StudentProject(String userID, String projectIdentifier, String currentGrade) {
        this.id = new StudentProjectID(userID, projectIdentifier);
        this.currentGrade = currentGrade;
    }

    public StudentProject() {

    }

    public String getStudentID() {
        return id.getStudentID();
    }

    public String getProjectIdentifier() {
        return id.getProjectIdentifier();
    }
}

@Getter
@Embeddable
class StudentProjectID implements Serializable {
    /** Key used to identify the student working on the project **/
    private String studentID;

    /** Key used to identify the project student is working on **/
    private String projectIdentifier;

    public StudentProjectID(String userID, String projectIdentifier) {
        this.studentID = userID;
        this.projectIdentifier = projectIdentifier;
    }

    public StudentProjectID() {

    }
}