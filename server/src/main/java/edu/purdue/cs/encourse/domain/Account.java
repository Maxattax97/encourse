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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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

    /** Indicates whether account is student, TA, professor, or college admin */
    @Enumerated
    @Column(columnDefinition = "smallint", name = "ROLE")
    @NonNull
    private Role role;
    
    public Account(@NonNull AccountModel model) {
        this.username = model.getUsername();
        this.firstName = model.getFirstName();
        this.lastName = model.getLastName();
        this.eduEmail = model.getEduEmail();
        this.role = model.getRole() == Role.STUDENT.ordinal() ? Role.STUDENT : model.getRole() == Role.PROFESSOR.ordinal() ? Role.PROFESSOR : Role.ADMIN;
    }
}
