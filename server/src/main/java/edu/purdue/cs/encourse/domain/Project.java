package edu.purdue.cs.encourse.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Getter
@Entity
@Table(name = "PROJECT")
public class Project {
    /** courseID + semester + projectName, forms the primary key **/
    @Id
    private String projectIdentifier;

    /** Each piece of the project identifier **/
    private String courseID;
    private String semester;
    private String projectName;

    /** Date that project is opened to students **/
    @Setter
    private String startDate;

    /** Deadline mainly stored for display purposes, hence it is two Strings and not a LocalDateTime **/
    @Setter
    private String dueDate;

    /** The name of the directory that will store the project. Remove .git if this is included **/
    private String repoName;

    /** Rate in which projects will be pulled and tested, in hourse **/
    @Setter
    private int testRate;

    /** Current count towards pulling and testing **/
    @Setter
    private int testCount;

    /** Date project was last synced **/
    @Setter
    private String syncDate;

    /** Date project was last tested **/
    @Setter
    private String testDate;

    public Project(String courseID, String semester, String projectName,
                   String repoName, String startDate, String dueDate, int testRate) {
        this.courseID = courseID;
        this.semester = semester;
        this.projectName = projectName;
        this.setRepoName(repoName);
        this.projectIdentifier = UUID.randomUUID().toString();
        this.startDate = startDate;
        this.dueDate = dueDate;
        if(testRate > 0) {
            this.testRate = testRate;
            this.testCount = testRate - 1;
        }
        else {
            this.testRate = 0;
            this.testCount = 0;
        }
    }

    public Project() {

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
