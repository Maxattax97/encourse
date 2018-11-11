package edu.purdue.cs.encourse.domain.relations;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Represents a relation between a student and the state of their project on a certain date.
 * Primarily used for keeping track of test scores over time.
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Getter
@Entity
@Table(name = "STUDENT_PROJECT_DATE")
public class StudentProjectDate {
    /** Primary key for relation in database. Never used directly */
    @EmbeddedId
    StudentProjectDateID id;

    /** Best visible grade output by testall on the given date */
    @Setter
    private double dateVisibleGrade;

    /** Best hidden grade output by testall on the given date */
    @Setter
    private double dateHiddenGrade;

    public StudentProjectDate(String userID, String projectIdentifier, String date, double dateVisibleGrade, double dateHiddenGrade) {
        this.id = new StudentProjectDateID(userID, projectIdentifier, date);
        this.dateVisibleGrade = dateVisibleGrade;
        this.dateHiddenGrade = dateHiddenGrade;
    }

    public StudentProjectDate() {

    }

    public String getStudentID() {
        return id.getStudentID();
    }

    public String getProjectIdentifier() {
        return id.getProjectIdentifier();
    }

    public String getDate() {
        return id.getDate();
    }
}

@Getter
@Embeddable
class StudentProjectDateID implements Serializable {
    /** Key used to identify the student */
    private String studentID;

    /** Key used to identify the project */
    private String projectIdentifier;

    /** Key used to identify the date */
    private String date;

    public StudentProjectDateID(String userID, String projectIdentifier, String date) {
        this.studentID = userID;
        this.projectIdentifier = projectIdentifier;
        this.date = date;
    }

    public StudentProjectDateID() {

    }
}