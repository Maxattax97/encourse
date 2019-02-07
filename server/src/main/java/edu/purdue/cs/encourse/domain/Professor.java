package edu.purdue.cs.encourse.domain;

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
 * Represents a professor's account for the application.
 * Primarily used for performing services that can only be done by professors.
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Getter
@Entity
@Table(name = "PROFESSOR")
@NoArgsConstructor
@AllArgsConstructor
public class Professor extends Account {
    
    @Setter
    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Course> courses;
    
    public Professor(@NonNull AccountModel model) {
        super(model);
        
        this.courses = new ArrayList<>();
    }
}
