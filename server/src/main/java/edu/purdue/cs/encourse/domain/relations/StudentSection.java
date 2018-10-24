package edu.purdue.cs.encourse.domain.relations;

import lombok.Getter;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Entity
@Table(name = "STUDENT_SECTION")
public class StudentSection {
    @EmbeddedId
    StudentSectionID id;

    public StudentSection(String studentID, String sectionIdentifier) {
        this.id = new StudentSectionID(studentID, sectionIdentifier);
    }

    public StudentSection() {

    }

    public String getStudentID() {
        return id.getStudentID();
    }

    public String getSectionIdentifier() {
        return id.getSectionIdentifier();
    }

    @Override
    public String toString() { return getStudentID() + " | " + getSectionIdentifier(); }
}

@Getter
@Embeddable
class StudentSectionID implements Serializable {
    /** Key used to identify the student **/
    private String studentID;

    /** Key used to identify section that student is in **/
    private String sectionIdentifier;

    public StudentSectionID(String studentID, String sectionIdentifier) {
        this.studentID = studentID;
        this.sectionIdentifier = sectionIdentifier;
    }

    public StudentSectionID() {

    }
}