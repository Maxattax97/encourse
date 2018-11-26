package edu.purdue.cs.encourse.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * Represents a project that has been created by a professor.
 * Primarily used for identifying relations involving a project, and storing general information about project.
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Getter
@Entity
@Table(name = "PROJECT")
public class Project {
    /** courseID + semester + projectName, forms the primary key */
    @Id
    private String projectIdentifier;

    /** Each piece of the project identifier */
    private String courseID;
    private String semester;
    private String projectName;

    /** Date that project is opened to students */
    @Setter
    private String startDate;

    /** Deadline mainly stored for display purposes, hence it is two Strings and not a LocalDateTime */
    @Setter
    private String dueDate;

    /** The name of the directory that will store the project. Remove .git if this is included */
    private String repoName;

    /** Rate in which projects will be pulled and tested, in hourse */
    @Setter
    private int testRate;

    /** Current count towards pulling and testing */
    @Setter
    private int testCount;

    /** Listing of suites for the project, separated by commas */
    @Setter
    private String suites;

    /** True if a pull is currently happening for the project, false otherwise */
    @Setter
    private boolean syncing;

    /** True if tests are currently being run for the project, false otherwise */
    @Setter
    private boolean testing;

    /** True if academic dishonesty analysis if currently running for the project, false otherwise */
    @Setter
    private boolean analyzing;

    /** Date project was last synced */
    @Setter
    private String syncDate;

    /** Date project was last tested */
    @Setter
    private String testDate;

    /** Date project last had academic dishonesty analysis ran */
    @Setter
    private String analyzeDate;

    /** Time project was last synced */
    @Setter
    private String syncTime;

    /** Time project was last tested */
    @Setter
    private String testTime;

    /** Time project last had academic dishonesty analysis ran */
    @Setter
    private String analyzeTime;

    /** Current progress for the active operation (sync, test, or analysis) */
    @Setter
    private double operationProgress;

    /** Estimated run time for the active operation (sync, test, or analysis) */
    @Setter
    private long operationTime;

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
        this.suites = "testall";
        this.syncing = false;
        this.testing = false;
        this.analyzing = false;
        this.operationProgress = 0.0;
        this.operationTime = 0;
    }

    public Project() {

    }

    /**
     * Used to set the name of the .git file to clone for every student.
     *
     * @param repoName Name of the .git file on remote repository
     */
    public void setRepoName(String repoName) {
        if(repoName.indexOf('.') >= 0) {
            this.repoName = repoName.substring(0, repoName.indexOf('.'));
        }
        else {
            this.repoName = repoName;
        }
    }

    public void addSuite(String suiteName) {
        suites += "," + suiteName;
    }

    public boolean hasSuite(String suiteName) {
        String[] suiteArray = suites.split(",");
        for(String s : suiteArray) {
            if(s.equals(suiteName)) {
                return true;
            }
        }
        return false;
    }

    public long getEstimatedTimeRemaining() {
        if(operationProgress == 0.0) {
            return 0;
        }
        return Math.round((operationTime / operationProgress) * (1.0 - operationProgress));
    }
}
