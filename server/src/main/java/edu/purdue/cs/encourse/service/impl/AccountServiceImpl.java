package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = AccountServiceImpl.NAME)
public class AccountServiceImpl implements AccountService {

    public final static String NAME = "AccountService";

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


    public Account retrieveAccount(String userName, String saltPass) {
        Account account = accountRepository.findByUserName(userName);
        account.setSaltPass(null);
        return account;
    }

    public Student retrieveStudent(String userName, String saltPass) {
        Student student = studentRepository.findByUserName(userName);
        student.setSaltPass(null);
        return student;
    }

    public Account retrieveAccountByID(String userID) { return accountRepository.findByUserID(userID); }

    public TeachingAssistant retrieveTA(String userName, String saltPass) {
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userName);
        teachingAssistant.setSaltPass(null);
        return teachingAssistant;
    }

    public Professor retrieveProfessor(String userName, String saltPass) {
        Professor professor = professorRepository.findByUserName(userName);
        professor.setSaltPass(null);
        return professor;
    }

    public CollegeAdmin retrieveAdmin(String userName, String saltPass) {
        CollegeAdmin collegeAdmin = adminRepository.findByUserName(userName);
        collegeAdmin.setSaltPass(null);
        return collegeAdmin;
    }

    public List<Account> retrieveAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        for (Account a: accounts) {
            a.setSaltPass(null);
        }
        return accounts;
    }
}
