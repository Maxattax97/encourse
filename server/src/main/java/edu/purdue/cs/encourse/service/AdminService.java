package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.*;
import lombok.NonNull;

import java.util.List;


import java.util.List;

public interface AdminService {

    /** Services for adding any type of account to all relevant tables **/
    int addAccount(@NonNull String userID, @NonNull String userName, @NonNull String firstName, @NonNull String lastName,
                   @NonNull String type, String middleInit, String eduEmail);
    int addStudent(@NonNull String userID, @NonNull String userName, @NonNull String firstName, @NonNull String lastName, String middleInit, String eduEmail);
    int addTA(@NonNull String userID, @NonNull String userName, @NonNull String firstName, @NonNull String lastName, String middleInit, String eduEmail);
    int addProfessor(@NonNull String userID, @NonNull String userName, @NonNull String firstName, @NonNull String lastName, String middleInit, String eduEmail);
    int addAdmin(@NonNull String userID, @NonNull String userName, @NonNull String firstName, @NonNull String lastName, String middleInit, String eduEmail);

    /** Services for modifying any type of account in all relevant tables **/
    int modifyAccount(@NonNull String userName, @NonNull String field, String value);
    int modifyStudent(@NonNull Account account, @NonNull String field, String value);
    int modifyTA(@NonNull Account account, @NonNull String field, String value);
    int modifyProfessor(@NonNull Account account, @NonNull String field, String value);
    int modifyAdmin(@NonNull Account account, @NonNull String field, String value);

    /** Services for manipulating courses and sections **/
    int addSection(@NonNull String CRN, @NonNull String semester, @NonNull String courseID, @NonNull String courseTitle, @NonNull String sectionType);
    int assignProfessorToCourse(@NonNull String userName, @NonNull String courseID, @NonNull String semester);
    int registerStudentToSection(@NonNull String userName, @NonNull String courseID, @NonNull String semester, @NonNull String sectionType);
    int hireStudentAsTeachingAssistant(@NonNull String userName);

    /** Services for authentication **/
    User addUser(@NonNull String userName, @NonNull String password, @NonNull String authority, boolean acc_expired, boolean locked, boolean cred_expired, boolean enabled);
    List<User> findAllUsers();
}
