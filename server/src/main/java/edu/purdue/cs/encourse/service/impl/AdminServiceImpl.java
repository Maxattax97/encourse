package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.domain.relations.*;
import edu.purdue.cs.encourse.service.AdminService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Contains implementations for all services pertaining to administrative operations
 * Primarily used for the admin panel functionality
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Service(value = AdminServiceImpl.NAME)
public class AdminServiceImpl implements AdminService {

    public final static String NAME = "AdminService";

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeachingAssistantRepository teachingAssistantRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private ProfessorCourseRepository professorCourseRepository;

    @Autowired
    private StudentSectionRepository studentSectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private TeachingAssistantStudentRepository teachingAssistantStudentRepository;

    @Autowired
    private StudentProjectRepository studentProjectRepository;

    @Autowired
    private StudentProjectDateRepository studentProjectDateRepository;

    @Autowired
    private TeachingAssistantCourseRepository teachingAssistantCourseRepository;

    /**
     * Adds user credentials and authorities for an account to the database
     *
     * @param userName      Front-end identifier for account being added
     * @param password      Encrypted credentials for account
     * @param authority     Permission level for the account
     * @param acc_expired   Indicates if account is currently active
     * @param locked        Indicates if account can no longer log in
     * @param cred_expired  Indicates if account login token is still active
     * @param enabled       Indicates if account can log in to application
     * @return              User credentials created for account
     */
    public User addUser(@NonNull String userName, @NonNull String password, @NonNull String authority, boolean acc_expired, boolean locked, boolean cred_expired, boolean enabled) {
        Authority auth = authorityRepository.findDistinctByName(authority);
        if (auth == null) {
            auth = authorityRepository.findDistinctByName("STUDENT");
        }
        List<Authority> auths = new ArrayList<>();
        auths.add(auth);

        User user = new User();
        user.setUsername(userName);
        user.setPassword(password);
        user.setAccountExpired(acc_expired);
        user.setAccountLocked(locked);
        user.setCredentialsExpired(cred_expired);
        user.setEnabled(enabled);
        user.setAuthorities(auths);
        userRepository.save(user);
        user.setPassword(null);
        return user;
    }

    /**
     * Indicates if a user has permission to view another account
     * Students can only view their own account
     * Teaching assistants can only view students that they were assigned to
     * Professors can view any student in courses that they are assigned to
     * Administrators can view any account
     *
     * @param loggedIn  Credentials for user trying to view another account
     * @param userName  Front-end identifier for account being accessed
     * @return          True if user can view account, false otherwise
     */
    public boolean hasPermissionOverAccount(User loggedIn, String userName) {
        Account account = accountRepository.findByUserName(userName);
        if (account == null) {
            return false;
        }
        Account sentRequest = accountRepository.findByUserName(loggedIn.getUsername());
        if (sentRequest == null) {
            return false;
        }

        boolean hasAuth = false;
        Iterator<Authority> auths = loggedIn.getAuthorities().iterator();
        while (auths.hasNext()) {
            String au = auths.next().getAuthority();
            switch (au) {
                case Account.Role_Names.STUDENT:
                    if (sentRequest.getUserName().contentEquals(userName)) {
                        hasAuth = true;
                        break;
                    }
                    break;
                case Account.Role_Names.TA:
                    List<TeachingAssistantStudent> teachingAssistantStudents = teachingAssistantStudentRepository.findByIdTeachingAssistantID(sentRequest.getUserID());
                    for (TeachingAssistantStudent tas: teachingAssistantStudents) {
                        if (tas.getStudentID().contentEquals(account.getUserID())) {
                            hasAuth = true;
                            break;
                        }
                    }
                    break;
                case Account.Role_Names.PROFESSOR:

                    List<StudentSection> studentSections = studentSectionRepository.findByIdStudentID(account.getUserID());
                    List<ProfessorCourse> professorCourses = professorCourseRepository.findByIdProfessorID(sentRequest.getUserID());

                    for (StudentSection ss: studentSections) {
                        Section section = sectionRepository.findBySectionID(ss.getSectionID());
                        for (ProfessorCourse pc: professorCourses) {
                            if (pc.getCourseID().contentEquals(section.getCourseID()) && pc.getSemester().contentEquals(section.getSemester())) {
                                hasAuth = true;
                                break;
                            }
                        }
                    }
                    break;
                case Account.Role_Names.ADMIN:
                    hasAuth = true;
                    break;
            }
        }
        if (!hasAuth) {
            System.out.println("User " + loggedIn.getUsername() + " does not have authority over " + userName);
        }
        return hasAuth;
    }

    /**
     * Replaces an account with a new account with different permissions
     *
     * @param userName  Front-end identifier for account being modified
     * @param role      Permissions for the new account
     * @return          Error code
     */
    public int modifyAuthority(@NonNull String userName, String role) {
        Account a = accountRepository.findByUserName(userName);
        if (a == null) {
            return -1;
        }
        deleteAccount(userName);
        int result = addAccount(a.getUserID(), a.getUserName(), a.getFirstName(), a.getLastName(), role, a.getMiddleInit(), a.getEduEmail());

        User user = userRepository.findByUsername(userName);
        if (user == null) {
            return -3;
        }
        List<Authority> auths = new ArrayList<>();
        Authority auth = authorityRepository.findDistinctByName(role);
        if (auth == null) {
            return -4;
        }
        auths.add(auth);
        user.setAuthorities(auths);
        userRepository.save(user);
        return result;
    }

    /**
     * Adds an account to the database, calling several private methods based on account type
     *
     * @param userID        Front-end identifier for account being added
     * @param userName      Attribute for account being added
     * @param firstName     Attribute for account being added
     * @param lastName      Attribute for account being added
     * @param type          Subclass and role of account being added
     * @param middleInit    (Optional) Attribute for account being added
     * @param eduEmail      (Optional) Email used for messaging the user
     * @return              Error code
     */
    public int addAccount(@NonNull String userID, @NonNull String userName, @NonNull String firstName, @NonNull String lastName,
                          @NonNull String type, String middleInit, String eduEmail) {
        int result;
        switch(type) {
            case Account.Role_Names.STUDENT: result = addStudent(userID, userName, firstName, lastName, middleInit, eduEmail); break;
            case Account.Role_Names.TA: result = addTA(userID, userName, firstName, lastName, middleInit, eduEmail); break;
            case Account.Role_Names.PROFESSOR: result = addProfessor(userID, userName, firstName, lastName, middleInit, eduEmail); break;
            case Account.Role_Names.ADMIN: result = addAdmin(userID, userName, firstName, lastName, middleInit, eduEmail); break;
            default: result = -1;
        }
        return result;
    }

    /**
     * Adds a student account to the database
     * Not called directly by endpoints
     *
     * @param userID        Front-end identifier for account being added
     * @param userName      Attribute for account being added
     * @param firstName     Attribute for account being added
     * @param lastName      Attribute for account being added
     * @param middleInit    (Optional) Attribute for account being added
     * @param eduEmail      (Optional) Email used for messaging the user
     * @return              Error code
     */
    private int addStudent(@NonNull String userID, @NonNull String userName, @NonNull String firstName, @NonNull String lastName, String middleInit, String eduEmail) {
        Student student = new Student(userID, userName, firstName, lastName, middleInit, eduEmail);
        if(accountRepository.existsByUserID(student.getUserID())) {
            return -2;
        }
        if(accountRepository.existsByUserName(student.getUserID())) {
            return -3;
        }
        if(accountRepository.save(student) == null) {
            return -4;
        }
        return 0;
    }

    /**
     * Adds a teaching assistant account to the database
     * Not called directly by endpoints
     *
     * @param userID        Front-end identifier for account being added
     * @param userName      Attribute for account being added
     * @param firstName     Attribute for account being added
     * @param lastName      Attribute for account being added
     * @param middleInit    (Optional) Attribute for account being added
     * @param eduEmail      (Optional) Email used for messaging the user
     * @return              Error code
     */
    private int addTA(@NonNull String userID, @NonNull String userName, @NonNull String firstName, @NonNull String lastName, String middleInit, String eduEmail) {
        TeachingAssistant teachingAssistant = new TeachingAssistant(userID, userName, firstName, lastName, middleInit, eduEmail);
        if(accountRepository.existsByUserID(teachingAssistant.getUserID())) {
            return -5;
        }
        if(accountRepository.existsByUserName(teachingAssistant.getUserID())) {
            return -6;
        }
        if(accountRepository.save(teachingAssistant) == null) {
            return -7;
        }
        return 0;
    }

    /**
     * Adds a professor account to the database
     * Not called directly by endpoints
     *
     * @param userID        Front-end identifier for account being added
     * @param userName      Attribute for account being added
     * @param firstName     Attribute for account being added
     * @param lastName      Attribute for account being added
     * @param middleInit    (Optional) Attribute for account being added
     * @param eduEmail      (Optional) Email used for messaging the user
     * @return              Error code
     */
    private int addProfessor(@NonNull String userID, @NonNull String userName, @NonNull String firstName, @NonNull String lastName, String middleInit, String eduEmail) {
        Professor professor = new Professor(userID, userName, firstName, lastName, middleInit, eduEmail);
        if(accountRepository.existsByUserID(professor.getUserID())) {
            return -8;
        }
        if(accountRepository.existsByUserName(professor.getUserID())) {
            return -9;
        }
        if(accountRepository.save(professor) == null) {
            return -10;
        }
        return 0;
    }

    /**
     * Adds an administrator account to the database
     * Not called directly by endpoints
     *
     * @param userID        Front-end identifier for account being added
     * @param userName      Attribute for account being added
     * @param firstName     Attribute for account being added
     * @param lastName      Attribute for account being added
     * @param middleInit    (Optional) Attribute for account being added
     * @param eduEmail      (Optional) Email used for messaging the user
     * @return              Error code
     */
    private int addAdmin(@NonNull String userID, @NonNull String userName, @NonNull String firstName, @NonNull String lastName, String middleInit, String eduEmail) {
        CollegeAdmin admin = new CollegeAdmin(userID, userName, firstName, lastName, middleInit, eduEmail);
        if(accountRepository.existsByUserID(admin.getUserID())) {
            return -11;
        }
        if(accountRepository.existsByUserName(admin.getUserID())) {
            return -12;
        }
        if(accountRepository.save(admin) == null) {
            return -13;
        }
        return 0;
    }

    /**
     * Removes an account from database and all relations associated with it
     *
     * @param userName  Front-end identifier for account being deleted
     * @return          Error code
     */
    public int deleteAccount(@NonNull String userName) {
        Account account = accountRepository.findByUserName(userName);
        if(account == null) {
            return -1;
        }
        int result;
        switch(account.getRole()) {
            case Account.Roles.STUDENT: result = deleteStudent(account); break;
            case Account.Roles.TA: result = deleteTA(account); break;
            case Account.Roles.PROFESSOR: result = deleteProfessor(account); break;
            case Account.Roles.ADMIN: result = deleteAdmin(account); break;
            default: result = -2;

        }
        return result;
    }

    /**
     * Removes a student account from database and all relations associated with it
     * Not called directly by endpoints
     *
     * @param account   Account to remove from the database
     * @return          Error code
     */
    private int deleteStudent(@NonNull Account account){
        Student student = studentRepository.findByUserID(account.getUserID());
        student.copyAccount(account);
        List<StudentProject> projects = studentProjectRepository.findByIdStudentID(student.getUserID());
        for(StudentProject p : projects) {
            studentProjectRepository.delete(p);
        }
        List<StudentProjectDate> projectDates = studentProjectDateRepository.findByIdStudentID(student.getUserID());
        for(StudentProjectDate p : projectDates) {
            studentProjectDateRepository.delete(p);
        }
        List<StudentSection> sections = studentSectionRepository.findByIdStudentID(student.getUserID());
        for(StudentSection s : sections) {
            studentSectionRepository.delete(s);
        }
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdStudentID(student.getUserID());
        for(TeachingAssistantStudent a : assignments) {
            teachingAssistantStudentRepository.delete(a);
        }
        accountRepository.delete(student);
        return 0;
    }

    /**
     * Removes a teaching assistant account from database and all relations associated with it
     * Not called directly by endpoints
     *
     * @param account   Account to remove from the database
     * @return          Error code
     */
    private int deleteTA(@NonNull Account account) {
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserID(account.getUserID());
        teachingAssistant.copyAccount(account);
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantID(teachingAssistant.getUserID());
        for(TeachingAssistantStudent a : assignments) {
            teachingAssistantStudentRepository.delete(a);
        }
        teachingAssistantRepository.delete(teachingAssistant);
        return 0;
    }

    /**
     * Removes a professor account from database and all relations associated with it
     * Not called directly by endpoints
     *
     * @param account   Account to remove from the database
     * @return          Error code
     */
    private int deleteProfessor(@NonNull Account account) {
        Professor professor = professorRepository.findByUserID(account.getUserID());
        professor.copyAccount(account);
        List<ProfessorCourse> assignments = professorCourseRepository.findByIdProfessorID(professor.getUserID());
        for(ProfessorCourse a : assignments) {
            professorCourseRepository.delete(a);
        }
        professorRepository.delete(professor);
        return 0;
    }

    /**
     * Removes an administrator account from database and all relations associated with it
     * Not called directly by endpoints
     *
     * @param account   Account to remove from the database
     * @return          Error code
     */
    private int deleteAdmin(@NonNull Account account) {
        CollegeAdmin admin = adminRepository.findByUserID(account.getUserID());
        admin.copyAccount(account);
        adminRepository.delete(admin);
        return 0;
    }

    /**
     * Changes valid fields for an account
     * Some fields, like userID, are not allowed to be changed
     *
     * @param userName  Front-end identifier for the account being modified
     * @param field     Attribute to change for the account
     * @param value     (Optional) New value to insert into field. Type parsed based on specified field
     * @return          Error code
     */
    public int modifyAccount(@NonNull String userName, @NonNull String field, String value) {
        Account account = accountRepository.findByUserName(userName);
        if(account == null) {
            return -1;
        }
        switch(field) {
            case "firstName": account.setFirstName(value); break;
            case "middleInitial": account.setMiddleInit(value); break;
            case "lastName": account.setLastName(value); break;
            case "eduEmail": account.setEduEmail(value); break;
            default: break;
        }

        int result;
        switch(account.getRole()) {
            case Account.Roles.STUDENT: result = modifyStudent(account, field, value); break;
            case Account.Roles.TA: result = modifyTA(account, field, value); break;
            case Account.Roles.PROFESSOR: result = modifyProfessor(account, field, value); break;
            case Account.Roles.ADMIN: result = modifyAdmin(account, field, value); break;
            default: result = -2;

        }
        return result;
    }

    /**
     * Changes valid fields for a student account
     * Some fields, like userID, are not allowed to be changed
     * Not called directly by endpoints
     *
     * @param account   Account to modify
     * @param field     Attribute to change for the account
     * @param value     (Optional) New value to insert into field. Type parsed based on specified field
     * @return          Error code
     */
    private int modifyStudent(@NonNull Account account, @NonNull String field, String value){
        Student student = studentRepository.findByUserID(account.getUserID());
        student.copyAccount(account);
        if(accountRepository.save(student) == null) {
            return -4;
        }
        return 0;
    }

    /**
     * Changes valid fields for a teaching assistant account
     * Some fields, like userID, are not allowed to be changed
     * Not called directly by endpoints
     *
     * @param account   Account to modify
     * @param field     Attribute to change for the account
     * @param value     (Optional) New value to insert into field. Type parsed based on specified field
     * @return          Error code
     */
    private int modifyTA(@NonNull Account account, @NonNull String field, String value) {
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserID(account.getUserID());
        teachingAssistant.copyAccount(account);
        if(accountRepository.save(teachingAssistant) == null) {
            return -6;
        }
        return 0;
    }

    /**
     * Changes valid fields for a professor account
     * Some fields, like userID, are not allowed to be changed
     * Not called directly by endpoints
     *
     * @param account   Account to modify
     * @param field     Attribute to change for the account
     * @param value     (Optional) New value to insert into field. Type parsed based on specified field
     * @return          Error code
     */
    private int modifyProfessor(@NonNull Account account, @NonNull String field, String value) {
        Professor professor = professorRepository.findByUserID(account.getUserID());
        professor.copyAccount(account);
        if(accountRepository.save(professor) == null) {
            return -9;
        }
        return 0;
    }

    /**
     * Changes valid fields for an administrator account
     * Some fields, like userID, are not allowed to be changed
     * Not called directly by endpoints
     *
     * @param account   Account to modify
     * @param field     Attribute to change for the account
     * @param value     (Optional) New value to insert into field. Type parsed based on specified field
     * @return          Error code
     */
    private int modifyAdmin(@NonNull Account account, @NonNull String field, String value) {
        CollegeAdmin admin = adminRepository.findByUserID(account.getUserID());
        admin.copyAccount(account);
        if(accountRepository.save(admin) == null) {
            return -11;
        }
        return 0;
    }

    /**
     * Adds a new course section to the database
     *
     * @param CRN           Course Registration Number for section being added
     * @param semester      Semester that course is offered
     * @param courseID      Identifier used by the university for the course that section is for
     * @param courseTitle   Name of the course that section is for
     * @param sectionType   Type of section being added
     * @param timeSlot      Time and days that course is offered
     * @return              Section that was added to the database
     */
    public Section addSection(@NonNull String CRN, @NonNull String semester, @NonNull String courseID, @NonNull String courseTitle, @NonNull String sectionType, @NonNull String timeSlot) {
        System.out.println("ADDING SECTION");
        Section section = new Section(CRN, semester, courseID, courseTitle, sectionType, timeSlot);
        if(sectionRepository.existsBySectionID(section.getSectionID())) {
            return null;
        }
        return sectionRepository.save(section);
    }

    /**
     * Obtains a course section from the database
     *
     * @param sectionID Identifier for a section
     * @return          Section corresponding to identifier
     */
    public Section retrieveSection(@NonNull String sectionID) {
        return sectionRepository.findBySectionID(sectionID);
    }

    /**
     * Removes a section from database and all relations associated with it
     *
     * @param sectionID Identifier for the section being removed
     * @return          Error code
     */
    public int deleteSection(@NonNull String sectionID) {
        System.out.println("ADDING SECTION");
        Section section = sectionRepository.findBySectionID(sectionID);
        if(section == null) {
            return -1;
        }
        List<StudentSection> sections = studentSectionRepository.findByIdSectionID(sectionID);
        for(StudentSection s : sections) {
            studentSectionRepository.delete(s);
        }
        sectionRepository.delete(section);
        return 0;
    }

    /**
     * Adds a relation between a professor and a courseID with semester
     * Professor can then access data for the specified course from the specified semester
     *
     * @param userName  Front-end identifier for professor being assigned
     * @param courseID  Identifier used by the university for a course
     * @param semester  Semester that assignment is effective during
     * @return          Error code
     */
    public int assignProfessorToCourse(@NonNull String userName, @NonNull String courseID, @NonNull String semester) {
        Professor professor = professorRepository.findByUserName(userName);
        if(professor == null) {
            return -1;
        }
        if(sectionRepository.findByCourseID(courseID).isEmpty()) {
            return -2;
        }
        ProfessorCourse assignment = new ProfessorCourse(professor.getUserID(), courseID, semester);
        if(professorCourseRepository.save(assignment) == null) {
            return -3;
        }
        return 0;
    }

    /**
     * Adds a relation between a student and a section
     * Student can then show up in the professor and TA panels for the course
     *
     * @param userName  Front-end identifier for the student being registered
     * @param sectionID Identifier for the section
     * @return          Error code
     */
    public int registerStudentToSection(@NonNull String userName, @NonNull String sectionID) {
        Student student = studentRepository.findByUserName(userName);
        if(student == null) {
            return -1;
        }
        Section section = sectionRepository.findBySectionID(sectionID);
        if(section == null) {
            return -2;
        }
        StudentSection assignment = new StudentSection(student.getUserID(), section.getSectionID());
        if(studentSectionRepository.save(assignment) == null) {
            return -3;
        }
        return 0;
    }

    /**
     * Changes a student account to a teaching assistant account
     * Student still has same view for most courses, but sees a TA view for courses that they are assigned to TA
     *
     * @param userName  Front-end identifier for student being hired
     * @return          Error code
     */
    public int hireStudentAsTeachingAssistant(@NonNull String userName) {
        Account account = accountRepository.findByUserName(userName);
        if(account == null) {
            return -1;
        }
        Student student = studentRepository.findByUserName(userName);
        if(student == null) {
            return -2;
        }
        if(teachingAssistantRepository.existsByUserName(userName)) {
            return -3;
        }
        TeachingAssistant teachingAssistant =
                new TeachingAssistant(account.getUserID(), account.getUserName(), account.getFirstName(),
                                        account.getLastName(), account.getMiddleInit(), account.getEduEmail());
        accountRepository.delete(account);
        studentRepository.delete(student);
        if(accountRepository.save(teachingAssistant) == null) {
            return -4;
        }
        return 0;
    }

    /**
     * Adds a relation between a teaching assistant and a courseID with semester
     * Teaching assistant will then show up in a list of TAs for the professor to further assign to sections
     *
     * @param userName  Front-end identifier for the teaching assistant being assigned
     * @param courseID  Identifier used by the university for a course
     * @param semester  Semester that assignment is effective during
     * @return          Error code
     */
    public int assignTeachingAssistantToCourse(@NonNull String userName, @NonNull String courseID, @NonNull String semester) {
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userName);
        if(teachingAssistant == null) {
            return -1;
        }
        if(sectionRepository.findByCourseID(courseID).isEmpty()) {
            return -2;
        }
        TeachingAssistantCourse assignment = new TeachingAssistantCourse(teachingAssistant.getUserID(), courseID, semester);
        teachingAssistantCourseRepository.save(assignment);
        return 0;
    }

    /**
     * @return  List of login information for all accounts in the database
     */
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * @return  List of all sections in the database
     */
    public List<Section> findAllSections() { return sectionRepository.findAll(); }

}