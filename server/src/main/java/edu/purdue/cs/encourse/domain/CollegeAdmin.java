package edu.purdue.cs.encourse.domain;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Represents an administrator's account for the application.
 * Primarily used for performing services that can only be done by administrators.
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Getter
@Entity
@Table(name = "ADMINISTRATOR")
public class CollegeAdmin extends Account {
    public CollegeAdmin(String userID, String userName, String firstName, String lastName,
                     String middleInit, String eduEmail) {
        super(userID, userName, firstName, lastName, Roles.ADMIN, middleInit, eduEmail);
    }

    public CollegeAdmin() {
        super();
    }

}
