package edu.purdue.cs.encourse.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.purdue.cs.encourse.domain.relations.CourseStudent;
import edu.purdue.cs.encourse.domain.relations.StudentProject;
import edu.purdue.cs.encourse.model.AccountModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a student's account for the application.
 * Primarily used for performing services that can only be done by students.
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Getter
@Setter
@Entity
@Table(name = "STUDENT")
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    
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
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CourseStudent> courses;
    
    public Student(@NonNull Long id, @NonNull AccountModel model) {
        this.userID = id;
        
        this.username = model.getUsername();
        this.firstName = model.getFirstName();
        this.lastName = model.getLastName();
        this.eduEmail = model.getEduEmail();
        
        this.courses = new ArrayList<>();
    }

}
