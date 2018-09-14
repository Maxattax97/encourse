package edu.purdue.cs.encourse.domain;

import lombok.*;

@Getter
public class Section {
    /** CRN + semester form the primary key **/
    private final String CRN;
    private final String semester;

    /** Use courseName to group together sections of the same course **/
    private String courseID;
    private String courseTitle;

    /** Can parse section type from sectionID, ie LE, Lab, PSO **/
    private final String sectionID;

    /** Path to the directory that contains repositories **/
    @Setter
    private String courseHub;

    /** The path to the remote repositories on data.cs **/
    private String remotePath;

    public Section(@NonNull String CRN, @NonNull String semester,
                   @NonNull String courseID, @NonNull String courseTitle,
                   @NonNull String sectionID) {
        this.CRN = CRN;
        this.semester = semester;
        this.courseID = courseID;
        this.courseTitle = courseTitle;
        this.sectionID = sectionID;
    }


}
