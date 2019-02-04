package edu.purdue.cs.encourse.domain.relations;

import edu.purdue.cs.encourse.domain.Project;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a relation between a student and the state of their project on a certain date.
 * Primarily used for keeping track of test scores over time.
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Getter
@Setter
@Entity
@Table(name = "STUDENT_PROJECT_DATE")
@NoArgsConstructor
@AllArgsConstructor
public class StudentProjectDate {
    
    /** Primary key for relation in database. Never used directly */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NonNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "projectID")
    private Project project;
    
    @NonNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "studentID")
    private StudentProject studentProject;
    
    @NonNull
    private LocalDate date;
    
    /** Best visible grade output by testall on the given date */
    @NonNull
    private Double visiblePoints;

    /** Best hidden grade output by testall on the given date */
    @NonNull
    private Double hiddenPoints;
    
    @NonNull
    @ElementCollection
    @CollectionTable(name = "STUDENT_PROJECT_DATE_TESTS", joinColumns = @JoinColumn(name = "id"))
    private List<Long> testsPassing;
    
    @NonNull
    private Double totalCommits;
    
    @NonNull
    private Double currentCommits;
    
    @NonNull
    private Double totalMinutes;
    
    @NonNull
    private Double currentMinutes;
    
    @NonNull
    private Double totalAdditions;
    
    @NonNull
    private Double currentAdditions;
    
    @NonNull
    private Double totalDeletions;
    
    @NonNull
    private Double currentDeletions;
    
    public StudentProjectDate(@NonNull Project project, @NonNull StudentProject studentProject, @NonNull LocalDate date) {
        this.project = project;
        this.studentProject = studentProject;
        
        this.date = date;
        
        this.visiblePoints = 0.0;
        this.hiddenPoints = 0.0;
        
        this.testsPassing = new ArrayList<>();
        
        this.totalCommits = 0.0;
        this.currentCommits = 0.0;
        this.totalMinutes = 0.0;
        this.currentMinutes = 0.0;
        this.totalAdditions = 0.0;
        this.currentAdditions = 0.0;
        this.totalDeletions = 0.0;
        this.currentDeletions = 0.0;
    }
}

