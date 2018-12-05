package edu.purdue.cs.encourse.domain.relations;

import lombok.Getter;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Represents a relation between a teaching assistant and a course that they work for.
 * Primarily used for showing a professor all of the TAs that they can assign for a course.
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Getter
@Entity
@Table(name = "TEACHING_ASSISTANT_COURSE")
public class TeachingAssistantCourse {
    /** Primary key for relation in database. Never used directly */
    @EmbeddedId
    private TeachingAssistantCourseID id;

    public TeachingAssistantCourse(String teachingAssistantID, String courseID, String semester) {
        this.id = new TeachingAssistantCourseID(teachingAssistantID, courseID, semester);
    }

    public TeachingAssistantCourse() {

    }

    public String getTeachingAssistantID() {
        return id.getTeachingAssistantID();
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
class TeachingAssistantCourseID implements Serializable {
    /** Key used to identify the TA */
    private String teachingAssistantID;

    /** Keys used to identify the course and semester */
    private String courseID;
    private String semester;

    public TeachingAssistantCourseID(String teachingAssistantID, String courseID, String semester) {
        this.teachingAssistantID = teachingAssistantID;
        this.courseID = courseID;
        this.semester = semester;
    }

    public TeachingAssistantCourseID() {

    }
}