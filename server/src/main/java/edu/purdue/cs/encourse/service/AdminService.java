package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.*;
import lombok.*;

import java.util.*;

public interface AdminService {
    /** Services for adding any type of account to all relevant tables **/
    int addAccount(String userID, String userName, String saltPass, String firstName, String lastName,
                   String type, String middleInit, String eduEmail);
    int addStudent(String userID, String userName, String saltPass, String firstName, String lastName, String middleInit, String eduEmail);
    int addTA(String userID, String userName, String saltPass, String firstName, String lastName, String middleInit, String eduEmail);
    int addProfessor(String userID, String userName, String saltPass, String firstName, String lastName, String middleInit, String eduEmail);
    int addAdmin(String userID, String userName, String saltPass, String firstName, String lastName, String middleInit, String eduEmail);

    /** Services for modifying any type of account in all relevant tables **/
    int modifyAccount(String userID, String field, String value);
    int modifyStudent(Account account, String field, String value);
    int modifyTA(Account account, String field, String value);
    int modifyProfessor(Account account, String field, String value);
    int modifyAdmin(Account account, String field, String value);
}
