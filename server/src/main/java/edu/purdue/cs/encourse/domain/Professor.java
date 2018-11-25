package edu.purdue.cs.encourse.domain;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Represents a professor's account for the application.
 * Primarily used for performing services that can only be done by professors.
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Getter
@Entity
@Table(name = "PROFESSOR")
public class Professor extends Account {
    public Professor(String userID, String userName, String firstName, String lastName,
                        String middleInit, String eduEmail) {
        super(userID, userName, firstName, lastName, Roles.PROFESSOR, middleInit, eduEmail);
    }

    public Professor() {
        super();
    }
}
