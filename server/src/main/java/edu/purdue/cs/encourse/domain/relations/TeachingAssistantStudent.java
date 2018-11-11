package edu.purdue.cs.encourse.domain.relations;

import lombok.Getter;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Represents a relation between a teaching assistant and a student that they oversee.
 * Primarily used for providing information on all students a TA is responsible for in a particular course.
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Getter
@Entity
@Table(name = "TEACHING_ASSISTANT_STUDENT")
public class TeachingAssistantStudent {
    /** Primary key for relation in database. Never used directly */
    @EmbeddedId
    TeachingAssistantStudentID id;

    public TeachingAssistantStudent(String teachingAssistantID, String studentID, String courseID) {
        this.id = new TeachingAssistantStudentID(teachingAssistantID, studentID, courseID);
    }

    public TeachingAssistantStudent() {

    }

    public String getTeachingAssistantID() {
        return id.getTeachingAssistantID();
    }

    public String getStudentID() {
        return id.getStudentID();
    }

    public String getCourseID() { return id.getCourseID(); }
}

@Getter
@Embeddable
class TeachingAssistantStudentID implements Serializable {
    /** Key used to identify the TA */
    private String teachingAssistantID;

    /** Key used to identify the student */
    private String studentID;

    /** Key used to identify the course */
    private String courseID;

    public TeachingAssistantStudentID(String teachingAssistantID, String studentID, String courseID) {
        this.teachingAssistantID = teachingAssistantID;
        this.studentID = studentID;
        this.courseID = courseID;
    }

    public TeachingAssistantStudentID() {

    }
}