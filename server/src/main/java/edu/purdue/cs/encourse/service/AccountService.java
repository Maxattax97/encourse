package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.*;
import lombok.*;

import java.util.*;

public interface AccountService {
    Account retrieveAccount(String userName);
    Student retrieveStudent(String userName);
    TeachingAssistant retrieveTA(String userName);
    Professor retrieveProfessor(String userName);
    CollegeAdmin retrieveAdmin(String userName);
    List<Account> retrieveAllAccounts();
}
