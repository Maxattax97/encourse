package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.domain.relations.*;
import edu.purdue.cs.encourse.service.AdminService;
import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public int addAccount(String userID, String userName, String saltPass, String firstName, String lastName,
                          String type, String middleInit, String eduEmail) {
        int result;
        switch(type) {
            case "Student": result = addStudent(userID, userName, saltPass, firstName, lastName, middleInit, eduEmail); break;
            case "TA": result = addTA(userID, userName, saltPass, firstName, lastName, middleInit, eduEmail); break;
            case "Professor": result = addProfessor(userID, userName, saltPass, firstName, lastName, middleInit, eduEmail); break;
            case "Admin": result = addAdmin(userID, userName, saltPass, firstName, lastName, middleInit, eduEmail); break;
            default: result = -1;
        }
        return result;
    }

    public int addStudent(String userID, String userName, String saltPass, String firstName, String lastName, String middleInit, String eduEmail) {
        Student student = new Student(userID, userName, saltPass, firstName, lastName, middleInit, eduEmail);
        if(accountRepository.existsByUserID(student.getUserID())) {
            return -2;
        }
        if(accountRepository.save(student) == null) {
            return -3;
        }
        if(studentRepository.save(student) == null) {
            return -4;
        }
        return 0;
    }

    public int addTA(String userID, String userName, String saltPass, String firstName, String lastName, String middleInit, String eduEmail) {
        TeachingAssistant teachingAssistant = new TeachingAssistant(userID, userName, saltPass, firstName, lastName, middleInit, eduEmail);
        if(accountRepository.existsByUserID(teachingAssistant.getUserID())) {
            return -5;
        }
        if(accountRepository.save(teachingAssistant) == null) {
            return -6;
        }
        if(studentRepository.save(teachingAssistant) == null) {
            return -7;
        }
        if(teachingAssistantRepository.save(teachingAssistant) == null) {
            return -8;
        }
        return 0;
    }

    public int addProfessor(String userID, String userName, String saltPass, String firstName, String lastName, String middleInit, String eduEmail) {
        Professor professor = new Professor(userID, userName, saltPass, firstName, lastName, middleInit, eduEmail);
        if(accountRepository.existsByUserID(professor.getUserID())) {
            return -9;
        }
        if(accountRepository.save(professor) == null) {
            return -10;
        }
        if(professorRepository.save(professor) == null) {
            return -11;
        }
        return 0;
    }

    public int addAdmin(String userID, String userName, String saltPass, String firstName, String lastName, String middleInit, String eduEmail) {
        CollegeAdmin admin = new CollegeAdmin(userID, userName, saltPass, firstName, lastName, middleInit, eduEmail);
        if(accountRepository.existsByUserID(admin.getUserID())) {
            return -12;
        }
        if(accountRepository.save(admin) == null) {
            return -13;
        }
        if(adminRepository.save(admin) == null) {
            return -14;
        }
        return 0;
    }

    public int modifyAccount(String userName, String field, String value) {
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
        if(accountRepository.save(account) == null) {
            return -2;
        }
        int result;
        switch(account.getRole()) {
            case Account.Roles.STUDENT: result = modifyStudent(account, field, value); break;
            case Account.Roles.TA: result = modifyStudent(account, field, value); break;
            case Account.Roles.PROFESSOR: result = modifyStudent(account, field, value); break;
            case Account.Roles.ADMIN: result = modifyStudent(account, field, value); break;
            default: result = -3;
        }
        return result;
    }

    public int modifyStudent(Account account, String field, String value){
        Student student = new Student(account);
        if(studentRepository.save(student) == null) {
            return -4;
        }
        return 0;
    }

    public int modifyTA(Account account, String field, String value) {
        TeachingAssistant teachingAssistant = new TeachingAssistant(account);
        if(studentRepository.save(teachingAssistant) == null) {
            return -5;
        }
        if(teachingAssistantRepository.save(teachingAssistant) == null) {
            return -6;
        }
        return 0;
    }

    public int modifyProfessor(Account account, String field, String value) {
        Professor professor = new Professor(account);
        if(professorRepository.save(professor) == null) {
            return -7;
        }
        return 0;
    }

    public int modifyAdmin(Account account, String field, String value) {
        CollegeAdmin admin = new CollegeAdmin(account);
        if(adminRepository.save(admin) == null) {
            return -8;
        }
        return 0;
    }

    public int addSection(String CRN, String semester, String courseID, String courseTitle, String sectionType) {
        Section section = new Section(CRN, semester, courseID, courseTitle, sectionType);
        if(sectionRepository.existsBySectionIdentifier(section.getSectionIdentifier())) {
            return -1;
        }
        if(sectionRepository.save(section) == null) {
            return -2;
        }
        return 0;
    }

    public int assignProfessorToCourse(String userName, String courseID, String semester) {
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

    public int registerStudentToSection(String userName, String courseID, String semester, String sectionType) {
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

    public int hireStudentAsTeachingAssistant(String userName) {
        Account account = accountRepository.findByUserName(userName);
        if(account == null) {
            return -1;
        }
        Student student = studentRepository.findByUserName(userName);
        if(student == null) {
            return -2;
        }
        account.setRole(Account.Roles.TA);
        student.setRole(Account.Roles.TA);
        TeachingAssistant teachingAssistant = new TeachingAssistant(account);
        if(accountRepository.save(account) == null) {
            return -3;
        }
        if(studentRepository.save(student) == null) {
            return -4;
        }
        if(teachingAssistantRepository.save(teachingAssistant) == null) {
            return -5;
        }
        return 0;
    }

}
