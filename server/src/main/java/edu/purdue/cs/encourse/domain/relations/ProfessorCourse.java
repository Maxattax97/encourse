package edu.purdue.cs.encourse.domain.relations;

import lombok.Getter;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Represents a relation between a particular professor and a course that they teach.
 * Relation primarily used for retrieving a professor's courses when they log in.
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Getter
@Entity
@Table(name = "PROFESSOR_COURSE")
public class ProfessorCourse {
    /** Primary key for relation in database. Never used directly */
    @EmbeddedId
    ProfessorCourseID id;

    public ProfessorCourse(String professorID, String courseID, String semester) {
        this.id = new ProfessorCourseID(professorID, courseID, semester);
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

    @Override
    public String toString() { return getProfessorID() + " | " + getSemester() + " | " + getCourseID(); }
}

@Getter
@Embeddable
class ProfessorCourseID implements Serializable {
    /** Key for the professor */
    private String professorID;

    /** Keys used to identify the course and semester */
    private String courseID;
    private String semester;

    public ProfessorCourseID(String professorID, String courseID, String semester) {
        this.professorID = professorID;
        this.courseID = courseID;
        this.semester = semester;
    }

    public ProfessorCourseID() {

    }
}