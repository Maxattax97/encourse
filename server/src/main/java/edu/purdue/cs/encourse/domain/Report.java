package edu.purdue.cs.encourse.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents a report that was created to show evidence of academic dishonesty.
 * Primarily used for a professor to temporarily provide evidence of dishonesty to an admin using magic links.
 *
 * @author Shawn Montgomery
 * @author montgo38@purdue.edu
 */
@Getter
@Entity
@Table(name = "REPORT")
public class Report {

    @Id
    private String reportID;

    @Setter
    private String lock;

    @Setter
    private String accountID;

    @Setter
    private String expireDate;

    @Setter
    private String comments;

    public Report() {
    }
}
