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
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
public class Student extends Account {
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CourseStudent> courses;

    public Student(@NonNull AccountModel model) {
        super(model);
        
        this.courses = new ArrayList<>();
    }

}
