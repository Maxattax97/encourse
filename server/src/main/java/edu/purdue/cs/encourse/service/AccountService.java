package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.*;

public interface AccountService {

    /** Services for retrieving account with specified type from database **/
    Account retrieveAccount(String userName, String saltPass);
    Student retrieveStudent(String userName, String saltPass);
    TeachingAssistant retrieveTA(String userName, String saltPass);
    Professor retrieveProfessor(String userName, String saltPass);
    CollegeAdmin retrieveAdmin(String userName, String saltPass);
    Account retrieveAccountByID(String userID);

}
