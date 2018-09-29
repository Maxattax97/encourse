package edu.purdue.cs.encourse.domain;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Entity
@Table(name = "STUDENT")
public class Student extends Account {

    public Student(String userID, String userName, String firstName, String lastName,
                     String middleInit, String eduEmail) {
        super(userID, userName, firstName, lastName, Roles.STUDENT, middleInit, eduEmail);
        if(this.getClass().toString().equals("TeachingAssistant")) {
            this.setRole(Roles.TA);
        }
    }

    Student(String userID, String userName, String firstName, String lastName,
            String middleInit, String eduEmail, boolean isTA) {
        super(userID, userName, firstName, lastName, Roles.STUDENT, middleInit, eduEmail);
        if(isTA) {
            this.setRole(Roles.TA);
        }
    }

    public Student() {
        super();
    }

}
