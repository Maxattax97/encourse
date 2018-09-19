package edu.purdue.cs.encourse.domain;

import lombok.*;
import java.util.*;
import javax.persistence.*;

@Getter
@Entity
@Table(name = "STUDENT")
public class Student extends Account {

    public Student(@NonNull String UID, @NonNull String userName,
                     @NonNull String firstName, @NonNull String lastName,
                     String middleInit, String eduEmail) {
        super(UID, userName, firstName, lastName, Roles.STUDENT, middleInit, eduEmail);
        if(this.getClass().toString().equals("TeachingAssistant")) {
            this.setRole(Roles.TA);
        }
    }

    public Student(@NonNull Account account) {
        super(account.getUserID(), account.getUserName(), account.getFirstName(),
                account.getLastName(), Roles.STUDENT, account.getMiddleInit(), account.getEduEmail());
        if(this.getClass().toString().equals("TeachingAssistant")) {
            this.setRole(Roles.TA);
        }
    }

    public Student() {
    }

}
