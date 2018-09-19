package edu.purdue.cs.encourse.domain;

import lombok.*;
import javax.persistence.*;

@Getter
@Entity
@Table(name = "PROFESSOR")
public class Professor extends Account {
    public Professor(@NonNull String UID, @NonNull String userName,
                        @NonNull String firstName, @NonNull String lastName,
                        String middleInit, String eduEmail) {
        super(UID, userName, firstName, lastName, Roles.PROFESSOR, middleInit, eduEmail);
    }

    public Professor(@NonNull Account account) {
        super(account.getUserID(), account.getUserName(), account.getFirstName(),
                account.getLastName(), Roles.PROFESSOR, account.getMiddleInit(), account.getEduEmail());
    }

    public Professor(){
    }
}
