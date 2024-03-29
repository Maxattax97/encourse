package edu.purdue.cs.encourse.domain;

import edu.purdue.cs.encourse.domain.relations.CourseStudent;
import edu.purdue.cs.encourse.model.SectionModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;

/**
 * Represents a section that has been added by an administrator.
 * Primarily used for identifying relations involving a section, and storing general information about a section.
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Getter
@Setter
@Entity
@Table(name = "SECTION")
@NoArgsConstructor
@AllArgsConstructor
public class Section {
    /** Randomly generated String */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SECTION_ID")
    private Long sectionID;
    
    @ManyToOne
    @JoinColumn(name = "COURSE_ID")
    @NonNull
    private Course course;
    
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name="SECTION_COURSE_STUDENTS",
            joinColumns=@JoinColumn(name="SECTION_ID"),
            inverseJoinColumns=@JoinColumn(name="USER_ID")
    )
    private List<CourseStudent> students;
    
    /** Examples: LE1, LE2, Lab6, PSO9 */
    @NonNull
    @Column(name = "TYPE")
    private String type;

    /** Time that section meets during the week, Format is "D H:MM - H:MM", where D is M, T, W, R, or F */
    @NonNull
    @Column(name = "TIME")
    private String time;
    
    public Section(@NonNull Course course, @NonNull SectionModel sectionModel) {
        this.course = course;
        
        this.type = sectionModel.getType();
        this.time = sectionModel.getTime();
    }
}
