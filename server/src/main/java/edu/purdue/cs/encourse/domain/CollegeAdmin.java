package edu.purdue.cs.encourse.domain;

import edu.purdue.cs.encourse.model.AccountModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Setter
@Entity
@Table(name = "ADMINISTRATOR")
@NoArgsConstructor
@AllArgsConstructor
public class CollegeAdmin {
    
    /** Primary key for all account types in the database */
    @Id
    @Column(name = "USER_ID")
    private Long userID;
    
    @NonNull
    @Column(name = "USERNAME")
    private String username;
    
    /** Name for display purposes */
    @NonNull
    @Column(name = "FIRST_NAME")
    private String firstName;
    
    @NonNull
    @Column(name = "LAST_NAME")
    private String lastName;
    
    /** Email settings */
    @NonNull
    @Column(name = "EDU_EMAIL")
    private String eduEmail;
    
    public CollegeAdmin(@NonNull Long id, @NonNull AccountModel model) {
        this.userID = id;
    
        this.username = model.getUsername();
        this.firstName = model.getFirstName();
        this.lastName = model.getLastName();
        this.eduEmail = model.getEduEmail();
    }

}
