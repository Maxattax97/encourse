package edu.purdue.cs.encourse.domain;

import lombok.*;
import javax.persistence.*;

@Getter
@Entity
@Table(name = "ACCOUNTS")
public class Account {
    /** Primary key for all account types, stands for University ID**/
    @Id
    private final String UID;

    /** More identifiable key for all account types **/
    private final String userName;

    /** Name for display purposes **/
    private String firstName;
    private String middleInit;
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

    public Account(@NonNull String UID, @NonNull String userName,
                   @NonNull String firstName, @NonNull String lastName,
                   @NonNull int role, String middleInit, String eduEmail) {
        this.UID = UID;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.middleInit = middleInit;
        this.eduEmail = eduEmail;
    }
}
