package edu.purdue.cs.encourse.domain.relations;

import lombok.*;
import javax.persistence.*;
import java.io.Serializable;

@Getter
@Entity
@Table(name = "PROFESSOR_COURSE")
public class ProfessorCourse {
    @EmbeddedId
    ProfessorCourseID id;

    public ProfessorCourse(String userID, String courseID, String semester) {
        this.id = new ProfessorCourseID(userID, courseID, semester);
    }

    public ProfessorCourse() {

    }

    public String getProfessorID() {
        return id.getProfessorID();
    }

    public String getCourseID() {
        return id.getCourseID();
    }

    public String getSemester() {
        return id.getSemester();
    }
}

@Getter
@Embeddable
class ProfessorCourseID implements Serializable {
    /** Key for the professor's account **/
    private String professorID;

    /** Name and semester of course being taught by professor **/
    private String courseID;
    private String semester;

    public ProfessorCourseID(String userID, String courseID, String semester) {
        this.professorID = userID;
        this.courseID = courseID;
        this.semester = semester;
    }

    public ProfessorCourseID() {

    }
}