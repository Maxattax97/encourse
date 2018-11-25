package edu.purdue.cs.encourse.domain.relations;

import lombok.Getter;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Represents a relation between a teaching assistant and a section that they are assigned to.
 * Primarily used to simplify assignment of students to TAs by restricting scope to sections TA is responsible for.
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Getter
@Entity
@Table(name = "TEACHING_ASSISTANT_SECTION")
public class TeachingAssistantSection {
    /** Primary key for relation in database. Never used directly */
    @EmbeddedId
    private TeachingAssistantSectionID id;

    public TeachingAssistantSection(String teachingAssistantID, String sectionID) {
        this.id = new TeachingAssistantSectionID(teachingAssistantID, sectionID);
    }

    public TeachingAssistantSection() {

    }

    public String getTeachingAssistantID() {
        return id.getTeachingAssistantID();
    }

    public String getSectionID() {
        return id.getSectionID();
    }
}

@Getter
@Embeddable
class TeachingAssistantSectionID implements Serializable {
    /** Key used to identify the TA */
    private String teachingAssistantID;

    /** Key used to identify the section*/
    private String sectionID;

    public TeachingAssistantSectionID(String teachingAssistantID, String sectionID) {
        this.teachingAssistantID = teachingAssistantID;
        this.sectionID = sectionID;
    }

    public TeachingAssistantSectionID() {

    }
}