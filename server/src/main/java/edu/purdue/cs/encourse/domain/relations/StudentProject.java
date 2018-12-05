package edu.purdue.cs.encourse.domain.relations;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Represents a relation between a student and the projects that they were assigned.
 * Primarily used to cache small data points such as grades and commit count for quick access by front-end.
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Getter
@Entity
@Table(name = "STUDENT_PROJECT")
public class StudentProject {
    /** Primary key for relation in database. Never used directly */
    @EmbeddedId
    private StudentProjectID id;

    /** Best visible grade output by testall for the project */
    @Setter
    private double bestVisibleGrade;

    /** Best hidden grade output by testall for the project */
    @Setter
    private double bestHiddenGrade;

    /** Best visible point score for the project */
    @Setter
    private double bestVisiblePoints;

    /** Best hidden point score for the project */
    @Setter
    private double bestHiddenPoints;

    /** Maximum points possible for visible grade */
    @Setter
    private double visiblePointTotal;

    /** Maximum points possible for hidden grade*/
    @Setter
    private double hiddenPointTotal;

    /** Git commit count for the project */
    @Setter
    private int commitCount;

    /** Total lines added in commits across all files */
    @Setter
    private int totalLinesAdded;

    /** Total lines removed in commits across all files */
    @Setter
    private int totalLinesRemoved;

    /** Date that student made first commit for the project */
    @Setter
    private String firstCommitDate;

    /** Date that student most recently committed for the project */
    @Setter
    private String mostRecentCommitDate;

    /** Time spend in hours on the project */
    @Setter
    private double totalTimeSpent;

    public StudentProject(String userID, String projectID, String suite) {
        this.id = new StudentProjectID(userID, projectID, suite);
        this.bestVisibleGrade = 0.0;
        this.bestHiddenGrade = 0.0;
        this.bestVisiblePoints = 0.0;
        this.bestHiddenPoints = 0.0;
        this.visiblePointTotal = 0.0;
        this.hiddenPointTotal = 0.0;
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

    public String getProjectID() {
        return id.getProjectID();
    }

    public String getSuite() {
        return id.getSuite();
    }
}

@Getter
@Embeddable
class StudentProjectID implements Serializable {
    /** Key used to identify the student */
    private String studentID;

    /** Key used to identify the project */
    private String projectID;

    /** Key used to identify the suite grade is for */
    private String suite;

    public StudentProjectID(String userID, String projectID, String suite) {
        this.studentID = userID;
        this.projectID = projectID;
        this.suite = suite;
    }

    public StudentProjectID() {

    }
}