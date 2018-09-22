package edu.purdue.cs.encourse.domain.relations;

import lombok.*;
import javax.persistence.*;
import java.io.Serializable;

@Getter
@Entity
@Table(name = "PROFESSOR_COURSE")
public class ProfessorCourse {
    @EmbeddedId ProfessorCourseId id;

    public ProfessorCourse(String userID, String courseID, String semester) {
        this.id = new ProfessorCourseId(userID, courseID, semester);
    }

    public ProfessorCourse() {

    }
}

@Getter
@Embeddable
class ProfessorCourseId implements Serializable {
    /** Key for the professor's account **/
    private String userID;

    /** Name and semester of course being taught by professor **/
    private String courseID;
    private String semester;

    public ProfessorCourseId(String userID, String courseID, String semester) {
        this.userID = userID;
        this.courseID = courseID;
        this.semester = semester;
    }

    public ProfessorCourseId() {

    }
}