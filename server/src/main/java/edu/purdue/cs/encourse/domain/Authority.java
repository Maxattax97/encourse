package edu.purdue.cs.encourse.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

/**
 * Represents an authorization level for an account.
 * Primarily used in authentication and privilege checking.
 *
 * @author Shawn Montgomery
 * @author montgo38@purdue.edu
 */
@Entity
@Table(name = "AUTHORITY", uniqueConstraints = {@UniqueConstraint(columnNames = {"NAME"})})
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Authority implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Override
    public String getAuthority() {
        return getName();
    }
}
