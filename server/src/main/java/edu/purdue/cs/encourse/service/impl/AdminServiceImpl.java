package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.service.AdminService;
import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service("adminService")
public class AdminServiceImpl implements AdminService {

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
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    public void addUser(String userName, String password, boolean acc_expired, boolean locked, boolean cred_expired, boolean enabled) {
        User user = new User();
        user.setUsername(userName);
        user.setPassword(password);
        user.setAccountExpired(acc_expired);
        user.setAccountLocked(locked);
        user.setCredentialsExpired(cred_expired);
        user.setEnabled(enabled);
        user.setAuthorities(authorityRepository.findAll());
        userRepository.save(user);
    }

    public int addAccount(String userID, String userName, String firstName, String lastName,
                          int type, String middleInit, String eduEmail) {
        int result;
        switch(type) {
            case Account.Roles.STUDENT: result = addStudent(userID, userName, firstName, lastName, middleInit, eduEmail); break;
            case Account.Roles.TA: result = addTA(userID, userName, firstName, lastName, middleInit, eduEmail); break;
            case Account.Roles.PROFESSOR: result = addProfessor(userID, userName, firstName, lastName, middleInit, eduEmail); break;
            case Account.Roles.ADMIN: result = addAdmin(userID, userName, firstName, lastName, middleInit, eduEmail); break;
            default: result = -1;
        }
        return result;
    }

    public int addAccount(Account a) {
        return addAccount(a.getUserID(), a.getUserName(), a.getFirstName(), a.getLastName(), a.getRole(), a.getMiddleInit(), a.getEduEmail());
    }

    public int addStudent(String userID, String userName, String firstName, String lastName, String middleInit, String eduEmail) {
        Student student = new Student(userID, userName, firstName, lastName, middleInit, eduEmail);
        if(accountRepository.existsByUserID(student.getUserID())) {
            return -2;
        }
        accountRepository.save(student);
        studentRepository.save(student);
        return 0;
    }

    public int addTA(String userID, String userName, String firstName, String lastName, String middleInit, String eduEmail) {
        TeachingAssistant teachingAssistant = new TeachingAssistant(userID, userName, firstName, lastName, middleInit, eduEmail);
        if(accountRepository.existsByUserID(teachingAssistant.getUserID())) {
            return -5;
        }
        accountRepository.save(teachingAssistant);
        studentRepository.save(teachingAssistant);
        teachingAssistantRepository.save(teachingAssistant);
        return 0;
    }

    public int addProfessor(String userID, String userName, String firstName, String lastName, String middleInit, String eduEmail) {
        Professor professor = new Professor(userID, userName, firstName, lastName, middleInit, eduEmail);
        if(accountRepository.existsByUserID(professor.getUserID())) {
            return -9;
        }
        accountRepository.save(professor);
        professorRepository.save(professor);
        return 0;
    }

    public int addAdmin(String userID, String userName, String firstName, String lastName, String middleInit, String eduEmail) {
        CollegeAdmin admin = new CollegeAdmin(userID, userName, firstName, lastName, middleInit, eduEmail);
        if(accountRepository.existsByUserID(admin.getUserID())) {
            return -12;
        }
        accountRepository.save(admin);
        adminRepository.save(admin);
        return 0;
    }

    public int modifyAccount(String userID, String field, String value) {
        Account account = accountRepository.findByUserID(userID);
        if(account == null) {
            return -1;
        }
        switch(field) {
            case "firstName": account.setFirstName(value); break;
            case "middleInitial": account.setMiddleInit(value); break;
            case "lastName": account.setLastName(value); break;
            default: break;
        }
        accountRepository.save(account);

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
        studentRepository.save(student);
        return 0;
    }

    public int modifyTA(Account account, String field, String value) {
        TeachingAssistant teachingAssistant = new TeachingAssistant(account);
        studentRepository.save(teachingAssistant);
        return 0;
    }

    public int modifyProfessor(Account account, String field, String value) {
        Professor professor = new Professor(account);
        professorRepository.save(professor);
        return 0;
    }

    public int modifyAdmin(Account account, String field, String value) {
        CollegeAdmin admin = new CollegeAdmin(account);
        adminRepository.save(admin);
        return 0;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

}
