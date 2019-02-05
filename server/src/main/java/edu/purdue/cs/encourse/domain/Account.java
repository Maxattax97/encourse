package edu.purdue.cs.encourse.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.purdue.cs.encourse.model.AccountModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents a user's account for the application.
 * Primarily used for identifying relations involving an account
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Getter
@Setter
@Entity
@Table(name = "ACCOUNT")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Account {
    
    public enum Role {
        STUDENT("STUDENT"), PROFESSOR("PROFESSOR"), ADMIN("ADMIN");
        
        final String name;
        
        Role(String name) {
            this.name = name;
        }
    }
    
    /** Primary key for all account types in the database */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userID;
    
    @NonNull
    private String username;
    
    /** Name for display purposes */
    @NonNull
    private String firstName;
    
    @NonNull
    private String lastName;

    /** Email settings */
    @NonNull
    private String eduEmail;

    /** Indicates whether account is student, TA, professor, or college admin */
    @Enumerated
    @Column(columnDefinition = "smallint")
    @NonNull
    private Role role;
    
    public Account(@NonNull AccountModel model) {
        if(model.getUserID() != null)
            this.userID = model.getUserID();
        
        this.username = model.getUsername();
        this.firstName = model.getFirstName();
        this.lastName = model.getLastName();
        this.eduEmail = model.getEduEmail();
        this.role = model.getRole() == 0 ? Role.STUDENT : model.getRole() == 1 ? Role.PROFESSOR : Role.ADMIN;
    }
}
