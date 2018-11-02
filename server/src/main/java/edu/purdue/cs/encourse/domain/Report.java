package edu.purdue.cs.encourse.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
