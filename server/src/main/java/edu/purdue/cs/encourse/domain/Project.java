package edu.purdue.cs.encourse.domain;

import lombok.*;
import javax.persistence.*;

@Getter
@Entity
@Table(name = "PROJECT")
public class Project {
    /** courseID + semester + projectName, forms the primary key **/
    @Id
    private final String projectIdentifier;

    /** Each piece of the project identifier **/
    private final String courseID;
    private final String semester;
    private final String projectName;

    /** Date that project is opened to students **/
    @Setter
    private String startDate;

    /** Deadline mainly stored for display purposes, hence it is two Strings and not a LocalDateTime **/
    @Setter
    private String dueDate;

    /** The name of the directory that will store the project. Remove .git if this is included **/
    private String repoName;

    public Project(@NonNull String courseID, @NonNull String semester, @NonNull String projectName,
                   String repoName, String startDate, String dueDate) {
        this.courseID = courseID;
        this.semester = semester;
        this.projectName = projectName;
        this.setRepoName(repoName);
        this.projectIdentifier = createSectionID(courseID, semester, projectName);
        this.startDate = startDate;
        this.dueDate = dueDate;
    }

    public String createSectionID(@NonNull String courseID, @NonNull String semester, @NonNull String projectName) {
        return (courseID + " " + semester + ": " + projectName);
    }

    public void setRepoName(String repoName) {
        if(repoName.indexOf('.') >= 0) {
            this.repoName = repoName.substring(0, repoName.indexOf('.'));
        }
        else {
            this.repoName = repoName;
        }
    }

}
