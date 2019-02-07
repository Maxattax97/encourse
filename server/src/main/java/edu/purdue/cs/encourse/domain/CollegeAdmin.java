package edu.purdue.cs.encourse.domain;

import edu.purdue.cs.encourse.model.AccountModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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
public class CollegeAdmin extends Account {
    
    public CollegeAdmin(@NonNull AccountModel model) {
        super(model);
    }

}
