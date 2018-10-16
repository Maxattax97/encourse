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
@Table(name = "STUDENT_PROJECT_DATE")
public class StudentProjectDate {
    @EmbeddedId
    StudentProjectDateID id;

    /** Best visible grade output by testall on the given date **/
    @Setter
    private double dateVisibleGrade;

    /** Best hidden grade output by testall on the given date **/
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
    /** Key used to identify the student working on the project **/
    private String studentID;

    /** Key used to identify the project student is working on **/
    private String projectIdentifier;

    /** Key used to identify the day that grade was given **/
    private String date;

    public StudentProjectDateID(String userID, String projectIdentifier, String date) {
        this.studentID = userID;
        this.projectIdentifier = projectIdentifier;
        this.date = date;
    }

    public StudentProjectDateID() {

    }
}