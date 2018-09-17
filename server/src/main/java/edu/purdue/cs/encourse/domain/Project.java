package edu.purdue.cs.encourse.domain;

import lombok.*;
import javax.persistence.*;

@Getter
@Entity
@Table(name = "PROJECTS")
public class Project {
    /** courseID + semester + projectName, forms the primary key **/
    @Id
    private String projectIdentifier;

    /** Each piece of the project identifier **/
    private String courseID;
    private final String semester;
    private String projectName;

    /** The name of the directory that will store the project. Remove .git if this is included **/
    @Setter
    private String repoName;

    public Project(@NonNull String courseID, @NonNull String semester, @NonNull String projectName, String repoName) {
        this.courseID = courseID;
        this.semester = semester;
        this.projectName = projectName;
        if(repoName.indexOf('.') >= 0) {
            this.repoName = repoName.substring(0, repoName.indexOf('.'));
        }
        this.projectIdentifier = createSectionID(courseID, semester, projectName);
    }

    public String createSectionID(@NonNull String courseID, @NonNull String semester, @NonNull String projectName) {
        return (courseID + " " + semester + ": " + projectName);
    }

}
