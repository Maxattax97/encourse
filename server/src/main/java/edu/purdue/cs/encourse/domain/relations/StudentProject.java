package edu.purdue.cs.encourse.domain.relations;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Entity
@Table(name = "STUDENT_PROJECT")
public class StudentProject {
    @EmbeddedId
    StudentProjectID id;

    /** Best visible grade output by testall for the project **/
    @Setter
    private String bestVisibleGrade;

    /** Best hidden grade output by testall for the project **/
    @Setter
    private String bestHiddenGrade;

    /** Git commit count for the project **/
    @Setter
    private int commitCount;

    /** Total lines added in commits across all files **/
    @Setter
    private int totalLinesAdded;

    /** Total lines removed in commits across all files **/
    @Setter
    private int totalLinesRemoved;

    /** Date that student made first commit for the project **/
    @Setter
    private String firstCommitDate;

    /** Date that student most recently committed for the project **/
    @Setter
    private String mostRecentCommitDate;

    /** Time spend in hours on the project **/
    @Setter
    private double totalTimeSpent;

    public StudentProject(String userID, String projectIdentifier) {
        this.id = new StudentProjectID(userID, projectIdentifier);
        this.bestVisibleGrade = "";
        this.bestHiddenGrade = "";
        this.commitCount = 0;
        this.totalLinesAdded = 0;
        this.totalLinesRemoved = 0;
        this.firstCommitDate = null;
        this.mostRecentCommitDate = null;
        this.mostRecentCommitDate = null;
        this.totalTimeSpent = 0.0;
    }

    public StudentProject() {

    }

    public String getStudentID() {
        return id.getStudentID();
    }

    public String getProjectIdentifier() {
        return id.getProjectIdentifier();
    }

    public void updateCommitInformation(int commitCount, int totalLinesAdded, int totalLinesRemoved, String mostRecentCommitDate, double totalTimeSpent) {
        setCommitCount(commitCount);
        setTotalLinesAdded(totalLinesAdded);
        setTotalLinesRemoved(totalLinesRemoved);
        setMostRecentCommitDate(mostRecentCommitDate);
        setTotalTimeSpent(totalTimeSpent);
    }
}

@Getter
@Embeddable
class StudentProjectID implements Serializable {
    /** Key used to identify the student working on the project **/
    private String studentID;

    /** Key used to identify the project student is working on **/
    private String projectIdentifier;

    public StudentProjectID(String userID, String projectIdentifier) {
        this.studentID = userID;
        this.projectIdentifier = projectIdentifier;
    }

    public StudentProjectID() {

    }
}