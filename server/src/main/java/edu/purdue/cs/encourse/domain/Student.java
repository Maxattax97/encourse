package edu.purdue.cs.encourse.domain;

import lombok.*;
import java.util.*;

@Getter
public class Student extends Account {
    /** Courses that the Student is taking **/
    private List<String> studentSectionCRNs;

    /** Active projects for the student and testall grade**/
    private Map<String, String> currentProjects;

    /** Projects that are no longer active **/
    private Map<String, String> oldProjects;

    public Student(@NonNull String UID, @NonNull String userName,
                     @NonNull String firstName, @NonNull String lastName,
                     String middleInit, String eduEmail) {
        super(UID, userName, firstName, lastName, Roles.STUDENT, middleInit, eduEmail);
        if(this.getClass().toString().equals("TeachingAssistant")) {
            this.setRole(Roles.TA);
        }
    }
}
