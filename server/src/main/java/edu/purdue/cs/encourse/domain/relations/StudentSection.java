package edu.purdue.cs.encourse.domain.relations;

import lombok.Getter;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Represents a relation between a student and a section that they are currently or have been enrolled in.
 * Primarily used for grouping students by section and finding all students taking a particular course.
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Getter
@Entity
@Table(name = "STUDENT_SECTION")
public class StudentSection {
    /** Primary key for relation in database. Never used directly */
    @EmbeddedId
    private StudentSectionID id;

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
    /** Key used to identify the student */
    private String studentID;

    /** Key used to identify the section */
    private String sectionIdentifier;

    public StudentSectionID(String studentID, String sectionIdentifier) {
        this.studentID = studentID;
        this.sectionIdentifier = sectionIdentifier;
    }

    public StudentSectionID() {

    }
}