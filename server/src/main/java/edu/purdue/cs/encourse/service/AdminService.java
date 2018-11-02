package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.*;

import lombok.NonNull;

import java.util.List;


import java.util.List;

public interface AdminService {

    /** Services for manipulating accounts in database **/
    int addAccount(@NonNull String userID, @NonNull String userName, @NonNull String firstName, @NonNull String lastName,
                   @NonNull String type, String middleInit, String eduEmail);
    int deleteAccount(@NonNull String userName);
    int modifyAccount(@NonNull String userName, @NonNull String field, String value);
    int modifyAuthority(@NonNull String userName, String role);

    /** Services for manipulating courses and sections in database **/
    Section addSection(@NonNull String CRN, @NonNull String semester, @NonNull String courseID, @NonNull String courseTitle, @NonNull String sectionType, @NonNull String timeSlot);
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
