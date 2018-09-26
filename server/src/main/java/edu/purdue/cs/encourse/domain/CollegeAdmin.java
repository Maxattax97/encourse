package edu.purdue.cs.encourse.domain;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Entity
@Table(name = "ADMINISTRATOR")
public class CollegeAdmin extends Account {
    public CollegeAdmin(String userID, String userName, String saltPass, String firstName, String lastName,
                     String middleInit, String eduEmail) {
        super(userID, userName, saltPass, firstName, lastName, Roles.ADMIN, middleInit, eduEmail);
    }

    public CollegeAdmin() {
        super();
    }

}
