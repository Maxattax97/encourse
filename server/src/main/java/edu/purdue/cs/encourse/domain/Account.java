package edu.purdue.cs.encourse.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "ACCOUNT")
@JsonDeserialize(using = AccountDeserializer.class)
public class Account {
    /** Primary key for all account types, meant to be university ID**/
    @Id
    private String userID;

    /** More identifiable key for all account types **/
    private String userName;

    /** Name for display purposes **/
    @Setter
    private String firstName;
    @Setter
    private String middleInit;
    @Setter
    private String lastName;

    /** Email settings **/
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

    public Account() {
    }

    public Account(String userID, String userName,
                   String firstName, String lastName,
                   int role, String middleInit, String eduEmail) {
        this.userID = userID;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.middleInit = middleInit;
        this.eduEmail = eduEmail;
    }

}
