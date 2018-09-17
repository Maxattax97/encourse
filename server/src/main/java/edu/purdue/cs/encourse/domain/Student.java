package edu.purdue.cs.encourse.domain;

import lombok.*;
import java.util.*;

@Getter
public class Student extends Account {

    public Student(@NonNull String UID, @NonNull String userName,
                     @NonNull String firstName, @NonNull String lastName,
                     String middleInit, String eduEmail) {
        super(UID, userName, firstName, lastName, Roles.STUDENT, middleInit, eduEmail);
        if(this.getClass().toString().equals("TeachingAssistant")) {
            this.setRole(Roles.TA);
        }
    }
}
