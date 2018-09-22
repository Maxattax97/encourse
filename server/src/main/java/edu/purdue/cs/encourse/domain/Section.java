package edu.purdue.cs.encourse.domain;

import lombok.*;
import java.util.*;
import javax.persistence.*;

@Getter
@Entity
@Table(name = "SECTION")
public class Section {
    /** courseID + semester + sectionType form the primary key **/
    @Id
    private String sectionIdentifier;

    /** Each piece of the section identifier **/
    private String courseID;
    private String semester;
    private String sectionType;

    /** May be useful to track for registration purposes **/
    private String CRN;

    /** Use courseID or courseTitle to group together sections of the same course **/
    private String courseTitle;

    /** Path to the directory that contains repositories **/
    @Setter
    private String courseHub;

    /** The path to the remote repositories on data.cs **/
    private String remotePath;

    public Section(String CRN, String semester, String courseID, String courseTitle, String sectionType) {
        this.CRN = CRN;
        this.semester = semester;
        this.courseID = courseID;
        this.courseTitle = courseTitle;
        this.sectionType = sectionType;
        this.sectionIdentifier = createSectionID(courseID, semester, sectionType);
    }

    public Section() {

    }

    public String createSectionID(@NonNull String courseID, @NonNull String semester, @NonNull String sectionType) {
        return (courseID + " "  + semester + ": " + sectionType);
    }


}
