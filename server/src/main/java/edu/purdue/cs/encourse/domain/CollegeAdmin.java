package edu.purdue.cs.encourse.domain;

import lombok.*;

@Getter
public class CollegeAdmin extends Account {
    public CollegeAdmin(@NonNull String UID, @NonNull String userName,
                     @NonNull String firstName, @NonNull String lastName,
                     String middleInit, String eduEmail) {
        super(UID, userName, firstName, lastName, Roles.ADMIN, middleInit, eduEmail);
    }
}
