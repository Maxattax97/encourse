package edu.purdue.cs.encourse.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "ACCOUNT")
public class Account {
    /** Primary key for all account types, meant to be university ID**/
    @Id
    private String userID;

    /** More identifiable key for all account types **/
    private String userName;

    /** Salted password for the account **/
    @Setter
    private String saltPass;

    /** Name for display purposes **/
    @Setter
    private String firstName;
    @Setter
    private String middleInit;
    @Setter
    private String lastName;

    /** Email settings **/
    @Setter
    private String eduEmail;

    /** Whether account is student, TA, professor, or college admin **/
    @Setter
    private int role;

    public static class Roles {
        public static final int STUDENT = 0;
        public static final int TA = 1;
        public static final int PROFESSOR = 2;
        public static final int ADMIN = 3;
    }

    public Account(String userID, String userName, String saltPass, String firstName, String lastName,
                   int role, String middleInit, String eduEmail) {
        this.userID = userID;
        this.userName = userName;
        this.saltPass = saltPass;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.middleInit = middleInit;
        this.eduEmail = eduEmail;
    }

    public Account() {

    }

    public void copyAccount(Account account) {
        setSaltPass(account.getSaltPass());
        setFirstName(account.getFirstName());
        setLastName(account.getLastName());
        setMiddleInit(account.getMiddleInit());
        setEduEmail(account.getEduEmail());
    }
}
