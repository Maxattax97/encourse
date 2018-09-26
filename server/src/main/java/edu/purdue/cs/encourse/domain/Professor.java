package edu.purdue.cs.encourse.domain;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Entity
@Table(name = "PROFESSOR")
public class Professor extends Account {
    public Professor(String userID, String userName, String saltPass, String firstName, String lastName,
                        String middleInit, String eduEmail) {
        super(userID, userName, saltPass, firstName, lastName, Roles.PROFESSOR, middleInit, eduEmail);
    }

    public Professor(Account account) {
        super(account.getUserID(), account.getUserName(), account.getSaltPass(), account.getFirstName(),
                account.getLastName(), Roles.PROFESSOR, account.getMiddleInit(), account.getEduEmail());
    }

    public Professor() {
        super();
    }
}
