package edu.purdue.cs.encourse.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
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
@Entity
@Table(name = "ACCOUNT")
public class Account {
    /** Primary key for all account types in the database */
    @Id
    private String userID;

    /** Identifier used by the frontend for an account */
    private String userName;

    /** Name for display purposes */
    @Setter
    private String firstName;
    @Setter
    private String middleInit;
    @Setter
    private String lastName;

    /** Email settings */
    @Setter
    private String eduEmail;

    /** Indicates whether account is student, TA, professor, or college admin */
    @Setter
    private int role;

    /** Integers representing all possible account roles */
    public static class Roles {
        public static final int STUDENT = 0;
        public static final int TA = 1;
        public static final int PROFESSOR = 2;
        public static final int ADMIN = 3;
    }

    /** Strings representing all possible account roles */
    public static class Role_Names {
        public static final String STUDENT = "STUDENT";
        public static final String TA = "TA";
        public static final String PROFESSOR = "PROFESSOR";
        public static final String ADMIN = "ADMIN";
    }

    public Account(String userID, String userName, String firstName, String lastName,
                   int role, String middleInit, String eduEmail) {
        this.userID = userID;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.middleInit = middleInit;
        this.eduEmail = eduEmail;
    }

    public Account() {

    }

    /**
     * Transfers information from one account to another that does not need to be a key.
     * Primarily used to convert from a generic account to a role specific account.
     *
     * @param account Account that information is being copied from
     */
    public void copyAccount(Account account) {
        setFirstName(account.getFirstName());
        setLastName(account.getLastName());
        setMiddleInit(account.getMiddleInit());
        setEduEmail(account.getEduEmail());
    }
}
