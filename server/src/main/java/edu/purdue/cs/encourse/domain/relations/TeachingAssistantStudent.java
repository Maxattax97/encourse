package edu.purdue.cs.encourse.domain.relations;

import lombok.*;
import javax.persistence.*;
import java.io.Serializable;

@Getter
@Entity
@Table(name = "TEACHING_ASSISTANT_STUDENT")
public class TeachingAssistantStudent {
    @EmbeddedId TeachingAssistantStudentId id;

    public TeachingAssistantStudent(String teachingAssistantID, String studentID, String sectionIdentifier) {
        this.id = new TeachingAssistantStudentId(teachingAssistantID,studentID,sectionIdentifier);
    }

    public TeachingAssistantStudent() {

    }
}

@Getter
@Embeddable
class TeachingAssistantStudentId implements Serializable {
    /** Key used to identify the TA **/
    private String teachingAssistantID;

    /** Key used to identify the student **/
    private String studentID;

    /** Key used to identify section that student is in **/
    private String sectionIdentifier;

    public TeachingAssistantStudentId(String teachingAssistantID, String studentID, String sectionIdentifier) {
        this.teachingAssistantID = teachingAssistantID;
        this.studentID = studentID;
        this.sectionIdentifier = sectionIdentifier;
    }

    public TeachingAssistantStudentId() {

    }
}
