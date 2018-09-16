package edu.purdue.cs.encourse.domain;

import lombok.*;
import java.util.*;
import javax.persistence.*;

@Getter
@Entity
@Table(name = "SECTIONS")
public class Section {
    /** courseID + semester + sectionType form the primary key **/
    @Id
    private final String sectionIdentifier;

    /** Each piece of the section identifier **/
    private String courseID;
    private final String semester;
    private final String sectionType;

    /** May be useful to track for registration purposes **/
    private final String CRN;

    /** Use courseID or courseTitle to group together sections of the same course **/
    private String courseTitle;

    /** UID of head TA or professor for the section **/
    private String headInstructorUID;

    /** Path to the directory that contains repositories **/
    @Setter
    private String courseHub;

    /** The path to the remote repositories on data.cs **/
    private String remotePath;

    public Section(@NonNull String CRN, @NonNull String semester,
                   @NonNull String courseID, @NonNull String courseTitle,
                   @NonNull String sectionType) {
        this.CRN = CRN;
        this.semester = semester;
        this.courseID = courseID;
        this.courseTitle = courseTitle;
        this.sectionType = sectionType;
        this.sectionIdentifier = createSectionID(courseID, semester, sectionType);
    }

    public String createSectionID(@NonNull String courseID, @NonNull String semester, @NonNull String sectionType) {
        return (courseID + " "  + semester + ": " + sectionType);
    }


}
