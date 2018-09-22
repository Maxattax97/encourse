package edu.purdue.cs.encourse.domain.relations;

import lombok.*;
import javax.persistence.*;
import java.io.Serializable;

@Getter
@Entity
@Table(name = "TEACHING_ASSISTANT_STUDENT")
public class StudentAssignment {
    @EmbeddedId
    StudentAssignmentID id;

    public StudentAssignment(String teachingAssistantID, String studentID, String sectionIdentifier) {
        this.id = new StudentAssignmentID(teachingAssistantID,studentID,sectionIdentifier);
    }

    public StudentAssignment() {

    }

    public String getTeachingAssistantID() {
        return id.getTeachingAssistantID();
    }

    public String getStudentID() {
        return id.getStudentID();
    }

    public String getSectionIdentifier() {
        return id.getSectionIdentifier();
    }
}

@Getter
@Embeddable
class StudentAssignmentID implements Serializable {
    /** Key used to identify the TA **/
    private String teachingAssistantID;

    /** Key used to identify the student **/
    private String studentID;

    /** Key used to identify section that student is in **/
    private String sectionIdentifier;

    public StudentAssignmentID(String teachingAssistantID, String studentID, String sectionIdentifier) {
        this.teachingAssistantID = teachingAssistantID;
        this.studentID = studentID;
        this.sectionIdentifier = sectionIdentifier;
    }

    public StudentAssignmentID() {

    }
}