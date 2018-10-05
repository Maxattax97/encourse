package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.domain.relations.*;
import edu.purdue.cs.encourse.service.AdminService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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

    public User addUser(@NonNull String userName, @NonNull String password, @NonNull String authority, boolean acc_expired, boolean locked, boolean cred_expired, boolean enabled) {
        Authority auth = authorityRepository.findDistinctByName(authority);
        if (auth == null) {
            auth = authorityRepository.findDistinctByName("STUDENT");
        }

        User user = new User();
        user.setUsername(userName);
        user.setPassword(password);
        user.setAccountExpired(acc_expired);
        user.setAccountLocked(locked);
        user.setCredentialsExpired(cred_expired);
        user.setEnabled(enabled);
        user.setAuthorities(Arrays.asList(auth));
        userRepository.save(user);
        user.setPassword(null);
        return user;
    }

    public boolean hasPermissionForStudent(User loggedIn, String userName) {
        Account account = studentRepository.findByUserName(userName);
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
            System.out.println("AUTHORITY: " + au);
            switch (au) {
                case "STUDENT":
                    if (sentRequest.getUserName().contentEquals(userName)) {
                        hasAuth = true;
                        break;
                    }
                    break;
                case "TA":
                    List<TeachingAssistantStudent> teachingAssistantStudents = teachingAssistantStudentRepository.findByIdTeachingAssistantID(sentRequest.getUserID());
                    if (teachingAssistantStudents.contains(account)) {
                        hasAuth = true;
                        break;
                    }
                    break;
                case "PROFESSOR":
                    System.out.println("PROFESSOR: " + sentRequest.getUserID());

                    List<StudentSection> studentSections = studentSectionRepository.findByIdStudentID(account.getUserID());
                    List<ProfessorCourse> professorCourses = professorCourseRepository.findByIdProfessorID(sentRequest.getUserID());
                    System.out.println(studentSections);
                    System.out.println(professorCourses);

                    for (StudentSection ss: studentSections) {
                        Section section = sectionRepository.findBySectionIdentifier(ss.getSectionIdentifier());
                        System.out.println("SECTION COURSE: " + section.getCourseID());
                        System.out.println("SECTION SEMESTER: " + section.getSemester());
                        for (ProfessorCourse pc: professorCourses) {
                            System.out.println("COURSE: " + pc.getCourseID());
                            System.out.println("SEMESTER: " + pc.getSemester());
                            if (pc.getCourseID().contentEquals(section.getCourseID()) && pc.getSemester().contentEquals(section.getSemester())) {
                                hasAuth = true;
                                break;
                            }
                        }
                    }
                    break;
                case "ADMIN":
                    System.out.println("ADMIN AUTHORITY");
                    hasAuth = true;
                    break;
            }
        }
        if (!hasAuth) {
            System.out.println("User " + loggedIn.getUsername() + " does not have authority over " + userName);
        }
        return hasAuth;
    }

    public int addAccount(@NonNull String userID, @NonNull String userName, @NonNull String firstName, @NonNull String lastName,
                          @NonNull String type, String middleInit, String eduEmail) {
        int result;
        switch(type) {
            case "Student": result = addStudent(userID, userName, firstName, lastName, middleInit, eduEmail); break;
            case "TA": result = addTA(userID, userName, firstName, lastName, middleInit, eduEmail); break;
            case "Professor": result = addProfessor(userID, userName, firstName, lastName, middleInit, eduEmail); break;
            case "Admin": result = addAdmin(userID, userName, firstName, lastName, middleInit, eduEmail); break;
            default: result = -1;
        }
        return result;
    }

    public int addStudent(@NonNull String userID, @NonNull String userName, @NonNull String firstName, @NonNull String lastName, String middleInit, String eduEmail) {
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

    public int addTA(@NonNull String userID, @NonNull String userName, @NonNull String firstName, @NonNull String lastName, String middleInit, String eduEmail) {
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

    public int addProfessor(@NonNull String userID, @NonNull String userName, @NonNull String firstName, @NonNull String lastName, String middleInit, String eduEmail) {
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

    public int addAdmin(@NonNull String userID, @NonNull String userName, @NonNull String firstName, @NonNull String lastName, String middleInit, String eduEmail) {
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

    public int deleteStudent(@NonNull Account account){
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

    public int deleteTA(@NonNull Account account) {
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserID(account.getUserID());
        teachingAssistant.copyAccount(account);
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantID(teachingAssistant.getUserID());
        for(TeachingAssistantStudent a : assignments) {
            teachingAssistantStudentRepository.delete(a);
        }
        teachingAssistantRepository.delete(teachingAssistant);
        return 0;
    }

    public int deleteProfessor(@NonNull Account account) {
        Professor professor = professorRepository.findByUserID(account.getUserID());
        professor.copyAccount(account);
        List<ProfessorCourse> assignments = professorCourseRepository.findByIdProfessorID(professor.getUserID());
        for(ProfessorCourse a : assignments) {
            professorCourseRepository.delete(a);
        }
        professorRepository.delete(professor);
        return 0;
    }

    public int deleteAdmin(@NonNull Account account) {
        CollegeAdmin admin = adminRepository.findByUserID(account.getUserID());
        admin.copyAccount(account);
        adminRepository.delete(admin);
        return 0;
    }

    public int modifyAccount(@NonNull String userName, @NonNull String field, String value) {
        Account account = accountRepository.findByUserName(userName);
        if(account == null) {
            return -1;
        }
        switch(field) {
            case "firstName": account.setFirstName(value); break;
            case "middleInitial": account.setMiddleInit(value); break;
            case "lastName": account.setLastName(value); break;
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

    public int modifyStudent(@NonNull Account account, @NonNull String field, String value){
        Student student = studentRepository.findByUserID(account.getUserID());
        student.copyAccount(account);
        if(accountRepository.save(student) == null) {
            return -4;
        }
        return 0;
    }

    public int modifyTA(@NonNull Account account, @NonNull String field, String value) {
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserID(account.getUserID());
        teachingAssistant.copyAccount(account);
        if(accountRepository.save(teachingAssistant) == null) {
            return -6;
        }
        return 0;
    }

    public int modifyProfessor(@NonNull Account account, @NonNull String field, String value) {
        Professor professor = professorRepository.findByUserID(account.getUserID());
        professor.copyAccount(account);
        if(accountRepository.save(professor) == null) {
            return -9;
        }
        return 0;
    }

    public int modifyAdmin(@NonNull Account account, @NonNull String field, String value) {
        CollegeAdmin admin = adminRepository.findByUserID(account.getUserID());
        admin.copyAccount(account);
        if(accountRepository.save(admin) == null) {
            return -11;
        }
        return 0;
    }

    public int addSection(@NonNull String CRN, @NonNull String semester, @NonNull String courseID, @NonNull String courseTitle, @NonNull String sectionType) {
        System.out.println("ADDING SECTION");
        Section section = new Section(CRN, semester, courseID, courseTitle, sectionType);
        if(sectionRepository.existsBySectionIdentifier(section.getSectionIdentifier())) {
            return -1;
        }
        System.out.println("SECTION DOES NOT EXIST YET");
        if(sectionRepository.save(section) == null) {
            return -2;
        }
        System.out.println("SECTION ADDED");
        return 0;
    }

    public int deleteSection(@NonNull String sectionID) {
        System.out.println("ADDING SECTION");
        Section section = sectionRepository.findBySectionIdentifier(sectionID);
        if(section == null) {
            return -1;
        }
        List<StudentSection> sections = studentSectionRepository.findByIdSectionIdentifier(sectionID);
        for(StudentSection s : sections) {
            studentSectionRepository.delete(s);
        }
        sectionRepository.delete(section);
        return 0;
    }

    public int assignProfessorToCourse(@NonNull String userName, @NonNull String courseID, @NonNull String semester) {
        System.out.println("INSIDE ASSIGNMENTS");
        Professor professor = professorRepository.findByUserName(userName);
        if(professor == null) {
            return -1;
        }
        System.out.println("PROFESSOR NOT NULL");
        if(sectionRepository.findByCourseID(courseID).isEmpty()) {
            return -2;
        }
        System.out.println("SECTION IS NOT EMPTY");
        ProfessorCourse assignment = new ProfessorCourse(professor.getUserID(), courseID, semester);
        System.out.println("ASSIGNMENT: " + assignment);
        if(professorCourseRepository.save(assignment) == null) {
            return -3;
        }
        return 0;
    }

    public int registerStudentToSection(@NonNull String userName, @NonNull String courseID, @NonNull String semester, @NonNull String sectionType) {
        Student student = studentRepository.findByUserName(userName);
        if(student == null) {
            return -1;
        }
        Section section = sectionRepository.findBySectionIdentifier(Section.createSectionID(courseID, semester, sectionType));
        if(section == null) {
            return -2;
        }
        StudentSection assignment = new StudentSection(student.getUserID(), section.getSectionIdentifier());
        if(studentSectionRepository.save(assignment) == null) {
            return -3;
        }
        return 0;
    }

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

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

}