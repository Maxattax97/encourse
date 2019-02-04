package edu.purdue.cs.encourse.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;

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
@NoArgsConstructor
@AllArgsConstructor
public class CollegeAdmin extends Account {
    
    public CollegeAdmin(String username, String firstName, String lastName, String eduEmail) {
        super(username, firstName, lastName, eduEmail, Role.ADMIN);
    }

}
