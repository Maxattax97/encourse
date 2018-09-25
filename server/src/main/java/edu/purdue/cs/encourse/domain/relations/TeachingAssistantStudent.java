package edu.purdue.cs.encourse.domain.relations;

import lombok.*;
import javax.persistence.*;
import java.io.Serializable;

@Getter
@Entity
@Table(name = "TEACHING_ASSISTANT_STUDENT")
public class TeachingAssistantStudent {
    @EmbeddedId
    TeachingAssistantStudentID id;

    public TeachingAssistantStudent(String teachingAssistantID, String studentID) {
        this.id = new TeachingAssistantStudentID(teachingAssistantID, studentID);
    }

    public TeachingAssistantStudent() {

    }

    public String getTeachingAssistantID() {
        return id.getTeachingAssistantID();
    }

    public String getStudentID() {
        return id.getStudentID();
    }
}

@Getter
@Embeddable
class TeachingAssistantStudentID implements Serializable {
    /** Key used to identify the TA **/
    private String teachingAssistantID;

    /** Key used to identify the student **/
    private String studentID;

    public TeachingAssistantStudentID(String teachingAssistantID, String studentID) {
        this.teachingAssistantID = teachingAssistantID;
        this.studentID = studentID;
    }

    public TeachingAssistantStudentID() {

    }
}