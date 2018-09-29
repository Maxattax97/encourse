package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.*;
import lombok.NonNull;

import java.util.List;

public interface AccountService {

    /** Services for retrieving account with specified type from database **/
    Account retrieveAccount(@NonNull String userName);
    Account retrieveAccountByID(@NonNull String userID);
    Student retrieveStudent(@NonNull String userName);
    TeachingAssistant retrieveTA(@NonNull String userName);
    Professor retrieveProfessor(@NonNull String userName);
    CollegeAdmin retrieveAdmin(@NonNull String userName);
    List<Account> retrieveAllAccounts();

}
