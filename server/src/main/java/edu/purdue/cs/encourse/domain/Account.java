package edu.purdue.cs.encourse.domain;

import lombok.*;

@Getter
public class Account {
    /** Primary key for all account types, stands for University ID**/
    private final String UID;

    /** More identifiable key for all account types **/
    private final String userName;

    /** Name for display purposes **/
    private String firstName;
    private String middleInit;
    private String lastName;

    public Account(@NonNull String UID, @NonNull String userName,
                   @NonNull String firstName, @NonNull String lastName,
                   String middleInit) {
        this.UID = UID;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleInit = middleInit;
    }
}
