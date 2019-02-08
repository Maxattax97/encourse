package edu.purdue.cs.encourse.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.purdue.cs.encourse.domain.relations.StudentProject;
import edu.purdue.cs.encourse.model.CourseProjectModel;
import edu.purdue.cs.encourse.model.ProjectModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a project that has been created by a professor.
 * Primarily used for identifying relations involving a project, and storing general information about project.
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Getter
@Setter
@Entity
@Table(name = "PROJECT")
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    
    /** courseID + semester + projectName, forms the primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name  = "PROJECT_ID")
    private Long projectID;

    @ManyToOne
    @JoinColumn(name = "COURSE_ID")
    @NonNull
    private Course course;
    
    @NonNull
    @Column(name  = "PROJECT_NAME")
    private String name;
    
    @NonNull
    @Column(name  = "START_DATE")
    private LocalDate startDate;
    
    @NonNull
    @Column(name  = "DUE_DATE")
    private LocalDate dueDate;

    /** The name of the directory that will store the project. Remove .git if this is included */
    @NonNull
    @Column(name  = "REPOSITORY")
    private String repository;

    /** Rate in which projects will be pulled and tested, in hourse */
    @Column(name  = "TEST_RATE")
    private int testRate;
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TestScript> testScripts;
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TestSuite> testSuites;
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ProjectDate> dates;
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<StudentProject> studentProjects;
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<AdditionHash> additionHashes;
    
    @Column(name  = "ANALYZE_DATE_TIME")
    private LocalDate analyzeDateTime;
    
    @Column(name  = "TOTAL_VISIBLE_POINTS")
    private Double totalVisiblePoints;
    
    @Column(name = "TOTAL_HIDDEN_POINTS")
    private Double totalHiddenPoints;
    
    @Column(name = "RUN_TESTALL")
    private Boolean runTestall;

    public Project(@NonNull Course course, @NonNull ProjectModel projectModel) {
        this.course = course;
        
        this.name = projectModel.getName();
    
        this.startDate = projectModel.getStartDate();
        this.dueDate = projectModel.getDueDate();
    
        this.setRepository(projectModel.getRepository());
    
        this.testRate = 6;
        
        this.testScripts = new ArrayList<>();
        this.testSuites = new ArrayList<>();
        this.dates = new ArrayList<>();
        this.studentProjects = new ArrayList<>();
        this.additionHashes = new HashSet<>();
        
        this.analyzeDateTime = LocalDate.ofYearDay(2000, 1);
        this.totalVisiblePoints = 0.0;
        this.totalHiddenPoints = 0.0;
        
        this.runTestall = projectModel.getRunTestall();
    }

    /**
     * Used to set the name of the .git file to clone for every student.
     *
     * @param repoName Name of the .git file on remote repository
     */
    public void setRepository(String repoName) {
        if(repoName.indexOf('.') >= 0)
            this.repository = repoName.substring(0, repoName.indexOf('.'));
        else
            this.repository = repoName;
    }
    
    public Long getCourseID() { return course.getCourseID(); }
    
    public String getCourseName() { return course.getName(); }
    
    public String toString() {
        return "Project (id=" + this.projectID + ", start=" + this.startDate + ", due=" + this.dueDate + ", repo=" + this.repository + ")";
    }
}
