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

    /** Services for deleting any type of account from all relevant tables **/
    int deleteAccount(@NonNull String userName);
    int deleteStudent(@NonNull Account account);
    int deleteTA(@NonNull Account account);
    int deleteProfessor(@NonNull Account account);
    int deleteAdmin(@NonNull Account account);

    /** Services for modifying any type of account in all relevant tables **/
    int modifyAccount(@NonNull String userName, @NonNull String field, String value);
    int modifyStudent(@NonNull Account account, @NonNull String field, String value);
    int modifyTA(@NonNull Account account, @NonNull String field, String value);
    int modifyProfessor(@NonNull Account account, @NonNull String field, String value);
    int modifyAdmin(@NonNull Account account, @NonNull String field, String value);
    int modifyAuthority(@NonNull String userName, String role);

    /** Services for manipulating courses and sections **/
    Section addSection(@NonNull String CRN, @NonNull String semester, @NonNull String courseID, @NonNull String courseTitle, @NonNull String sectionType);
    Section retrieveSection(@NonNull String sectionID);
    int deleteSection(@NonNull String sectionID);
    int assignProfessorToCourse(@NonNull String userName, @NonNull String courseID, @NonNull String semester);
    int registerStudentToSection(@NonNull String userName, @NonNull String sectionID);
    int hireStudentAsTeachingAssistant(@NonNull String userName);
    int assignTeachingAssistantToCourse(@NonNull String userName, @NonNull String courseID, @NonNull String semester);

    /** Services for authentication **/
    User addUser(@NonNull String userName, @NonNull String password, @NonNull String authority, boolean acc_expired, boolean locked, boolean cred_expired, boolean enabled);
    boolean hasPermissionOverAccount(@NonNull User loggedIn, @NonNull String userName);
    List<User> findAllUsers();
    List<Section> findAllSections();
}
