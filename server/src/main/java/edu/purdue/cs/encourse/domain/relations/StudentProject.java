package edu.purdue.cs.encourse.domain.relations;

import edu.purdue.cs.encourse.domain.Commit;
import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a relation between a student and the projects that they were assigned.
 * Primarily used to cache small data points such as grades and commit count for quick access by front-end.
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Getter
@Setter
@Entity
@Table(name = "STUDENT_PROJECT")
@AllArgsConstructor
@ToString(exclude = {"project", "dates"})
public class StudentProject {
    /** Primary key for relation in database. Never used directly */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STUDENT_PROJECT_ID")
    private Long id;
    
    @NonNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PROJECT_ID")
    private Project project;
    
    @NonNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "COURSE_STUDENT_ID")
    private CourseStudent student;

    /** Date that student made first commit for the project */
    @Column(name = "FIRST_COMMIT")
    private LocalDateTime firstCommit;

    /** Date that student most recently committed for the project */
    @Column(name = "MOST_RECENT_COMMIT")
    private LocalDateTime mostRecentCommit;
    
    @Column(name = "LAST_UPDATED_COMMIT")
    private String lastUpdatedCommit;
    
    /** Best visible grade output by testall on the given date */
    @NonNull
    @Column(name = "VISIBLE_POINTS")
    private Double visiblePoints;
    
    /** Best hidden grade output by testall on the given date */
    @NonNull
    @Column(name = "HIDDEN_POINTS")
    private Double hiddenPoints;
    
    @NonNull
    @Column(name = "COMMIT_COUNT")
    private Double commitCount;
    
    @NonNull
    @Column(name = "ADDITIONS")
    private Double additions;
    
    @NonNull
    @Column(name = "DELETIONS")
    private Double deletions;
    
    @NonNull
    @Column(name = "MINUTES")
    private Double minutes;
    
    @NonNull
    @Column(name = "CHANGES")
    private Double changes;
    
    @NonNull
    @Column(name = "TIME_VELOCITY")
    private Double timeVelocity;
    
    @NonNull
    @Column(name = "COMMIT_VELOCITY")
    private Double commitVelocity;
    
    @NonNull
    @Column(name = "COUNT_SIMILARITY")
    private Integer countSimilarity;
    
    @NonNull
    @Column(name = "PERCENT_SIMILARITY")
    private Double percentSimilarity;
    
    @NonNull
    @ElementCollection
    @CollectionTable(name = "STUDENT_PROJECT_COMMITS", joinColumns = @JoinColumn(name = "STUDENT_PROJECT_ID"))
    private List<Commit> commits;
    
    @NonNull
    @OneToMany(mappedBy = "studentProject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StudentProjectDate> dates;
    
    @NonNull
    @ElementCollection
    @CollectionTable(name = "STUDENT_PROJECT_TESTS", joinColumns = @JoinColumn(name = "STUDENT_PROJECT_ID"))
    private List<Long> testsPassing;
    
    @NonNull
    @OneToMany(mappedBy = "studentProject1", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<StudentComparison> firstComparisons;
    
    @NonNull
    @OneToMany(mappedBy = "studentProject2", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<StudentComparison> secondComparisons;
    
    public StudentProject(@NonNull Project project, @NonNull CourseStudent student) {
        this.project = project;
        this.student = student;
        
        this.firstCommit = this.mostRecentCommit = LocalDateTime.of(LocalDate.ofYearDay(2000, 1), LocalTime.of(0, 0));
        this.lastUpdatedCommit = "";
        
        this.visiblePoints = 0.0;
        this.hiddenPoints = 0.0;
        this.commitCount = 0.0;
        this.additions = 0.0;
        this.deletions = 0.0;
        this.minutes = 0.0;
        this.changes = 0.0;
        this.timeVelocity = 0.0;
        this.commitVelocity = 0.0;
        this.countSimilarity = 0;
        this.percentSimilarity = 0.0;
        
        this.commits = new ArrayList<>();
        this.dates = new ArrayList<>();
        this.testsPassing = new ArrayList<>();
        
        this.firstComparisons = new HashSet<>();
        this.secondComparisons = new HashSet<>();
    }
    
    public StudentProject() {
        this.project = null;
        this.student = null;
        
        this.firstCommit = this.mostRecentCommit = LocalDateTime.of(LocalDate.ofYearDay(2000, 1), LocalTime.of(0, 0));
    
        this.visiblePoints = 0.0;
        this.hiddenPoints = 0.0;
        this.commitCount = 0.0;
        this.additions = 0.0;
        this.deletions = 0.0;
        this.minutes = 0.0;
        this.changes = 0.0;
        this.timeVelocity = 0.0;
        this.commitVelocity = 0.0;
        this.countSimilarity = 0;
        this.percentSimilarity = 0.0;
        
        this.commits = new ArrayList<>();
        this.dates = new ArrayList<>();
        this.testsPassing = new ArrayList<>();
        
        this.firstComparisons = new HashSet<>();
        this.secondComparisons = new HashSet<>();
    }
    
    @Override
    public int hashCode() {
        return Math.toIntExact(this.id);
    }
    
    @Override
    public boolean equals(Object studentProject) {
        return studentProject instanceof StudentProject && ((StudentProject) studentProject).id.equals(this.id);
    }
    
}

