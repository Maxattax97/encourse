package edu.purdue.cs.encourse.domain.relations;

import lombok.*;
import javax.persistence.*;
import java.io.Serializable;

@Getter
@Entity
@Table(name = "STUDENT_PROJECT")
public class StudentProject {
    @EmbeddedId StudentProjectId id;

    /** Current grade output by testall for the project **/
    private String currentGrade;

    public StudentProject(@NonNull String userID, @NonNull String projectIdentifier, String currentGrade) {
        this.id = new StudentProjectId(userID, projectIdentifier);
        this.currentGrade = currentGrade;
    }
}

@Getter
@Embeddable
class StudentProjectId implements Serializable {
    /** Key used to identify the student working on the project **/
    private String userID;

    /** Key used to identify the project student is working on **/
    private String projectIdentifier;

    public StudentProjectId(@NonNull String userID, @NonNull String projectIdentifier) {
        this.userID = userID;
        this.projectIdentifier = projectIdentifier;
    }
}