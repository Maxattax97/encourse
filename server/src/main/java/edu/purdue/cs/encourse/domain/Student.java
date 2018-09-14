package edu.purdue.cs.encourse.domain;

import lombok.*;

@Getter
public class Student extends Account {
    public Student(@NonNull String UID, @NonNull String userName,
                        @NonNull String firstName, @NonNull String lastName,
                        String middleInit) {
        super(UID, userName, firstName, lastName, middleInit);
    }
}
