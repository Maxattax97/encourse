package edu.purdue.cs.encourse.domain;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * Represents a section that has been added by an administrator.
 * Primarily used for identifying relations involving a section, and storing general information about a section.
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Getter
@Entity
@Table(name = "SECTION")
public class Section {
    /** Randomly generated String */
    @Id
    private String sectionIdentifier;

    /** Each piece of the section identifier */
    private String courseID;

    /** Format: FallYYYY, SpringYYYY, or SummerYYYY */
    private String semester;

    /** Examples: LE1, LE2, Lab6, PSO9 */
    private String sectionType;

    /** May be useful to track for registration purposes */
    private String CRN;

    /** Use courseID or courseTitle to group together sections of the same course */
    private String courseTitle;

    /** Time that section meets during the week, Format is "D H:MM - H:MM", where D is M, T, W, R, or F */
    private String timeSlot;

    /**
     * Path to the directory that contains repositories
     * Format of course hub : "/sourcecontrol/{COURSEID}/{SEMESTER}
     */
    @Setter
    private String courseHub;

    /** The path to the remote repositories on data.cs **/
    @Setter
    private String remotePath;

    public Section(String CRN, String semester, String courseID, String courseTitle, String sectionType, String timeSlot) {
        this.sectionIdentifier = UUID.randomUUID().toString();
        this.CRN = CRN;
        this.semester = semester;
        this.courseID = courseID;
        this.courseTitle = courseTitle;
        this.sectionType = sectionType;
        this.timeSlot = timeSlot;
    }

    public Section() {

    }
}
