package edu.purdue.cs.encourse.domain.relations;

import lombok.Getter;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Entity
@Table(name = "TEACHING_ASSISTANT_SECTION")
public class TeachingAssistantSection {
    @EmbeddedId
    TeachingAssistantSectionID id;

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
    /** Key for the professor's account **/
    private String teachingAssistantID;

    /** Section TA is assigned to **/
    private String sectionID;

    public TeachingAssistantSectionID(String teachingAssistantID, String sectionID) {
        this.teachingAssistantID = teachingAssistantID;
        this.sectionID = sectionID;
    }

    public TeachingAssistantSectionID() {

    }
}