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
        return accountRepository.findByUserName(userName);
    }

    public Student retrieveStudent(String userName, String saltPass) {
        return studentRepository.findByUserName(userName);
    }

    public Account retrieveAccountByID(String userID) { return accountRepository.findByUserID(userID); }

    public TeachingAssistant retrieveTA(String userName, String saltPass) {
        return teachingAssistantRepository.findByUserName(userName);
    }

    public Professor retrieveProfessor(String userName, String saltPass) {
        return professorRepository.findByUserName(userName);
    }

    public CollegeAdmin retrieveAdmin(String userName, String saltPass) {
        return adminRepository.findByUserName(userName);
    }

    public List<Account> retrieveAllAccounts() {
        return accountRepository.findAll();
    }
}
