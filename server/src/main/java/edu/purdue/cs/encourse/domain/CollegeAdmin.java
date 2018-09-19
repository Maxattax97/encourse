package edu.purdue.cs.encourse.domain;

import lombok.*;
import javax.persistence.*;

@Getter
@Entity
@Table(name = "ADMINISTRATOR")
public class CollegeAdmin extends Account {
    public CollegeAdmin(@NonNull String UID, @NonNull String userName,
                     @NonNull String firstName, @NonNull String lastName,
                     String middleInit, String eduEmail) {
        super(UID, userName, firstName, lastName, Roles.ADMIN, middleInit, eduEmail);
    }

    public CollegeAdmin(@NonNull Account account) {
        super(account.getUserID(), account.getUserName(), account.getFirstName(),
                account.getLastName(), Roles.ADMIN, account.getMiddleInit(), account.getEduEmail());
    }

    public CollegeAdmin() {
    }
}
