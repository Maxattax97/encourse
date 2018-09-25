package edu.purdue.cs.encourse.domain;

import lombok.*;
import java.util.*;
import javax.persistence.*;

@Getter
@Entity
@Table(name = "STUDENT")
public class Student extends Account {

    public Student(String userID, String userName, String saltPass, String firstName, String lastName,
                     String middleInit, String eduEmail) {
        super(userID, userName, saltPass, firstName, lastName, Roles.STUDENT, middleInit, eduEmail);
        if(this.getClass().toString().equals("TeachingAssistant")) {
            this.setRole(Roles.TA);
        }
    }

    public Student(String userID, String userName, String saltPass, String firstName, String lastName,
                   String middleInit, String eduEmail, boolean isTA) {
        super(userID, userName, saltPass, firstName, lastName, Roles.STUDENT, middleInit, eduEmail);
        if(isTA) {
            this.setRole(Roles.TA);
        }
    }

    public Student(Account account) {
        super(account.getUserID(), account.getUserName(), account.getSaltPass(), account.getFirstName(),
                account.getLastName(), Roles.STUDENT, account.getMiddleInit(), account.getEduEmail());
    }

    public Student(Account account, boolean isTA) {
        super(account.getUserID(), account.getUserName(), account.getSaltPass(), account.getFirstName(),
                account.getLastName(), Roles.STUDENT, account.getMiddleInit(), account.getEduEmail());
        if(isTA) {
            this.setRole(Roles.TA);
        }
    }

    public Student() {
        super();
    }
}
