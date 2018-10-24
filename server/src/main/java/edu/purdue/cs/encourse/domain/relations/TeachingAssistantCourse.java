package edu.purdue.cs.encourse.domain.relations;

import lombok.Getter;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Entity
@Table(name = "TEACHING_ASSISTANT_COURSE")
public class TeachingAssistantCourse {
    @EmbeddedId
    TeachingAssistantCourseID id;

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
    /** Key for the professor's account **/
    private String teachingAssistantID;

    /** Name and semester of course being taught by professor **/
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