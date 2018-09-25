package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("accountService")
public class AccountServiceImpl implements AccountService {
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

    public Account retrieveAccount(String userName) {
        return accountRepository.findByUserName(userName);
    }

    public Account retrieveAccountByID(String userID) { return accountRepository.findByUserID(userID); }

    public Student retrieveStudent(String userName) {
        return studentRepository.findByUserName(userName);
    }

    public TeachingAssistant retrieveTA(String userName) {
        return teachingAssistantRepository.findByUserName(userName);
    }

    public Professor retrieveProfessor(String userName) {
        return professorRepository.findByUserName(userName);
    }

    public CollegeAdmin retrieveAdmin(String userName) {
        return adminRepository.findByUserName(userName);
    }

    public List<Account> retrieveAllAccounts() { return accountRepository.findAll(); }
}
