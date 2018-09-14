package edu.purdue.cs.encourse.domain;

import lombok.*;

@Getter
public class Project {
    /** courseID + semester + projectName form the primary key **/
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
        this.repoName = repoName;
    }

}
