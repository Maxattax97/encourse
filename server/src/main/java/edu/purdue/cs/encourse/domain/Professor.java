package edu.purdue.cs.encourse.domain;

import lombok.*;
import javax.persistence.*;

@Getter
@Entity
@Table(name = "PROFESSOR")
public class Professor extends Account {
    public Professor(String userID, String userName, String saltPass, String firstName, String lastName,
                        String middleInit, String eduEmail) {
        super(userID, userName, saltPass, firstName, lastName, Roles.PROFESSOR, middleInit, eduEmail);
    }

    public Professor() {
        super();
    }
}
