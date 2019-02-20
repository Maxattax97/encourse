package edu.purdue.cs.encourse.domain;

import edu.purdue.cs.encourse.domain.relations.StudentComparison;
import edu.purdue.cs.encourse.domain.relations.StudentProject;
import edu.purdue.cs.encourse.model.BasicStatistics;
import edu.purdue.cs.encourse.model.ProjectModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
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
    
    @Column(name  = "ANALYZE_DATE_TIME")
    private LocalDate analyzeDateTime;
    
    @Column(name  = "TOTAL_VISIBLE_POINTS")
    private Double totalVisiblePoints;
    
    @Column(name = "TOTAL_HIDDEN_POINTS")
    private Double totalHiddenPoints;
    
    @Column(name = "RUN_TESTALL")
    private Boolean runTestall;
    
    @Column(name = "VALID_SIMILARITY_STATS_COUNT")
    private Integer validSimilarityCount;
    
    @Column(name = "VALID_STATS_COUNT")
    private Integer validCount;
    
    @Setter
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "max", column = @Column(name = "MAX_SIMILARITY")),
            @AttributeOverride(name = "min", column = @Column(name = "MIN_SIMILARITY")),
            @AttributeOverride(name = "mean", column = @Column(name = "MEAN_SIMILARITY")),
            @AttributeOverride(name = "median", column = @Column(name = "MEDIAN_SIMILARITY")),
            @AttributeOverride(name = "variance", column = @Column(name = "VARIANCE_SIMILARITY"))
    })
    private BasicStatistics similarityStats;
    
    @Setter
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "max", column = @Column(name = "MAX_SIMILARITY_PERCENT")),
            @AttributeOverride(name = "min", column = @Column(name = "MIN_SIMILARITY_PERCENT")),
            @AttributeOverride(name = "mean", column = @Column(name = "MEAN_SIMILARITY_PERCENT")),
            @AttributeOverride(name = "median", column = @Column(name = "MEDIAN_SIMILARITY_PERCENT")),
            @AttributeOverride(name = "variance", column = @Column(name = "VARIANCE_SIMILARITY_PERCENT"))
    })
    private BasicStatistics similarityPercentStats;
    
    @Setter
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "max", column = @Column(name = "MAX_CHANGES")),
            @AttributeOverride(name = "min", column = @Column(name = "MIN_CHANGES")),
            @AttributeOverride(name = "mean", column = @Column(name = "MEAN_CHANGES")),
            @AttributeOverride(name = "median", column = @Column(name = "MEDIAN_CHANGES")),
            @AttributeOverride(name = "variance", column = @Column(name = "VARIANCE_CHANGES"))
    })
    private BasicStatistics changesStats;
    
    @Setter
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "max", column = @Column(name = "MAX_TIME_VELOCITY")),
            @AttributeOverride(name = "min", column = @Column(name = "MIN_TIME_VELOCITY")),
            @AttributeOverride(name = "mean", column = @Column(name = "MEAN_TIME_VELOCITY")),
            @AttributeOverride(name = "median", column = @Column(name = "MEDIAN_TIME_VELOCITY")),
            @AttributeOverride(name = "variance", column = @Column(name = "VARIANCE_TIME_VELOCITY"))
    })
    private BasicStatistics timeVelocityStats;
    
    @Setter
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "max", column = @Column(name = "MAX_COMMIT_VELOCITY")),
            @AttributeOverride(name = "min", column = @Column(name = "MIN_COMMIT_VELOCITY")),
            @AttributeOverride(name = "mean", column = @Column(name = "MEAN_COMMIT_VELOCITY")),
            @AttributeOverride(name = "median", column = @Column(name = "MEDIAN_COMMIT_VELOCITY")),
            @AttributeOverride(name = "variance", column = @Column(name = "VARIANCE_COMMIT_VELOCITY"))
    })
    private BasicStatistics commitVelocityStats;
    
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
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<StudentComparison> studentComparisons;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "PROJECT_IGNORE_GIT_USERS", joinColumns = @JoinColumn(name = "PROJECT_ID"))
    private List<String> ignoredUsers;

    public Project(@NonNull Course course, @NonNull ProjectModel projectModel) {
        this.course = course;
        
        this.name = projectModel.getName();
    
        this.startDate = projectModel.getStartDate();
        this.dueDate = projectModel.getDueDate();
    
        this.setRepository(projectModel.getRepository());
    
        this.testRate = 6;
        
        this.analyzeDateTime = LocalDate.ofYearDay(2000, 1);
        this.totalVisiblePoints = 0.0;
        this.totalHiddenPoints = 0.0;
        
        this.runTestall = projectModel.getRunTestall();
        
        this.validSimilarityCount = 0;
        this.validCount = 0;
        
        this.similarityStats = new BasicStatistics();
        this.similarityPercentStats = new BasicStatistics();
        this.changesStats = new BasicStatistics();
        this.timeVelocityStats = new BasicStatistics();
        this.commitVelocityStats = new BasicStatistics();
    
        this.testScripts = new ArrayList<>();
        this.testSuites = new ArrayList<>();
        this.dates = new ArrayList<>();
        this.studentProjects = new ArrayList<>();
        this.additionHashes = new HashSet<>();
        this.studentComparisons = new HashSet<>();
        this.ignoredUsers = new ArrayList<>();
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
